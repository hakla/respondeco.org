(function() {
  'use strict';

  /* Controllers */
  respondecoApp.controller('MainController', function($scope, $location, $rootScope) {
    var linkToOrganization;
    $scope.main = function() {
      return $location.path() === '' || $location.path() === '/';
    };
    $scope.redirectToProjectSearch = function() {
      return $location.path('projects');
    };
    $scope.redirectToNewProject = function() {
      return $location.path('projects/edit/new');
    };
    $rootScope.globalAlerts = [];
    $rootScope.closeAlert = function(index) {
      return $rootScope.globalAlerts.splice(index, 1);
    };
    linkToOrganization = function() {
      if ($rootScope._account !== null && $rootScope._account.organization !== null) {
        return "/organization/" + $rootScope._account.organization.id;
      } else {
        return 'edit/new';
      }
    };
    $scope.redirectToOwnOrganization = function() {
      return $location.path(linkToOrganization());
    };
    $scope.redirectToOwnProjects = function() {
      return (linkToOrganization()) + "/projects";
    };
    $scope.isOrganizationUser = function() {
      var account;
      account = $rootScope._account;
      if (account.invited || !account.organization) {
        $rootScope.username = account.login;
      } else {
        $rootScope.username = account.organization.name;
      }
      return $rootScope._account.invited === false;
    };
    $scope.isActive = function(viewLocation) {
      return viewLocation === $location.path();
    };
    return $rootScope.title = 'Organization';
  });

  respondecoApp.controller('AdminController', function($scope) {});

  respondecoApp.controller('LanguageController', function($scope, $translate, LanguageService, $window, $rootScope) {
    var changeLocale;
    $scope.changeLanguage = function(languageKey) {
      changeLocale(languageKey);
      return LanguageService.getBy(languageKey).then(function(languages) {
        return $scope.languages = languages;
      });
    };
    changeLocale = function(languageKey) {
      $translate.use(languageKey);
      $window.moment.locale(languageKey);
      return $rootScope.$broadcast('amMoment:localeChanged');
    };
    return LanguageService.getBy().then(function(languages) {
      $scope.languages = languages;
      return changeLocale($translate.use());
    });
  });

  respondecoApp.controller('MenuController', function($rootScope, TextMessage) {
    var refresh;
    refresh = function() {
      return TextMessage.countNewMessages(function(amount) {
        return $rootScope.newMessages = amount[0];
      });
    };
    refresh();
    return $rootScope.$on('$routeChangeStart', function(event, next) {
      return refresh();
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
    };
    return $scope.$on('');
  });

  respondecoApp.controller('LogoutController', function($location, AuthenticationSharedService) {
    return AuthenticationSharedService.logout();
  });

  respondecoApp.controller('SettingsController', function($scope, $location, Account, AuthenticationSharedService, OrgJoinRequest, Organization) {
    var getCurrentOrgJoinRequests, reloadOrganization;
    $scope.onComplete = function(fileItem, response) {
      $scope.settingsAccount.profilePicture = response;
      $scope.profilePicture = response.id;
      return $scope.save();
    };
    $scope.success = null;
    $scope.error = null;
    $scope.fullName = null;
    $scope.settingsAccount = {};
    Account.get(function(account) {
      if (account.id === account.organization.ownerId) {

      } else {
        $scope.settingsAccount = account;
        $scope.organization = account.organization;
        if ($scope.settingsAccount.organizationId !== null) {
          reloadOrganization($scope.settingsAccount.organizationId);
        }
        if ($scope.settingsAccount.firstName === null) {
          $scope.fullName = $scope.settingsAccount.lastName;
        } else if ($scope.settingsAccount.lastName === null) {
          $scope.fullName = $scope.settingsAccount.firstName;
        } else {
          $scope.fullName = $scope.settingsAccount.firstName + ' ' + $scope.settingsAccount.lastName;
        }
        if (account.profilePicture !== null) {
          return $scope.profilePicture = account.profilePicture.id;
        }
      }
    });
    $scope.success = null;
    $scope.error = null;
    $scope.gender = ['UNSPECIFIED', 'MALE', 'FEMALE'];
    getCurrentOrgJoinRequests = function() {
      return OrgJoinRequest.current(function(data) {
        $scope.orgJoinRequests = data;
        return data.forEach(function(el) {
          return Organization.get({
            id: el.organization.id
          }, function(data) {
            return el.organization = data;
          });
        });
      });
    };
    reloadOrganization = function(id) {
      if (id !== null) {
        return $scope.organization = Organization.get({
          id: id
        });
      } else {
        return Account.get(function(account) {
          if (account.organizationId !== null) {
            return reloadOrganization(account.organizationId);
          } else {
            return $scope.organization = null;
          }
        });
      }
    };
    getCurrentOrgJoinRequests();
    $scope.save = function() {
      return Account.save($scope.settingsAccount, (function(value, responseHeaders) {
        $scope.error = null;
        $scope.success = 'OK';
        if ($scope.settingsAccount.firstName === null) {
          return $scope.fullName = $scope.settingsAccount.lastName;
        } else if ($scope.settingsAccount.lastName === null) {
          return $scope.fullName = $scope.settingsAccount.firstName;
        } else {
          return $scope.fullName = $scope.settingsAccount.firstName + ' ' + $scope.settingsAccount.lastName;
        }
      }), function(httpResponse) {
        $scope.success = null;
        return $scope.error = 'ERROR';
      });
    };
    $scope["delete"] = function() {
      return Account["delete"]((function(value, responseHeaders) {
        $scope.error = null;
        $scope.accountdeleted = 'OK';
        return AuthenticationSharedService.logout();
      }), function(httpResponse) {
        $scope.accountdeleted = null;
        return $scope.error = 'ERROR';
      });
    };
    $scope.acceptInvitation = function(id) {
      return OrgJoinRequest.accept({
        id: id
      }, function() {
        getCurrentOrgJoinRequests();
        return reloadOrganization();
      });
    };
    $scope.declineInvitation = function(id) {
      return OrgJoinRequest.decline({
        id: id
      }, getCurrentOrgJoinRequests);
    };
    $scope.leaveOrganization = function() {
      return Account.leaveOrganization(function() {
        return reloadOrganization();
      });
    };
    return $scope.edit = {
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
      npo: null,
      email: null,
      password: null,
      langKey: 'de'
    };
    if ($location.path() === '/activateInvitation') {
      $scope.activation = true;
      $scope.registerAccount.email = $routeParams.email;
    }
    return $scope.register = function() {
      if ($scope.registerAccount.password !== $scope.confirmPassword) {
        return $scope.doNotMatch = 'ERROR';
      } else {
        $scope.registerAccount.langKey = $translate.use();
        $scope.doNotMatch = null;
        return Register.save($scope.registerAccount, (function(value, responseHeaders) {
          $scope.error = null;
          $scope.errorUserExists = null;
          return $scope.success = 'OK';
        }), function(httpResponse) {
          $scope.success = null;
          if (httpResponse.status === 304 && httpResponse.data.error && httpResponse.data.error === 'Not Modified') {
            $scope.error = null;
            return $scope.errorUserExists = 'ERROR';
          } else {
            $scope.error = 'ERROR';
            return $scope.errorUserExists = null;
          }
        });
      }
    };
  });

  respondecoApp.controller('ActivationController', function($scope, $routeParams, Activate) {
    return Activate.get({
      key: $routeParams.key
    }, (function(value, responseHeaders) {
      $scope.error = null;
      return $scope.success = 'OK';
    }), function(httpResponse) {
      $scope.success = null;
      return $scope.error = 'ERROR';
    });
  });

  respondecoApp.controller('PasswordController', function($scope, Password) {
    $scope.success = null;
    $scope.error = null;
    $scope.doNotMatch = null;
    return $scope.changePassword = function() {
      if ($scope.password !== $scope.confirmPassword) {
        return $scope.doNotMatch = 'ERROR';
      } else {
        $scope.doNotMatch = null;
        return Password.save($scope.password, (function(value, responseHeaders) {
          $scope.error = null;
          return $scope.success = 'OK';
        }), function(httpResponse) {
          $scope.success = null;
          return $scope.error = 'ERROR';
        });
      }
    };
  });

  respondecoApp.controller('SessionsController', function($scope, resolvedSessions, Sessions) {
    $scope.success = null;
    $scope.error = null;
    $scope.sessions = resolvedSessions;
    return $scope.invalidate = function(series) {
      return Sessions["delete"]({
        series: encodeURIComponent(series)
      }, (function(value, responseHeaders) {
        $scope.error = null;
        $scope.success = 'OK';
        return $scope.sessions = Sessions.get();
      }), function(httpResponse) {
        $scope.success = null;
        return $scope.error = 'ERROR';
      });
    };
  });

}).call(this);

//# sourceMappingURL=controllers.js.map
