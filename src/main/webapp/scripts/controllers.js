'use strict';

/* Controllers */

respondecoApp.controller('MainController', function ($scope, $location) {
    $scope.main = function() {
        return $location.path() === '' || $location.path() === '/';
    };

    $scope.redirectToProjectSearch = function() {
        $location.path("projects");
    };

    $scope.redirectToNewProject = function() {
        $location.path("projects/edit/new");
    };
});

respondecoApp.controller('AdminController', function ($scope) {
});

respondecoApp.controller('LanguageController', function ($scope, $translate, LanguageService) {
    $scope.changeLanguage = function (languageKey) {
        $translate.use(languageKey);

        LanguageService.getBy(languageKey).then(function (languages) {
            $scope.languages = languages;
        });
    };

    LanguageService.getBy().then(function (languages) {
        $scope.languages = languages;
    });
});

respondecoApp.controller('MenuController', function ($scope) {
});

respondecoApp.controller('LoginController', function ($scope, $location, AuthenticationSharedService) {
    $scope.rememberMe = true;
    $scope.login = function () {
        AuthenticationSharedService.login({
            username: $scope.username,
            password: $scope.password,
            rememberMe: $scope.rememberMe
        });
    }
});

respondecoApp.controller('LogoutController', function ($location, AuthenticationSharedService) {
    AuthenticationSharedService.logout();
});

respondecoApp.controller('SettingsController', function ($scope, Account, AuthenticationSharedService, OrgJoinRequest, Organization) {
    $scope.onComplete = function(fileItem, response) {
        $scope.settingsAccount.profilePicture = response;

        $scope.save();
    };

    $scope.success = null;
    $scope.error = null;

    $scope.settingsAccount = {};
    Account.get(function(account) {
        $scope.settingsAccount = account;

        if ($scope.settingsAccount.organization !== undefined) {
            $scope.organization = $scope.settingsAccount.organization;
        }

        if($scope.settingsAccount.firstName == null) {
            $scope.fullName = $scope.settingsAccount.lastName;
        } else if($scope.settingsAccount.lastName == null) {
            $scope.fullName = $scope.settingsAccount.firstName;
        } else {
            $scope.fullName = $scope.settingsAccount.firstName + " " + $scope.settingsAccount.lastName;
        }

        if (account.profilePicture != null) {
            $scope.profilePicture = account.profilePicture.id;
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

    getCurrentOrgJoinRequests();

    $scope.save = function () {
        Account.save($scope.settingsAccount,
            function (value, responseHeaders) {
                $scope.error = null;
                $scope.success = 'OK';
                $scope.settingsAccount = Account.get();
            },
            function (httpResponse) {
                $scope.success = null;
                $scope.error = "ERROR";
            });
    };
     $scope.delete = function() {
        Account.delete(
            function (value, responseHeaders) {
                $scope.error = null;
                $scope.accountdeleted = "OK";
                AuthenticationSharedService.logout();
            },
            function (httpResponse) {
                $scope.accountdeleted = null;
                $scope.error = "ERROR";
            });
    };

    $scope.acceptInvitation = function(id) {
        OrgJoinRequest.accept({
            id: id
        }, getCurrentOrgJoinRequests);
    };

    $scope.declineInvitation = function(id) {
        OrgJoinRequest.decline({
            id: id
        }, getCurrentOrgJoinRequests);
    };

    $scope.edit = {
        image: true,
        account: false
    };
});



respondecoApp.controller('RegisterController', function ($scope, $translate, Register) {
    $scope.success = null;
    $scope.error = null;
    $scope.doNotMatch = null;
    $scope.errorUserExists = null;
    $scope.register = function () {
        if ($scope.registerAccount.password != $scope.confirmPassword) {
            $scope.doNotMatch = "ERROR";
        } else {
            $scope.registerAccount.langKey = $translate.use();
            $scope.doNotMatch = null;
            Register.save($scope.registerAccount,
                function (value, responseHeaders) {
                    $scope.error = null;
                    $scope.errorUserExists = null;
                    $scope.success = 'OK';
                },
                function (httpResponse) {
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

respondecoApp.controller('ActivationController', function ($scope, $routeParams, Activate) {
    Activate.get({key: $routeParams.key},
        function (value, responseHeaders) {
            $scope.error = null;
            $scope.success = 'OK';
        },
        function (httpResponse) {
            $scope.success = null;
            $scope.error = "ERROR";
        });
});

respondecoApp.controller('PasswordController', function ($scope, Password) {
    $scope.success = null;
    $scope.error = null;
    $scope.doNotMatch = null;
    $scope.changePassword = function () {
        if ($scope.password != $scope.confirmPassword) {
            $scope.doNotMatch = "ERROR";
        } else {
            $scope.doNotMatch = null;
            Password.save($scope.password,
                function (value, responseHeaders) {
                    $scope.error = null;
                    $scope.success = 'OK';
                },
                function (httpResponse) {
                    $scope.success = null;
                    $scope.error = "ERROR";
                });
        }
    };
});

respondecoApp.controller('SessionsController', function ($scope, resolvedSessions, Sessions) {
    $scope.success = null;
    $scope.error = null;
    $scope.sessions = resolvedSessions;
    $scope.invalidate = function (series) {
        Sessions.delete({series: encodeURIComponent(series)},
            function (value, responseHeaders) {
                $scope.error = null;
                $scope.success = "OK";
                $scope.sessions = Sessions.get();
            },
            function (httpResponse) {
                $scope.success = null;
                $scope.error = "ERROR";
            });
    };
});
