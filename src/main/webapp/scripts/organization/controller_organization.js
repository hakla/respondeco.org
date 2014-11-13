'use strict';

respondecoApp.controller('OrganizationController', function ($scope, $location, $routeParams, resolvedOrganization, Organization, Account) {

        var id = $routeParams.id;
        var isNew = id === 'new';
        var isEdit = id !== undefined && isNew === false
        var redirectToEdit = function(name) {
            $location.path('organization/' + name);
        };

        $scope.organization = {
            npo: false,
            owner: false
        };

        Account.get().$promise.then(function(account) {
            $scope.organization.owner = account.login;
        });

        $scope.organizations = resolvedOrganization;

        $scope.isNew = function() {
            return isNew;
        };

        $scope.isEdit = function() {
            return isEdit;
        }

        $scope.create = function () {
            Organization.save($scope.organization,
                function () {
                    $scope.organizations = Organization.query();
                    $scope.clear();
                }, 
                function(resp) {
                    console.log(resp.data.message);
                });
        };

        $scope.update = function (name) {
            $scope.organization = Organization.get({id: name});
            redirectToEdit(name);
        };

        $scope.delete = function (id) {
            Organization.delete({id: id},
                function () {
                    $scope.organizations = Organization.query();
                });
        };

        $scope.clear = function () {
            $scope.organization = {name: null, description: null, email: null, isNpo: null, id: null};
            $location.path('organization');
        };

        $scope.redirectToNew = function() {
            redirectToEdit('new');
        };

        $scope.redirectToEdit = redirectToEdit;

        if (isEdit) {
            $scope.update(id);
        } 
    });
