/**
 * Created by Clemens Puehringer on 25/11/14.
 */
'use strict';

respondecoApp.controller('ProjectSearchController', function ($scope, Project, ProjectNames, PropertyTagNames) {

    $scope.project = {
        name: null,
        tags: null
    };

    $scope.projects = null;

    $scope.search = function() {
        Project.query({
            name: $scope.project.name,
            tags: $scope.project.tags
        }).$promise.then(function(data) {
            $scope.projects = data;
        })
    }

});
