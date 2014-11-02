'use strict';

respondecoApp.controller('OrganizationController', function ($scope, resolvedOrganization, Organization) {

        $scope.organizations = resolvedOrganization;

        $scope.create = function () {
            Organization.save($scope.organization,
                function () {
                    $scope.organizations = Organization.query();
                    $('#saveOrganizationModal').modal('hide');
                    $scope.clear();
                });
        };

        $scope.update = function (id) {
            $scope.organization = Organization.get({id: id});
            $('#saveOrganizationModal').modal('show');
        };

        $scope.delete = function (id) {
            Organization.delete({id: id},
                function () {
                    $scope.organizations = Organization.query();
                });
        };

        $scope.clear = function () {
            $scope.organization = {name: null, description: null, email: null, logo: null, isNpo: null, id: null};
        };
    });
