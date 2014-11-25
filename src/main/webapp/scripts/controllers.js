'use strict';

/* Controllers */

respondecoApp.controller('MainController', function ($scope, Account, OrgJoinRequest, Organization) {
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
    $scope.success = null;
    $scope.error = null;

    $scope.settingsAccount = {};
    $scope.profilePicture = "images/profile_empty.png";
    Account.get().$promise.then(function(account) {
        $scope.settingsAccount = account;

        if ($scope.settingsAccount.orgId !== null) {
            Organization.getById({
                id: $scope.settingsAccount.orgId
            }).$promise.then(function(organization) {
                $scope.organization = organization;
            });
        }

        if($scope.settingsAccount.firstName == null) {
            $scope.fullName = $scope.settingsAccount.lastName;
        } else if($scope.settingsAccount.lastName == null) {
            $scope.fullName = $scope.settingsAccount.firstName;
        } else {
            $scope.fullName = $scope.settingsAccount.firstName + " " + $scope.settingsAccount.lastName;
        }
        $scope.profilePicture = "images/profile_empty.png";
    });

    $scope.success = null;
    $scope.error = null;
    $scope.gender = [
        "UNSPECIFIED",
        "MALE",
        "FEMALE"
    ];
    
    var getCurrentOrgJoinRequests = function() {
        OrgJoinRequest.getCurrent().$promise.then(function(data) {
            $scope.orgJoinRequests = data;

            data.forEach(function(el) {
                Organization.getById({
                    id: el.orgId
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
        image: false,
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

respondecoApp.controller('HealthController', function ($scope, HealthCheckService) {
    $scope.updatingHealth = true;

    $scope.refresh = function () {
        $scope.updatingHealth = true;
        HealthCheckService.check().then(function (promise) {
            $scope.healthCheck = promise;
            $scope.updatingHealth = false;
        }, function (promise) {
            $scope.healthCheck = promise.data;
            $scope.updatingHealth = false;
        });
    }

    $scope.refresh();

    $scope.getLabelClass = function (statusState) {
        if (statusState == 'UP') {
            return "label-success";
        } else {
            return "label-danger";
        }
    }
});

respondecoApp.controller('MetricsController', function ($scope, MetricsService, HealthCheckService, ThreadDumpService) {
    $scope.metrics = {};
    $scope.updatingMetrics = true;

    $scope.refresh = function () {
        $scope.updatingMetrics = true;
        MetricsService.get().then(function (promise) {
            $scope.metrics = promise;
            $scope.updatingMetrics = false;
        }, function (promise) {
            $scope.metrics = promise.data;
            $scope.updatingMetrics = false;
        });
    };

    $scope.$watch('metrics', function (newValue, oldValue) {
        $scope.servicesStats = {};
        $scope.cachesStats = {};
        angular.forEach(newValue.timers, function (value, key) {
            if (key.indexOf("web.rest") != -1 || key.indexOf("service") != -1) {
                $scope.servicesStats[key] = value;
            }

            if (key.indexOf("net.sf.ehcache.Cache") != -1) {
                // remove gets or puts
                var index = key.lastIndexOf(".");
                var newKey = key.substr(0, index);

                // Keep the name of the domain
                index = newKey.lastIndexOf(".");
                $scope.cachesStats[newKey] = {
                    'name': newKey.substr(index + 1),
                    'value': value
                };
            }
            ;
        });
    });

    $scope.refresh();

    $scope.threadDump = function () {
        ThreadDumpService.dump().then(function (data) {
            $scope.threadDump = data;

            $scope.threadDumpRunnable = 0;
            $scope.threadDumpWaiting = 0;
            $scope.threadDumpTimedWaiting = 0;
            $scope.threadDumpBlocked = 0;

            angular.forEach(data, function (value, key) {
                if (value.threadState == 'RUNNABLE') {
                    $scope.threadDumpRunnable += 1;
                } else if (value.threadState == 'WAITING') {
                    $scope.threadDumpWaiting += 1;
                } else if (value.threadState == 'TIMED_WAITING') {
                    $scope.threadDumpTimedWaiting += 1;
                } else if (value.threadState == 'BLOCKED') {
                    $scope.threadDumpBlocked += 1;
                }
            });

            $scope.threadDumpAll = $scope.threadDumpRunnable + $scope.threadDumpWaiting +
            $scope.threadDumpTimedWaiting + $scope.threadDumpBlocked;

        });
    };

    $scope.getLabelClass = function (threadState) {
        if (threadState == 'RUNNABLE') {
            return "label-success";
        } else if (threadState == 'WAITING') {
            return "label-info";
        } else if (threadState == 'TIMED_WAITING') {
            return "label-warning";
        } else if (threadState == 'BLOCKED') {
            return "label-danger";
        }
    };
});

respondecoApp.controller('LogsController', function ($scope, resolvedLogs, LogsService) {
    $scope.loggers = resolvedLogs;

    $scope.changeLevel = function (name, level) {
        LogsService.changeLevel({name: name, level: level}, function () {
            $scope.loggers = LogsService.findAll();
        });
    }
});

respondecoApp.controller('AuditsController', function ($scope, $translate, $filter, AuditsService) {
    $scope.onChangeDate = function () {
        AuditsService.findByDates($scope.fromDate, $scope.toDate).then(function (data) {
            $scope.audits = data;
        });
    };

    // Date picker configuration
    $scope.today = function () {
        // Today + 1 day - needed if the current day must be included
        var today = new Date();
        var tomorrow = new Date(today.getFullYear(), today.getMonth(), today.getDate() + 1); // create new increased date

        $scope.toDate = $filter('date')(tomorrow, "yyyy-MM-dd");
    };

    $scope.previousMonth = function () {
        var fromDate = new Date();
        if (fromDate.getMonth() == 0) {
            fromDate = new Date(fromDate.getFullYear() - 1, 0, fromDate.getDate());
        } else {
            fromDate = new Date(fromDate.getFullYear(), fromDate.getMonth() - 1, fromDate.getDate());
        }

        $scope.fromDate = $filter('date')(fromDate, "yyyy-MM-dd");
    };

    $scope.today();
    $scope.previousMonth();

    AuditsService.findByDates($scope.fromDate, $scope.toDate).then(function (data) {
        $scope.audits = data;
    });
});
