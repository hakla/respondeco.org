'use strict';

respondecoApp.controller('PropertyTagController', function ($scope, resolvedPropertyTag, PropertyTag) {

        $scope.propertytags = resolvedPropertyTag;

        $scope.create = function () {
            PropertyTag.save($scope.propertytag,
                function () {
                    $scope.propertytags = PropertyTag.query();
                    $('#savePropertyTagModal').modal('hide');
                    $scope.clear();
                });
        };

        $scope.update = function (id) {
            $scope.propertytag = PropertyTag.get({id: id});
            $('#savePropertyTagModal').modal('show');
        };

        $scope.delete = function (id) {
            PropertyTag.delete({id: id},
                function () {
                    $scope.propertytags = PropertyTag.query();
                });
        };

        $scope.clear = function () {
            $scope.propertytag = {name: null, id: null};
        };
    });
