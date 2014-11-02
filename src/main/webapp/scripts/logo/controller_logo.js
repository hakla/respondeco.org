'use strict';

respondecoApp.controller('LogoController', function ($scope, resolvedLogo, Logo) {

        $scope.logos = resolvedLogo;

        $scope.create = function () {
            Logo.save($scope.logo,
                function () {
                    $scope.logos = Logo.query();
                    $('#saveLogoModal').modal('hide');
                    $scope.clear();
                });
        };

        $scope.update = function (id) {
            $scope.logo = Logo.get({id: id});
            $('#saveLogoModal').modal('show');
        };

        $scope.delete = function (id) {
            Logo.delete({id: id},
                function () {
                    $scope.logos = Logo.query();
                });
        };

        $scope.clear = function () {
            $scope.logo = {orgName: null, label: null, data: null, id: null};
        };
    });
