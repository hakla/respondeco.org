'use strict';

respondecoApp.controller('ProjectController', function ($scope, Project, ResourceRequirement, $location) {

        $scope.project = {id:null,name:null,purpose:null,concrete:false,startDate:null,endDate:null,projectLogo:null,propertyTags:[],resourceRequirements:[]};
        $scope.projects = Project.query();
        var searchText=null;
        $scope.viewedProject = Project.currentProject;

        $scope.list_of_string = []

        $scope.select2Options = {
            'tags': []
        };

        $scope.onUploadComplete = function(fileItem, response) {
            $scope.project.logo = response;
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
                    $location.path('/projects');
                });
        };

        $scope.clear = function () {
            $scope.project = {id: null, name: null, purpose: null, concrete:false,startDate:null,endDate:null,projectLogo:null};
            $location.path('/project');
        };

        $scope.viewProjectDetails = function (viewedProject) {
            Project.setProject(viewedProject);
            $location.path('/projects/viewDetails');
        };

        $scope.createProject = function () {
            $location.path('/project/create');
        };

        $scope.open = function($event) {
            $event.preventDefault();
            $event.stopPropagation();

            $scope.opened = true;
        };

        //Resource Requirement Modal
        var edit = false;
        $scope.resource = {resourceTags: [], isEssential: false}

        $scope.createRequirement = function() {
            var resource = $scope.resource;

            if(edit == false) {
                $scope.project.resourceRequirements.push(resource);
            }
        }

        $scope.clearRequirement = function() {
            $scope.resource = {resourceTags: [], isEssential: false};
            edit = false;
        };

        $scope.removeRequirement = function(index) {
            $scope.project.resourceRequirements.splice(index,1);
        }

        $scope.editRequirement = function(index) {
            edit = true;
            $('#addResource').modal('toggle');
            $scope.resource = $scope.project.resourceRequirements[index];
        }
    });
