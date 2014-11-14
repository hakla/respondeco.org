'use strict';

respondecoApp.controller('ProjectIdeaController', function ($scope, resolvedProjectIdea, ProjectIdea, resolvedOrganization) {

        $scope.projectideas = resolvedProjectIdea;
        $scope.Organizations = resolvedOrganization;

        $scope.create = function () {
            ProjectIdea.save($scope.projectidea,
                function () {
                    $scope.projectideas = ProjectIdea.query();
                    $('#saveProjectIdeaModal').modal('hide');
                    $scope.clear();
                });
        };

        $scope.update = function (id) {
            $scope.projectidea = ProjectIdea.get({id: id});
            $('#saveProjectIdeaModal').modal('show');
        };

        $scope.delete = function (id) {
            ProjectIdea.delete({id: id},
                function () {
                    $scope.projectideas = ProjectIdea.query();
                });
        };

        $scope.clear = function () {
            $scope.projectidea = {organizationId: null, name: null, purpose: null, projectLogo: null, id: null};
        };
    });
