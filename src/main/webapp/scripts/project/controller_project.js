'use strict';

respondecoApp.controller('ProjectController', function ($scope, Project, $location) {

        $scope.project = {id:null,name:null,purpose:null,concrete:false,startDate:null,endDate:null,projectLogo:null,propertyTags:null,resourceRequirements:null};
        $scope.projects = Project.query();
        var searchText=null;
        $scope.viewedProject = Project.currentProject;

        $scope.list_of_string = []

        $scope.select2Options = {
            'tags': []
        };

        $scope.create = function () {
            Project.save($scope.project,
                function () {
                    $scope.projects = Project.query();
                    $scope.clear();
                    $location.path('/project');
                });
        };

        $scope.update = function (id) {
            $scope.project = Project.get({id: id});
        };

        $scope.delete = function (id) {
            Project.delete({id: id},
                function () {
                    $scope.projects = Project.query();
                    $location.path('/project');
                });
        };

        $scope.clear = function () {
            $scope.project = {id: null, name: null, purpose: null, concrete:false,startDate:null,endDate:null,projectLogo:null};
            $location.path('/project');
        };

        $scope.viewProjectDetails = function (viewedProject) {
            Project.setProject(viewedProject);
            $location.path('/project/viewDetails');
        };

        $scope.createProject = function () {
            $location.path('/project/create');
        };

        $scope.open = function($event) {
            $event.preventDefault();
            $event.stopPropagation();

            $scope.opened = true;
        };
    });
