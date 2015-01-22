/**
 * Created by Clemens Puehringer on 22/12/14.
 */

respondecoApp.controller('OrganizationControllerVerify', function($scope, $routeParams, Organization) {

    var PAGESIZE = 20;

    $scope.totalItems = null;
    $scope.currentPage = 1;
    $scope.filter = { pageSize: PAGESIZE };

    $scope.organizations = new Object();

    $scope.getOrganizations = function() {
        $scope.filter.page = $scope.currentPage - 1;
        Organization.query($scope.filter, function (results) {
            $scope.totalItems = results.totalItems;
            $scope.organizations = new Object();
            results.organizations.forEach(function (result) {
                $scope.organizations[result.id] = result;
            });
        });
    }

    $scope.verify = function(id, verify) {
        //convert boolean to string, else angular ignores false values completely
        Organization.verify({id: id}, "" + verify, function(result) {
            $scope.organizations[result.id] = result;
        });
    };

    $scope.onPageChange = function() {
        $scope.getOrganizations();
    };

    $scope.getOrganizations();

});
