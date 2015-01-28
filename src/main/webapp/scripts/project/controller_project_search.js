/**
 * Created by Clemens Puehringer on 25/11/14.
 */
'use strict';

respondecoApp.controller('ProjectSearchController', function ($scope, $location, $q, Project,
                                                              ProjectNames, PropertyTagNames,
                                                              Organization, $route, $routeParams, $rootScope) {

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
        $scope.filter.page = $scope.currentPage - 1;

        var success = function(data) {
            $scope.projects = data.projects;
            $scope.totalItems = data.totalItems;
            if($scope.projects.length == 0) {
                $scope.noProjects = "NOPROJECTS";
            } else {
                $scope.noProjects = null;
            }
            $scope.searchError = null;

            $scope.projects.filter(function(project) { return project.concrete == true; }).forEach(function(project) {
                var startDate = new XDate(project.startDate);

                if (startDate.diffDays() < 0) {
                    project.remainingDays = Math.round(startDate.diffDays() * -1) + 1;
                } else {
                    project.remainingDays = false;
                }
            })
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

    $scope.checkAccount = function(account) {
            $scope.ownOrganization = account.organization.id == $routeParams.id;

            Organization.get({
                id: $routeParams.id
            }, function(data) {
                $scope.organization = data;
            });
        };

    $scope.checkShowOrgOnly = function() {
        if ($scope.showOrganizationOnly) {
            
            $scope.checkAccount();

            // if the account isn't yet loaded, we'll wait for it
            if ($rootScope._account == null) {
                $rootScope.$on("event:authenticated", function() {
                    $scope.checkAccount($rootScope._account);
                });
            } else {
                $scope.checkAccount($rootScope._account);
            }
        }
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

    $scope.redirectToOrganization = function(organization) {
        $location.path("/organization/" + $routeParams.id);
    }

});
