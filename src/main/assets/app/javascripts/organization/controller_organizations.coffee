OrganizationsController = ($scope, $location, Organization) ->
  PAGESIZE = 20
  $scope.totalItems = null
  $scope.organizations = []
  $scope.currentPage = 1
  $scope.filter =
    pageSize: PAGESIZE
    fields: "name,logo"

  $scope.getOrganizations = ->
    $scope.filter.page = $scope.currentPage - 1
    Organization.query $scope.filter, (page) ->
      $scope.totalItems = page.totalItems
      $scope.organizations = page.organizations

  $scope.onPageChange = ->
    $scope.getOrganizations()

  $scope.redirectToOrganization = (name) ->
    $location.path 'organization/' + name

  $scope.redirectToNew = ->
    $location.path 'organization/edit/new'

  $scope.getOrganizations()

angular
  .module 'respondecoApp'
  .controller 'OrganizationsController', OrganizationsController
