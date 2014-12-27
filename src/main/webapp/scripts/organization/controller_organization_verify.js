/**
 * Created by Clemens Puehringer on 22/12/14.
 */

respondecoApp.controller('OrganizationControllerVerify', function($scope, $routeParams, Organization) {
    $scope.organizations = new Object();
    $scope.bla = false;
    Organization.query(function(results) {
        results.forEach(function(result) {
            $scope.organizations[result.id] = result;
        });
    });

    $scope.verify = function(id, verify) {
        //convert boolean to string, else angular ignores false values completely
        Organization.verify({id: id}, "" + verify, function(result) {
            $scope.organizations[result.id] = result;
        });
    }

});
