/**
 * Created by Clemens Puehringer on 25/11/14.
 */
'use strict';

respondecoApp.controller('ProjectSearchController', function ($scope,$location, Project, ProjectNames, PropertyTagNames) {

    $scope.projects = null;
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
        return ProjectNames.getProjectNames(value);
    }

    $scope.getPropertyTagNames = function(value) {
        return PropertyTagNames.getPropertyTagNames(value);
    }

    $scope.redirectToProject = function(project) {
        $location.path("/projects/" + project.id);
    }

});
