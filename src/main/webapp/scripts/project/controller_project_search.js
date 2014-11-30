/**
 * Created by Clemens Puehringer on 25/11/14.
 */
'use strict';

respondecoApp.controller('ProjectSearchController', function ($scope, $location, Project,
                                                              resolvedProjects, ProjectNames, PropertyTagNames) {

    $scope.projects = resolvedProjects;
    $scope.project = {
        name: null,
        tags: null
    };
    $scope.selectedTags = [];

    $scope.search = function() {
        $scope.project.tags = $.map($scope.selectedTags, function(tag) {return tag.name}).join(","); //create comma separated list
        Project.query({
            filter: $scope.project.name,
            tags: $scope.project.tags
        }, function(data) {
            $scope.projects = data;
        });
    }

    $scope.getProjectNames = function(viewValue) {
        return ProjectNames.getProjectNames(viewValue).$promise.then(
            function(response) {
                return response;
            }
        );
    }

    $scope.getPropertyTagNames = function(viewValue) {
        return PropertyTagNames.getPropertyTagNames(viewValue).$promise;
    }

    $scope.redirectToProject = function(project) {
        $location.path("/projects/" + project.id);
    }

});
