'use strict';

respondecoApp.controller('ResourceTagController', function ($scope, resolvedResourceTag, ResourceTag) {

        $scope.resourceTags = resolvedResourceTag;

        $scope.create = function () {
            ResourceTag.save($scope.resourceTag,
                function () {
                    $scope.resourceTags = ResourceTag.query();
                    $('#saveResourceTagModal').modal('hide');
                    $scope.clear();
                });
        };

        $scope.update = function (id) {
            $scope.resourceTag = ResourceTag.get({id: id});
            $('#saveResourceTagModal').modal('show');
        };

        $scope.delete = function (id) {
            ResourceTag.delete({id: id},
                function () {
                    $scope.resourceTags = ResourceTag.query();
                });
        };

        $scope.clear = function () {
            $scope.resourceTag = {name: null, createdBy: null, createdDate: null, lastModifiedBy: null, lastModifiedDate: null, isActive: null, id: null};
        };
    });
