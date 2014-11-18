'use strict';

respondecoApp.controller('ProjectController', function ($scope, Project, $location) {

        $scope.project = {id:null,name:null,purpose:null,concrete:false,startDate:null,endDate:null};
        $scope.projects = Project.query();
        var searchText=null;
        $scope.viewedProject = null;

        $scope.create = function () {
            Project.save($scope.project,
                function () {
                    $scope.projects = Project.query();
                    $scope.clear();
                });
        };

        $scope.update = function (id) {
            $scope.project = Project.get({id: id});
        };

        $scope.delete = function (id) {
            Project.delete({id: id},
                function () {
                    $scope.projects = Project.query();
                });
        };

        $scope.clear = function () {
            $scope.project = {id: null, name: null, purpose: null, concrete:false,startDate:null,endDate:null};
        };

        $scope.viewProjectDetails = function (viewedProject) {
            Project.setProject(viewedProject);
            $location.path('/project/viewDetails');
            $scope.viewedProject = Project.currentProject;
            alert(viewedProject.name);
        };

        $scope.createProject = function () {
            $location.path('/project/create');
        };
    });
