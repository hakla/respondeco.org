'use strict'

### Controllers ###

respondecoApp.controller 'MainController', ($scope, $location, $rootScope) ->

  $scope.main = -> $location.path() is '' or $location.path() is '/'
  $scope.redirectToProjectSearch = -> $location.path 'projects'
  $scope.redirectToNewProject = -> $location.path 'projects/edit/new'
  $rootScope.globalAlerts = []
  $rootScope.closeAlert = (index) -> $rootScope.globalAlerts.splice index, 1
  linkToOrganization = -> if $rootScope._account isnt null and $rootScope._account.organization isnt null then "/organization/#{$rootScope._account.organization.id}" else 'edit/new'
  $scope.redirectToOwnOrganization = -> $location.path linkToOrganization()
  $scope.redirectToOwnProjects = -> "#{linkToOrganization()}/projects"

  $scope.isOrganizationUser = ->
    account = $rootScope._account
    if account.invited or !account.organization
      $rootScope.username = account.login
    else
      $rootScope.username = account.organization.name
    $rootScope._account.invited == false

  $scope.isActive = (viewLocation) ->
    viewLocation == $location.path()

  $rootScope.title = 'Organization'

respondecoApp.controller 'AdminController', ($scope) ->
respondecoApp.controller 'LanguageController', ($scope, $translate, LanguageService, $window, $rootScope) ->

  $scope.changeLanguage = (languageKey) ->
    changeLocale languageKey
    LanguageService.getBy(languageKey).then (languages) ->
      $scope.languages = languages

  changeLocale = (languageKey) ->
    $translate.use languageKey
    $window.moment.locale languageKey
    $rootScope.$broadcast 'amMoment:localeChanged'

  LanguageService.getBy().then (languages) ->
    $scope.languages = languages
    changeLocale $translate.use()

respondecoApp.controller 'MenuController', ($rootScope, TextMessage) ->

  refresh = ->
    TextMessage.countNewMessages (amount) ->
      $rootScope.newMessages = amount[0]

  refresh()
  # refresh the messages on each route change
  $rootScope.$on '$routeChangeStart', (event, next) ->
    refresh()

respondecoApp.controller 'LoginController', ($scope, $location, AuthenticationSharedService) ->
  $scope.rememberMe = true

  $scope.login = ->
    AuthenticationSharedService.login
      username: $scope.username
      password: $scope.password
      rememberMe: $scope.rememberMe
    return

  $scope.$on ''

respondecoApp.controller 'LogoutController', ($location, AuthenticationSharedService) ->
  AuthenticationSharedService.logout()

respondecoApp.controller 'SettingsController', ($scope, $location, Account, AuthenticationSharedService, OrgJoinRequest, Organization) ->

  $scope.onComplete = (fileItem, response) ->
    $scope.settingsAccount.profilePicture = response
    $scope.profilePicture = response.id
    $scope.save()

  $scope.success = null
  $scope.error = null
  $scope.fullName = null
  $scope.settingsAccount = {}
  Account.get (account) ->
    #account is an organization account, redirect to organization page
    if account.id == account.organization.ownerId
      # $location.path("/organization/" + account.organization.id);
    else
      $scope.settingsAccount = account
      $scope.organization = account.organization
      if $scope.settingsAccount.organizationId != null
        reloadOrganization $scope.settingsAccount.organizationId
      if $scope.settingsAccount.firstName == null
        $scope.fullName = $scope.settingsAccount.lastName
      else if $scope.settingsAccount.lastName == null
        $scope.fullName = $scope.settingsAccount.firstName
      else
        $scope.fullName = $scope.settingsAccount.firstName + ' ' + $scope.settingsAccount.lastName
      if account.profilePicture != null
        $scope.profilePicture = account.profilePicture.id

  $scope.success = null
  $scope.error = null
  $scope.gender = [
    'UNSPECIFIED'
    'MALE'
    'FEMALE'
  ]

  getCurrentOrgJoinRequests = ->
    OrgJoinRequest.current (data) ->
      $scope.orgJoinRequests = data
      data.forEach (el) ->
        Organization.get { id: el.organization.id }, (data) ->
          el.organization = data

  reloadOrganization = (id) ->
    if id != null
      $scope.organization = Organization.get(id: id)
    else
      Account.get (account) ->
        if account.organizationId != null
          reloadOrganization account.organizationId
        else
          $scope.organization = null

  getCurrentOrgJoinRequests()

  $scope.save = ->
    Account.save $scope.settingsAccount, ((value, responseHeaders) ->
      $scope.error = null
      $scope.success = 'OK'
      # $scope.settingsAccount = Account.get();
      if $scope.settingsAccount.firstName == null
        $scope.fullName = $scope.settingsAccount.lastName
      else if $scope.settingsAccount.lastName == null
        $scope.fullName = $scope.settingsAccount.firstName
      else
        $scope.fullName = $scope.settingsAccount.firstName + ' ' + $scope.settingsAccount.lastName
    ), (httpResponse) ->
      $scope.success = null
      $scope.error = 'ERROR'

  $scope.delete = ->
    Account.delete ((value, responseHeaders) ->
      $scope.error = null
      $scope.accountdeleted = 'OK'
      AuthenticationSharedService.logout()
    ), (httpResponse) ->
      $scope.accountdeleted = null
      $scope.error = 'ERROR'

  $scope.acceptInvitation = (id) ->
    OrgJoinRequest.accept { id: id }, ->
      getCurrentOrgJoinRequests()
      reloadOrganization()

  $scope.declineInvitation = (id) -> OrgJoinRequest.decline { id: id }, getCurrentOrgJoinRequests

  $scope.leaveOrganization = ->
    Account.leaveOrganization ->
      reloadOrganization()

  $scope.edit =
    image: true
    account: false
respondecoApp.controller 'RegisterController', ($scope, $translate, Register, $location, $routeParams) ->
  $scope.activation = false
  $scope.success = null
  $scope.error = null
  $scope.doNotMatch = null
  $scope.errorUserExists = null
  $scope.registerAccount =
    orgname: null
    npo: null
    email: null
    password: null
    langKey: 'de'
  if $location.path() == '/activateInvitation'
    $scope.activation = true
    $scope.registerAccount.email = $routeParams.email

  $scope.register = ->
    if $scope.registerAccount.password != $scope.confirmPassword
      $scope.doNotMatch = 'ERROR'
    else
      $scope.registerAccount.langKey = $translate.use()
      $scope.doNotMatch = null
      Register.save $scope.registerAccount, ((value, responseHeaders) ->
        $scope.error = null
        $scope.errorUserExists = null
        $scope.success = 'OK'
      ), (httpResponse) ->
        $scope.success = null
        if httpResponse.status == 304 and httpResponse.data.error and httpResponse.data.error == 'Not Modified'
          $scope.error = null
          $scope.errorUserExists = 'ERROR'
        else
          $scope.error = 'ERROR'
          $scope.errorUserExists = null

respondecoApp.controller 'ActivationController', ($scope, $routeParams, $location, Activate) ->
  Activate.get { key: $routeParams.key }, ((value, responseHeaders) ->
    $scope.error = null
    $scope.success = 'OK'

    $location.path "organization/#{value.id}"
  ), (httpResponse) ->
    $scope.success = null
    $scope.error = 'ERROR'

respondecoApp.controller 'PasswordController', ($scope, Password) ->
  $scope.success = null
  $scope.error = null
  $scope.doNotMatch = null

  $scope.changePassword = ->
    if $scope.password != $scope.confirmPassword
      $scope.doNotMatch = 'ERROR'
    else
      $scope.doNotMatch = null
      Password.save $scope.password, ((value, responseHeaders) ->
        $scope.error = null
        $scope.success = 'OK'
      ), (httpResponse) ->
        $scope.success = null
        $scope.error = 'ERROR'

respondecoApp.controller 'SessionsController', ($scope, resolvedSessions, Sessions) ->
  $scope.success = null
  $scope.error = null
  $scope.sessions = resolvedSessions

  $scope.invalidate = (series) ->
    Sessions.delete { series: encodeURIComponent(series) }, ((value, responseHeaders) ->
      $scope.error = null
      $scope.success = 'OK'
      $scope.sessions = Sessions.get()
    ), (httpResponse) ->
      $scope.success = null
      $scope.error = 'ERROR'

# ---
# generated by js2coffee 2.0.3