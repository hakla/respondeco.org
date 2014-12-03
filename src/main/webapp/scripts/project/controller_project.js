'use strict';

respondecoApp.controller('ProjectController', function($scope, Project, ResourceRequirement, PropertyTagNames, $location, $routeParams, $sce) {
    $scope.project = {
        id: null,
        name: null,
        purpose: null,
        concrete: false,
        startDate: null,
        endDate: null,
        logo: null,
        propertyTags: [],
        resourceRequirements: []
    };
    $scope.projects = Project.query();
    $scope.viewedProject = Project.currentProject;

    // rating mock
    $scope.rate = 4;

    // details mock
    $scope.status = {
        open1: true
    };

    var searchText = null;
    var isNew = $routeParams.id === 'new';

    $scope.list_of_string = []

    $scope.select2Options = {
        'tags': []
    };

    $scope.openedStartDate = false;
    $scope.openedEndDate = false;
    $scope.dateOptions = {
        formatYear: 'yy',
        startingDay: 1
    };

    $scope.openStart = function($event) {
        $event.stopPropagation();
        $scope.openedStartDate = true;
    };

    $scope.openEnd = function($event) {
        $event.stopPropagation();
        $scope.openedEndDate = true;
    };

    $scope.onUploadComplete = function(fileItem, response) {
        $scope.project.logo = response;
    };

    $scope.create = function() {
        var startDate = $scope.project.startDate || null;
        var endDate = $scope.project.endDate || null;

        if (startDate != null) {
            startDate = new XDate(startDate).toString("yyyy-MM-dd");
        }

        if (endDate != null) {
            endDate = new XDate(endDate).toString("yyyy-MM-dd");
        }

        var project = {
            id: $scope.project.id,
            name: $scope.project.name,
            purpose: $scope.project.purpose,
            concrete: $scope.project.concrete,
            startDate: startDate,
            endDate: endDate,
            logo: $scope.project.logo,
            propertyTags: $.map($scope.project.propertyTags, function(tag) {
                return tag.name
            }),
            resourceRequirements: $scope.project.resourceRequirements
        };

        Project[isNew ? 'save' : 'update'](project,
            function() {
                $scope.projects = Project.query();
                $scope.clear();
            });
    };

    $scope.edit = function() {
        $location.path("/projects/edit/" + $scope.project.id)
    }

    $scope.update = function(id) {
        $scope.project = Project.get({
            id: id
        }, function() {
            $scope.project.resourceRequirements = $scope.project.resourceRequirements || [];
            $scope.purpose = $sce.trustAsHtml($scope.project.purpose);
        });
    };

    $scope.delete = function(id) {
        Project.delete({
                id: id
            },
            function() {
                $scope.projects = Project.query();
                $location.path('/projects');
            });
    };

    $scope.clear = function() {
        $scope.project = {
            id: null,
            name: null,
            purpose: null,
            concrete: false,
            startDate: null,
            endDate: null,
            projectLogo: null
        };
        $location.path('/projects');
    };

    $scope.createProject = function() {
        $location.path('/project/create');
    };

    //Resource Requirement Modal
    var edit = false;
    $scope.resource = {
        resourceTags: [],
        isEssential: false
    }
    $scope.selectedResourceTags = [];

    $scope.createRequirement = function() {
        $scope.resource.resourceTags = $.map($scope.selectedResourceTags, function(tag) {
            return tag.name
        });
        var resource = $scope.resource;

        if (edit == false) {
            $scope.project.resourceRequirements.push(resource);
        }
    }

    $scope.clearRequirement = function() {
        $scope.resource = {
            resourceTags: [],
            isEssential: false
        };
        $scope.selectedResourceTags = [];
        edit = false;
    };

    $scope.removeRequirement = function(index) {
        $scope.project.resourceRequirements.splice(index, 1);
    }

    $scope.editRequirement = function(index) {
        edit = true;
        $('#addResource').modal('toggle');
        $scope.resource = $scope.project.resourceRequirements[index];
    };

    if (isNew === false) {
        $scope.update($routeParams.id);
    }
});
