/**
 * Created by Clemens Puehringer on 25/11/14.
 */
'use strict';

respondecoApp.controller('ProjectSearchController', function ($scope, Project) {

    $scope.select2Options = {
        tags: $scope.test,
        multiple: true,
        minimumInputLength: 1,
        formatResult: function (item) {
            return item;
        },
        formatSelection: function (item) {
            return item;
        }
    }

    $scope.project = {
        name: null,
        tags: null
    };

    $scope.search = function() {
        Project.query({
            name: $scope.project.name,
            tags: $scope.project.tags
        }, function(data) {
            $scope.projects = data;
        });
    }

    $scope.getProjectNames = function(value) {
        return Project.getProjectNames(value);
    }

});
