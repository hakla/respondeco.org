/**
 * Created by Clemens Puehringer on 25/11/14.
 */
'use strict';

respondecoApp.controller('ProjectSearchController', function ($scope, $location, $q, Project,
                                                              ProjectNames, PropertyTagNames,
                                                              Organization, $rootScope, $route, $routeParams) {

    var PAGESIZE = 20;

    $scope.filter = {page:0, pageSize:PAGESIZE};
    $scope.currentPage = 1;
    $scope.searchText = null;
    $scope.searchError = null;
    $scope.noProjects = null;

    //if true shows only projects from the specified organization (specified via route parameter)
    $scope.showOrganizationOnly = $route.current.$$route.originalPath === '/organization/:id/projects';
    
    $scope.search = function() {
        $scope.filter.filter = $scope.searchText;

        var success = function(data) {
            $scope.projects = data.projects;
            $scope.totalItems = data.totalItems;
            if($scope.projects.length == 0) {
                $scope.noProjects = "NOPROJECTS";
            } else {
                $scope.noProjects = null;
            }
            $scope.searchError = null;
        };

        var error = function(error) {
            $scope.searchError = "ERROR";
            $scope.noProjects = null;
        };

        if($scope.showOrganizationOnly) {
            Organization.getOwnProjects(jQuery.extend({
                id: $routeParams.id
            }, $scope.filter), success, error);
        } else {
            Project.query($scope.filter, success, error);
        }
    }

    // if the account isn't yet loaded, we'll wait for it
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
