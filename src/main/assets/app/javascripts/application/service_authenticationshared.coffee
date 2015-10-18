AuthenticationSharedService = ($rootScope, $http, authService, Session, Account, USER_ROLES) ->
  @login = (param) ->
    data = 'j_username=' + encodeURIComponent(param.username) + '&j_password=' + encodeURIComponent(param.password) + '&_spring_security_remember_me=' + param.rememberMe + '&submit=Login'
    $http.post('app/authentication', data,
      headers: 'Content-Type': 'application/x-www-form-urlencoded'
      ignoreAuthModule: 'ignoreAuthModule').success((data, status, headers, config) ->
      Account.get (data) ->
        if data.login != 'anonymousUser'
          Session.create data
          $rootScope.account = Session
          $rootScope._account = data
          authService.loginConfirmed data
    ).error (data, status, headers, config) ->
      $rootScope.authenticationError = true
      Session.invalidate()

  @refresh= ->
    Account.get (data) ->
      if data.login != 'anonymousUser'
        Session.create data
        $rootScope.account = Session
        $rootScope._account = data

  @valid = (authorizedRoles) ->
    $http.get('protected/authentication_check.gif', ignoreAuthModule: 'ignoreAuthModule').success((data, status, headers, config) ->
      if !Session.login
        Account.get (data) ->
          if data.login != 'anonymousUser'
            Session.create data
            $rootScope.account = Session
            $rootScope._account = data
            $rootScope.authenticated = true
            $rootScope.$broadcast 'event:authenticated', data
      $rootScope.authenticated = ! !Session.login
    ).error (data, status, headers, config) ->
      $rootScope.authenticated = false
      if !$rootScope.isAuthorized(authorizedRoles)
        $rootScope.$broadcast 'event:auth-loginRequired', data

  @isAuthorized = (authorizedRoles) ->
    if authorizedRoles == null
      authorizedRoles = [ USER_ROLES.user ]
    if !angular.isArray(authorizedRoles)
      if authorizedRoles == '*'
        authorizedRoles = [ authorizedRoles ]
    isAuthorized = false
    angular.forEach authorizedRoles, (authorizedRole) ->
      authorized = ! !Session.login and Session.userRoles.indexOf(authorizedRole) != -1
      if authorized or authorizedRole == '*'
        isAuthorized = true
    isAuthorized

  @logout = ->
    $rootScope.authenticationError = false
    $rootScope.authenticated = false
    $rootScope.account = null
    $rootScope._account = null
    $http.get 'app/logout'
    Session.invalidate()
    authService.loginCancelled()

  @

angular
  .module 'respondecoApp'
  .factory 'AuthenticationSharedService', AuthenticationSharedService
