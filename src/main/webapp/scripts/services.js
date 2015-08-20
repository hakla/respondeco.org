(function() {
  'use strict';

  /* Services */
  respondecoApp.factory('LanguageService', function($http, $translate, LANGUAGES) {
    return {
      getBy: function(language) {
        var promise;
        if (language === void 0) {
          language = $translate.storage().get('NG_TRANSLATE_LANG_KEY');
        }
        if (language === void 0) {
          language = 'en';
        }
        promise = $http.get('/i18n/' + language + '.json').then(function(response) {
          return LANGUAGES;
        });
        return promise;
      }
    };
  });

  respondecoApp.factory('Register', function($resource) {
    return $resource('app/rest/register', {}, {});
  });

  respondecoApp.factory('Activate', function($resource) {
    return $resource('app/rest/activate', {}, {
      'get': {
        method: 'GET',
        params: {},
        isArray: false
      }
    });
  });

  respondecoApp.factory('Account', function($resource) {
    return $resource('app/rest/account', {}, {
      leaveOrganization: {
        method: 'POST',
        url: 'app/rest/account/leaveorganization'
      },
      getNewsfeed: {
        method: 'GET',
        url: 'app/rest/account/newsfeed'
      },
      setProfilePicture: {
        method: 'POST',
        url: 'app/rest/account/profilepicture'
      }
    });
  });

  respondecoApp.factory('Password', function($resource) {
    return $resource('app/rest/account/change_password', {}, {});
  });

  respondecoApp.factory('Sessions', function($resource) {
    return $resource('app/rest/account/sessions/:series', {}, {
      'get': {
        method: 'GET',
        isArray: true
      }
    });
  });

  respondecoApp.factory('Session', function() {
    this.create = function(account) {
      this.login = account.login;
      this.firstName = account.firstName;
      this.lastName = account.lastName;
      this.email = account.email;
      this.userRoles = account.roles;
      return this.profilePicture = account.profilePicture;
    };
    this.invalidate = function() {
      this.login = null;
      this.firstName = null;
      this.lastName = null;
      this.email = null;
      return this.userRoles = null;
    };
    this.valid = function() {
      return this.login !== null && this.login !== void 0;
    };
    return this;
  });

  respondecoApp.factory('AuthenticationSharedService', function($rootScope, $http, authService, Session, Account, USER_ROLES) {
    this.login = function(param) {
      var data;
      data = 'j_username=' + encodeURIComponent(param.username) + '&j_password=' + encodeURIComponent(param.password) + '&_spring_security_remember_me=' + param.rememberMe + '&submit=Login';
      return $http.post('app/authentication', data, {
        headers: {
          'Content-Type': 'application/x-www-form-urlencoded'
        },
        ignoreAuthModule: 'ignoreAuthModule'
      }).success(function(data, status, headers, config) {
        return Account.get(function(data) {
          if (data.login !== 'anonymousUser') {
            Session.create(data);
            $rootScope.account = Session;
            $rootScope._account = data;
            return authService.loginConfirmed(data);
          }
        });
      }).error(function(data, status, headers, config) {
        $rootScope.authenticationError = true;
        return Session.invalidate();
      });
    };
    this.refresh = function() {
      return Account.get(function(data) {
        if (data.login !== 'anonymousUser') {
          Session.create(data);
          $rootScope.account = Session;
          return $rootScope._account = data;
        }
      });
    };
    this.valid = function(authorizedRoles) {
      return $http.get('protected/authentication_check.gif', {
        ignoreAuthModule: 'ignoreAuthModule'
      }).success(function(data, status, headers, config) {
        if (!Session.login) {
          Account.get(function(data) {
            if (data.login !== 'anonymousUser') {
              Session.create(data);
              $rootScope.account = Session;
              $rootScope._account = data;
              $rootScope.authenticated = true;
              return $rootScope.$broadcast('event:authenticated', data);
            }
          });
        }
        return $rootScope.authenticated = !!Session.login;
      }).error(function(data, status, headers, config) {
        $rootScope.authenticated = false;
        if (!$rootScope.isAuthorized(authorizedRoles)) {
          return $rootScope.$broadcast('event:auth-loginRequired', data);
        }
      });
    };
    this.isAuthorized = function(authorizedRoles) {
      var isAuthorized;
      if (authorizedRoles === null) {
        authorizedRoles = [USER_ROLES.user];
      }
      if (!angular.isArray(authorizedRoles)) {
        if (authorizedRoles === '*') {
          authorizedRoles = [authorizedRoles];
        }
      }
      isAuthorized = false;
      angular.forEach(authorizedRoles, function(authorizedRole) {
        var authorized;
        authorized = !!Session.login && Session.userRoles.indexOf(authorizedRole) !== -1;
        if (authorized || authorizedRole === '*') {
          return isAuthorized = true;
        }
      });
      return isAuthorized;
    };
    this.logout = function() {
      $rootScope.authenticationError = false;
      $rootScope.authenticated = false;
      $rootScope.account = null;
      $rootScope._account = null;
      $http.get('app/logout');
      Session.invalidate();
      return authService.loginCancelled();
    };
    return this;
  });

}).call(this);

//# sourceMappingURL=services.js.map
