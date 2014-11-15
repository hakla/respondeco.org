'use strict';

respondecoApp.controller('ProjectLogoController', function ($scope, resolvedProjectLogo, ProjectLogo, resolvedProject) {

        $scope.projectlogos = resolvedProjectLogo;
        $scope.projects = resolvedProject;

        $scope.create = function () {
            ProjectLogo.save($scope.projectlogo,
                function () {
                    $scope.projectlogos = ProjectLogo.query();
                    $('#saveProjectLogoModal').modal('hide');
                    $scope.clear();
                });
        };

        $scope.update = function (id) {
            $scope.projectlogo = ProjectLogo.get({id: id});
            $('#saveProjectLogoModal').modal('show');
        };

        $scope.delete = function (id) {
            ProjectLogo.delete({id: id},
                function () {
                    $scope.projectlogos = ProjectLogo.query();
                });
        };

        $scope.clear = function () {
            $scope.projectlogo = {data: null, id: null};
        };
    });
