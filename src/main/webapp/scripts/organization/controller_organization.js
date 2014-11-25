'use strict';

respondecoApp.controller('OrganizationController', function($scope, $location, $routeParams, resolvedOrganization, Organization, Account, User) {
    var isOwner = false;
    var user;

    $scope.organizations = resolvedOrganization;

    // get the current logged in user and set the organization owner to it
    Account.get().$promise.then(function(account) {
        user = account;

        if ($scope.organization.owner !== undefined) {
            return user.login === $scope.organization.owner.login;
        }
    });

    $scope.update = function(name) {
        $scope.organization = Organization.get({
            id: name
        });

        $scope.organization.$promise.then(function() {
            Organization.getMembers({
                id: $scope.organization.id
            }).$promise.then(function(data)  {
                $scope.members = data;
            });

            isOwner = user !== undefined && user.login === $scope.organization.owner.login;
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
        $location.path('organization/edit/' + $scope.organization.name);
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
