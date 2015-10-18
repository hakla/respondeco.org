MainRouter = ($routeProvider, $httpProvider, $translateProvider, tmhDynamicLocaleProvider, USER_ROLES) ->
  $routeProvider.when('/register',
    templateUrl: 'views/register.html'
    controller: 'RegisterController'
    access:
      authorizedRoles: [USER_ROLES.all])
  .when('/activate',
    templateUrl: 'views/activate.html'
    controller: 'ActivationController'
    access:
      authorizedRoles: [USER_ROLES.all])
  .when('/activateInvitation',
    templateUrl: 'views/register.html'
    controller: 'RegisterController'
    access:
      authorizedRoles: [USER_ROLES.all])
  .when('/login',
    templateUrl: 'views/login.html'
    controller: 'LoginController'
    access:
      authorizedRoles: [USER_ROLES.all])
  .when('/error',
    templateUrl: 'views/error.html'
    access:
      authorizedRoles: [USER_ROLES.all])
  .when('/settings',
    templateUrl: 'views/settings.html'
    controller: 'SettingsController'
    access:
      authorizedRoles: [USER_ROLES.user])
  .when('/password',
    templateUrl: 'views/password.html'
    controller: 'PasswordController'
    access:
      authorizedRoles: [USER_ROLES.user])
  .when('/sessions',
    templateUrl: 'views/sessions.html'
    controller: 'SessionsController'
    resolve:
      resolvedSessions: [
        'Sessions'
        (Sessions) ->
          Sessions.get()
      ]
    access:
      authorizedRoles: [USER_ROLES.user])
  .when('/logout',
    templateUrl: 'views/main.html'
    controller: 'LogoutController'
    access:
      authorizedRoles: [USER_ROLES.all])
  .when('/docs',
    templateUrl: 'views/docs.html'
    access:
      authorizedRoles: [USER_ROLES.admin]).otherwise
    templateUrl: 'views/main.html'
    controller: 'MainController'
    access:
      authorizedRoles: [USER_ROLES.all]

  # Initialize angular-translate
  $translateProvider.useStaticFilesLoader
    prefix: 'i18n/'
    suffix: '.json'

  $translateProvider.preferredLanguage 'de'
  $translateProvider.useCookieStorage()
  tmhDynamicLocaleProvider.localeLocationPattern 'bower_components/angular-i18n/angular-locale_{{locale}}.js'
  tmhDynamicLocaleProvider.useCookieStorage 'NG_TRANSLATE_LANG_KEY'

Main = ($rootScope, $location, $http, AuthenticationSharedService, Session, USER_ROLES, $sce, $route, editableOptions) ->
  editableOptions.theme = 'bs3'
  $rootScope.globalAlerts = $rootScope.globalAlerts or []
  regMessage =
    type: 'info'
    msg: 'Um die Registrierung abzuschließen erstelle eine Organisation! <a><strong class="pull-right" ng-click="redirectToOwnOrganization()">Zur Erstellung</strong></a>'

  checkInitialConditions = (showMessage) ->
    account = $rootScope._account
    if account == null
      return
    if account.invited == false and account.organization == null
      if showMessage != false and $rootScope.globalAlerts.indexOf(regMessage) < 0
        $rootScope.globalAlerts.push regMessage
      return true
    false

  regMessageOrganization =
    type: 'info'
    msg: 'Um die Registrierung abzuschließen erstelle eine Organisation!'
  $rootScope.$on '$routeChangeStart', (event, next) ->
    $rootScope.isAuthorized = AuthenticationSharedService.isAuthorized
    $rootScope.userRoles = USER_ROLES

    if next.access != undefined
      AuthenticationSharedService.valid next.access.authorizedRoles

    $rootScope.globalAlerts.splice $rootScope.globalAlerts.indexOf(regMessageOrganization), 1
    route = if next.$$route != null then next.$$route.originalPath else null

    if route != null
# If the user navigates to another site than the the organization/edit/new site
      if route == '/organization/edit/:id' and next.pathParams.id == 'new' or route == '/organization/edit/new'
        if checkInitialConditions(false)
          $rootScope.globalAlerts.splice $rootScope.globalAlerts.indexOf(regMessage), 1
          $rootScope.globalAlerts.push regMessageOrganization
      else
        checkInitialConditions()

  $rootScope.$on 'event:authenticated', (data) ->
    route = $route.current.originalPath

    # If the user navigates to another site than the the organization/edit/new site
    if route == '/organization/edit/:id' and $route.current.pathParams.id == 'new'
      if checkInitialConditions(false)
        $rootScope.globalAlerts.splice $rootScope.globalAlerts.indexOf(regMessage), 1
        $rootScope.globalAlerts.push regMessageOrganization
    else
      checkInitialConditions()

  # Call when the the client is confirmed
  $rootScope.$on 'event:auth-loginConfirmed', (data) ->
    $rootScope.authenticated = true

    if checkInitialConditions()
      $location.path '/organization/edit/new'
    else if $location.path() == '/login'
      search = $location.search()
      if search.redirect != undefined
        $location.path(search.redirect).search('redirect', null).replace()
      else
        $location.path('/').replace()

  # Call when the 401 response is returned by the server
  $rootScope.$on 'event:auth-loginRequired', (rejection) ->
    Session.invalidate()
    $rootScope.authenticated = false
    allowedRoutes = [
      '/'
      ''
      '/register'
      '/activate'
      '/login'
      '/activateInvitation'
      '/organization'
      '/projects'
      '/organization/d+'
    ]
    if allowedRoutes.indexOf($location.path()) == -1
      redirect = $location.path()
      $location.path('/login').search('redirect', redirect).replace()

  # Call when the 403 response is returned by the server
  $rootScope.$on 'event:auth-notAuthorized', (rejection) ->
    $rootScope.errorMessage = 'errors.403'
    $location.path('/error').replace()

  # Call when the user logs out
  $rootScope.$on 'event:auth-loginCancelled', ->
    $location.path ''


angular
  .module('respondecoApp',
    ['http-auth-interceptor', 'tmh.dynamicLocale', 'ngResource', 'ngRoute', 'ngCookies', 'pascalprecht.translate',
     'truncate', 'ui.bootstrap.bindHtml', 'ui.bootstrap.position', 'ui.bootstrap.typeahead', 'respondecoAppFilters',
     'angularFileUpload', 'ngTagsInput', 'ui.bootstrap', 'uiGmapgoogle-maps', 'angularMoment', 'xeditable'])
  .config MainRouter
  .run Main
