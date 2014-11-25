'use strict';

respondecoApp.controller('ResourceRequirementJoinResourceTagController', function ($scope, resolvedResourceRequirementJoinResourceTag, ResourceRequirementJoinResourceTag, resolvedResourceTag, resolvedResourceRequirement) {

        $scope.resourceRequirementJoinResourceTags = resolvedResourceRequirementJoinResourceTag;
        $scope.resourceTags = resolvedResourceTag;
        $scope.resourceRequirements = resolvedResourceRequirement;

        $scope.create = function () {
            ResourceRequirementJoinResourceTag.save($scope.resourceRequirementJoinResourceTag,
                function () {
                    $scope.resourceRequirementJoinResourceTags = ResourceRequirementJoinResourceTag.query();
                    $('#saveResourceRequirementJoinResourceTagModal').modal('hide');
                    $scope.clear();
                });
        };

        $scope.update = function (id) {
            $scope.resourceRequirementJoinResourceTag = ResourceRequirementJoinResourceTag.get({id: id});
            $('#saveResourceRequirementJoinResourceTagModal').modal('show');
        };

        $scope.delete = function (id) {
            ResourceRequirementJoinResourceTag.delete({id: id},
                function () {
                    $scope.resourceRequirementJoinResourceTags = ResourceRequirementJoinResourceTag.query();
                });
        };

        $scope.clear = function () {
            $scope.resourceRequirementJoinResourceTag = {resourceRequirementId: null, resourceTagId: null, id: null};
        };
    });
