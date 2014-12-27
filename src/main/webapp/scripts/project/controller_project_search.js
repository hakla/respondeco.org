/**
 * Created by Clemens Puehringer on 25/11/14.
 */
'use strict';

respondecoApp.controller('ProjectSearchController', function ($scope, $location, Project,
                                                                ProjectNames, PropertyTagNames) {

    var PAGESIZE = 20;

    $scope.filter = {pageSize:PAGESIZE};
    $scope.currentPage = 1;

    $scope.project = {
        name: null,
        tags: null
    };
    $scope.selectedTags = [];

    $scope.searchError = null;
    $scope.noProjects = null;

    $scope.search = function() {
        $scope.project.tags = $.map($scope.selectedTags, function(tag) {return tag.name}).join(","); //create comma separated list

        $scope.filter.filter = $scope.project.name;
        $scope.filter.tags = $scope.project.tags;
        $scope.filter.page = $scope.currentPage-1;

        Project.query($scope.filter,function(data) {
            $scope.projects = data.projects;
            $scope.totalItems = data.totalItems;
            if($scope.projects.length == 0) {
                $scope.noProjects = "NOPROJECTS";
            } else {
                $scope.noProjects = null;
            }
            $scope.searchError = null;
        }, function(error) {
            $scope.searchError = "ERROR";
            $scope.noProjects = null;
        });
    }

    $scope.search();

    $scope.searchButton = function() {
        $scope.currentPage = 1;
        $scope.search();
    }

    $scope.onPageChange = function() {
        $scope.search();
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
