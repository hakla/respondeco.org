'use strict';

respondecoApp.controller('ResourceRequirementController', function ($scope, resolvedResourceRequirement, ResourceRequirement, resolvedResourceTag, resolvedProject, resolvedResourceOfferJoinResourceRequirement) {

        $scope.resourceRequirements = resolvedResourceRequirement;
        $scope.resourceTags = resolvedResourceTag;
        $scope.projects = resolvedProject;
        $scope.resourceOfferJoinResourceRequirements = resolvedResourceOfferJoinResourceRequirement;

        $scope.create = function () {
            ResourceRequirement.save($scope.resourceRequirement,
                function () {
                    $scope.resourceRequirements = ResourceRequirement.query();
                    $('#saveResourceRequirementModal').modal('hide');
                    $scope.clear();
                });
        };

        $scope.update = function (id) {
            $scope.resourceRequirement = ResourceRequirement.get({id: id});
            $('#saveResourceRequirementModal').modal('show');
        };

        $scope.delete = function (id) {
            ResourceRequirement.delete({id: id},
                function () {
                    $scope.resourceRequirements = ResourceRequirement.query();
                });
        };

        $scope.clear = function () {
            $scope.resourceRequirement = {amount: null, description: null, projectId: null, isEssential: null, createdBy: null, createdDate: null, lastModifiedBy: null, lastModifiedDate: null, isActive: null, id: null};
        };
    });
