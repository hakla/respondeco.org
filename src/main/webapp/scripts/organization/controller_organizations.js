'use strict';

respondecoApp.controller('OrganizationsController', function($scope, $location, Organization) {

    var PAGESIZE = 20;

    $scope.totalItems = null;
    $scope.organizations = [];
    $scope.currentPage = 1;
    $scope.filter = { pageSize: PAGESIZE };

    $scope.getOrganizations = function() {
        $scope.filter.page = $scope.currentPage - 1;
        Organization.query($scope.filter, function (page) {
            $scope.totalItems = page.totalItems;
            $scope.organizations = page.organizations;
        });
    };

    $scope.onPageChange = function() {
      $scope.getOrganizations();
    };

    $scope.redirectToOrganization = function(name) {
        $location.path('organization/' + name);
    };

    $scope.redirectToNew = function() {
        $location.path('organization/edit/new');
    };

    $scope.getOrganizations();
});
