'use strict';

respondecoApp.controller('OrganizationsController', function($scope, $location, Organization) {
    Organization.query(null, function(data) {
      $scope.organizations = data;
    });

    $scope.redirectToOrganization = function(name) {
        $location.path('organization/' + name);
    };

    $scope.redirectToNew = function() {
        $location.path('organization/edit/new');
    };
});
