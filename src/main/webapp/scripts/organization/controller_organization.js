'use strict';

respondecoApp.controller('OrganizationController', function($scope, $location, $routeParams, resolvedOrganization, Organization, Account, User) {
    var isOwner = false;
    var user;

    $scope.organizations = resolvedOrganization;

    // get the current logged in user and set the organization owner to it

    $scope.update = function(name) {
        $scope.organization = Organization.get({
            id: name
        }, function() {
            Organization.getMembers({
                id: $scope.organization.id
            }, function(data)  {
                $scope.members = data;
            });

            Account.get(null, function(account) {
                user = account;
                isOwner = user !== undefined && user.login === $scope.organization.owner.login;
            });

        });
    };

    $scope.delete = function(id) {
        id = id || $scope.organization.id;

        if (confirm("Wirklich löschen?") === false) return;

        Organization.delete({
                id: id
            },
            function() {
                $scope.organizations = Organization.query();
            });
    };

    $scope.redirectToEdit = function() {
        $location.path('organization/edit/' + $scope.organization.id);
    };

    $scope.isOwner = function() {
        return isOwner;
    };

    $scope.updateUser = function($item, $model, $label) {
        $selectedUser = $item;
    };

    $scope.redirectToOverview = function() {
        $location.path('organization');
    };

    if ($routeParams.id !== undefined) {
        $scope.update($routeParams.id);
    }
});
