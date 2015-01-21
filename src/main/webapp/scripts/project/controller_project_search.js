/**
 * Created by Clemens Puehringer on 25/11/14.
 */
'use strict';

respondecoApp.controller('ProjectSearchController', function ($scope, $location, Project) {

    var PAGESIZE = 20;

    $scope.filter = {page:0, pageSize:PAGESIZE};
    $scope.currentPage = 1;

    $scope.searchText = null;

    $scope.searchError = null;
    $scope.noProjects = null;

    $scope.search = function() {
        $scope.filter.filter = $scope.searchText;

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

    $scope.redirectToProject = function(project) {
        $location.path("/projects/" + project.id);
    }

});
