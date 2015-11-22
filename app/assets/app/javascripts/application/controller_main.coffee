MainController = ($scope, $location, $rootScope) ->
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

  $menu = jQuery '.subheader'
  $window = jQuery(window).on 'scroll', (event) ->
    if $window.scrollTop() > 87
      $menu.addClass 'fixed-menu'
    else
      $menu.removeClass 'fixed-menu'

angular
.module 'respondecoApp'
.controller 'MainController', MainController
