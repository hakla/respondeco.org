'use strict';

/* Controllers */

respondecoApp.controller('MainController', function($scope, $location, $rootScope) {
    $scope.main = function() {
        return $location.path() === '' || $location.path() === '/';
    };

    $scope.redirectToProjectSearch = function() {
        $location.path("projects");
    };

    $scope.redirectToNewProject = function() {
        $location.path("projects/edit/new");
    };

    $rootScope.globalAlerts = [];

    $rootScope.closeAlert = function(index) {
        $rootScope.globalAlerts.splice(index, 1);
    };

    $scope.redirectToOwnOrganization = function() {
        var org = 'edit/new';

        if ($rootScope._account != null && $rootScope._account.organization != null) {
            org = $rootScope._account.organization.id;
        }

        $location.path("/organization/" + org);
    };

    $scope.isOrganizationUser = function()  {
        var account = $rootScope._account;

        if (account.invited) {
            $rootScope.username = account.login;
        } else {
            $rootScope.username = account.organization.name;
        }

        return $rootScope._account.invited === false;
    };
});

respondecoApp.controller('AdminController', function($scope) {});

respondecoApp.controller('LanguageController', function($scope, $translate, LanguageService, $window, $rootScope) {
    $scope.changeLanguage = function(languageKey) {
        changeLocale(languageKey);
        LanguageService.getBy(languageKey).then(function(languages) {
            $scope.languages = languages;
        });
    };

    var changeLocale = function(languageKey) {
        $translate.use(languageKey);
        $window.moment.locale(languageKey);
        $rootScope.$broadcast('amMoment:localeChanged');
    }

    LanguageService.getBy().then(function(languages) {
        $scope.languages = languages;

        changeLocale($translate.use());
    });
});

respondecoApp.controller('MenuController', function($rootScope, TextMessage) {
    var refresh = function() {
        TextMessage.countNewMessages(function(amount) {
            $rootScope.newMessages = amount[0];
        });
    };

    refresh();

    // refresh the messages on each route change
    $rootScope.$on('$routeChangeStart', function(event, next) {
        refresh();
    });
});

respondecoApp.controller('LoginController', function($scope, $location, AuthenticationSharedService) {
    $scope.rememberMe = true;
    $scope.login = function() {
        AuthenticationSharedService.login({
            username: $scope.username,
            password: $scope.password,
            rememberMe: $scope.rememberMe
        });
    }

    $scope.$on("")
});

respondecoApp.controller('LogoutController', function($location, AuthenticationSharedService) {
    AuthenticationSharedService.logout();
});

respondecoApp.controller('SettingsController', function($scope, $location, Account, AuthenticationSharedService,
                                                        OrgJoinRequest, Organization) {
    $scope.onComplete = function(fileItem, response) {
        $scope.settingsAccount.profilePicture = response;
        $scope.profilePicture = response.id;

        $scope.save();
    };

    $scope.success = null;
    $scope.error = null;
    $scope.fullName = null;

    $scope.settingsAccount = {};
    Account.get(function(account) {
        //account is an organization account, redirect to organization page
        if(account.id === account.organization.ownerId) {
            // $location.path("/organization/" + account.organization.id);
        } else {
            $scope.settingsAccount = account;
            $scope.organization = account.organization;

            if ($scope.settingsAccount.organizationId != null) {
                reloadOrganization($scope.settingsAccount.organizationId);
            }

            if ($scope.settingsAccount.firstName == null) {
                $scope.fullName = $scope.settingsAccount.lastName;
            } else if ($scope.settingsAccount.lastName == null) {
                $scope.fullName = $scope.settingsAccount.firstName;
            } else {
                $scope.fullName = $scope.settingsAccount.firstName + " " + $scope.settingsAccount.lastName;
            }

            if (account.profilePicture != null) {
                $scope.profilePicture = account.profilePicture.id;
            }
        }
    });

    $scope.success = null;
    $scope.error = null;
    $scope.gender = [
        "UNSPECIFIED",
        "MALE",
        "FEMALE"
    ];

    var getCurrentOrgJoinRequests = function() {
        OrgJoinRequest.current(function(data) {
            $scope.orgJoinRequests = data;

            data.forEach(function(el) {
                Organization.get({
                    id: el.organization.id
                }, function(data) {
                    el.organization = data;
                });
            });
        });
    };

    var reloadOrganization = function(id) {
        if (id != null) {
            $scope.organization = Organization.get({
                id: id
            });
        } else {
            Account.get(function(account) {
                if (account.organizationId != null) {
                    reloadOrganization(account.organizationId);
                } else {
                    $scope.organization = null;
                }
            });
        }
    };

    getCurrentOrgJoinRequests();

    $scope.save = function() {
        Account.save($scope.settingsAccount,
            function(value, responseHeaders) {
                $scope.error = null;
                $scope.success = 'OK';
                // $scope.settingsAccount = Account.get();
                if ($scope.settingsAccount.firstName == null) {
                    $scope.fullName = $scope.settingsAccount.lastName;
                } else if ($scope.settingsAccount.lastName == null) {
                    $scope.fullName = $scope.settingsAccount.firstName;
                } else {
                    $scope.fullName = $scope.settingsAccount.firstName + " " + $scope.settingsAccount.lastName;
                }
            },
            function(httpResponse) {
                $scope.success = null;
                $scope.error = "ERROR";
            });
    };
    $scope.delete = function() {
        Account.delete(
            function(value, responseHeaders) {
                $scope.error = null;
                $scope.accountdeleted = "OK";
                AuthenticationSharedService.logout();
            },
            function(httpResponse) {
                $scope.accountdeleted = null;
                $scope.error = "ERROR";
            });
    };

    $scope.acceptInvitation = function(id) {
        OrgJoinRequest.accept({
            id: id
        }, function() {
            getCurrentOrgJoinRequests();
            reloadOrganization();
        });
    };

    $scope.declineInvitation = function(id) {
        OrgJoinRequest.decline({
            id: id
        }, getCurrentOrgJoinRequests);
    };

    $scope.leaveOrganization = function() {
        Account.leaveOrganization(function() {
            reloadOrganization();
        });
    };

    $scope.edit = {
        image: true,
        account: false
    };
});



respondecoApp.controller('RegisterController', function($scope, $translate, Register, $location, $routeParams) {
    $scope.activation = false;
    $scope.success = null;
    $scope.error = null;
    $scope.doNotMatch = null;
    $scope.errorUserExists = null;
    $scope.registerAccount = {
        orgname: null,
        npo:null,
        email: null,
        password: null,
        langKey: "de"
    };

    if ($location.path() === '/activateInvitation') {
        $scope.activation = true;
        $scope.registerAccount.email = $routeParams.email;
    }

    $scope.register = function() {
        if ($scope.registerAccount.password != $scope.confirmPassword) {
            $scope.doNotMatch = "ERROR";
        } else {
            $scope.registerAccount.langKey = $translate.use();
            $scope.doNotMatch = null;
            Register.save($scope.registerAccount,
                function(value, responseHeaders) {
                    $scope.error = null;
                    $scope.errorUserExists = null;
                    $scope.success = 'OK';
                },
                function(httpResponse) {
                    $scope.success = null;
                    if (httpResponse.status === 304 &&
                        httpResponse.data.error && httpResponse.data.error === "Not Modified") {
                        $scope.error = null;
                        $scope.errorUserExists = "ERROR";
                    } else {
                        $scope.error = "ERROR";
                        $scope.errorUserExists = null;
                    }
                });
        }
    }
});

respondecoApp.controller('ActivationController', function($scope, $routeParams, Activate) {
    Activate.get({
            key: $routeParams.key
        },
        function(value, responseHeaders) {
            $scope.error = null;
            $scope.success = 'OK';
        },
        function(httpResponse) {
            $scope.success = null;
            $scope.error = "ERROR";
        });
});

respondecoApp.controller('PasswordController', function($scope, Password) {
    $scope.success = null;
    $scope.error = null;
    $scope.doNotMatch = null;
    $scope.changePassword = function() {
        if ($scope.password != $scope.confirmPassword) {
            $scope.doNotMatch = "ERROR";
        } else {
            $scope.doNotMatch = null;
            Password.save($scope.password,
                function(value, responseHeaders) {
                    $scope.error = null;
                    $scope.success = 'OK';
                },
                function(httpResponse) {
                    $scope.success = null;
                    $scope.error = "ERROR";
                });
        }
    };
});

respondecoApp.controller('SessionsController', function($scope, resolvedSessions, Sessions) {
    $scope.success = null;
    $scope.error = null;
    $scope.sessions = resolvedSessions;
    $scope.invalidate = function(series) {
        Sessions.delete({
                series: encodeURIComponent(series)
            },
            function(value, responseHeaders) {
                $scope.error = null;
                $scope.success = "OK";
                $scope.sessions = Sessions.get();
            },
            function(httpResponse) {
                $scope.success = null;
                $scope.error = "ERROR";
            });
    };
});
