'use strict';

respondecoApp.controller('ProjectController', function($scope, Project, Organization, ResourceRequirement,
                                                       PropertyTagNames, $location, $routeParams, $sce) {
    $(function () {
        $('[data-toggle="popover"]').popover()
    });
    //$("#rating").popover("show");

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

    $scope.canRate = true;
    $scope.isRating = false;
    $scope.shownRating = 0;
    $scope.ratingCount = 0;
    $scope.ratingComment = null;

    Project.getAggregatedRating({pid: $routeParams.id},
        function(rating) {
            $scope.shownRating = rating.rating;
            $scope.ratingCount = rating.count;
        });

    $scope.collected = 0;

    $scope.resourceRequirementsWithMatches = [];

    if($scope.canRate) {
        $("#rating").trigger("show");
    }

    // details mock
    $scope.status = {
        open1: true
    };

    var searchText = null;
    var isNew = $routeParams.id === 'new' || $routeParams.id === 'null' || $routeParams.id === 'undefined';

    $scope.list_of_string = [];

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

        var actualTags;
        for(var i=0;i<$scope.project.resourceRequirements.length;i++) {
            var req = $scope.project.resourceRequirements[i];
            actualTags = $.map(req.resourceTags, function(tag) {
                return tag.name
            });
            req.resourceTags = actualTags;
        }

        $.map($scope.project.resourceRequirements, function(req) { delete req.matches; delete req.sum; return req; });

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
            console.log($scope.project);
            $scope.project.resourceRequirements = $scope.project.resourceRequirements || [];
            $scope.purpose = $sce.trustAsHtml($scope.project.purpose);

            $scope.resourceRequirementsWithMatches = $scope.project.resourceRequirements.slice(0);

            console.log($scope.resourceRequirementsWithMatches);

            Project.getResourceMatchesByProjectId({id:id}, function(matches) {

                //assign matches to requirement and calculate amount
                 $scope.resourceRequirementsWithMatches.forEach(function(req) {
                    req.matches = [];
                    req.sum = 0;

                    matches.forEach(function(match) {
                        if(match.resourceRequirement.id == req.id) {
                           req.matches.push(match);
                           console.log("PUSHING");
                            req.sum = req.sum + match.resourceRequirement.amount;
                        }
                    });
                });

                calculateCollected();

            });
        });
    };

    var calculateCollected = function() {
        var reqs = $scope.resourceRequirementsWithMatches;
        var quantifier;
        var percentage = 0;

        if(reqs.length>0) {
            quantifier = 100 / reqs.length;
            console.log("TEST");
            reqs.forEach(function(req) {
                percentage = percentage + (req.sum / req.amount / reqs.length);
            });
        }


        $scope.collected = percentage*100;
    }

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
        $scope.resource.resourceTags = $scope.selectedResourceTags;
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
        $scope.selectedResourceTags = $scope.resource.resourceTags;
    };

    $scope.showRating = function() {
        if($scope.canRate) {
            $scope.isRating = true;
        }
    }

    $scope.hideRating = function() {
        $scope.isRating = false;
    }

    $scope.rateProject = function() {
        if($scope.project != null) {
            if ($scope.rating.id == null) {
                Project.rateProject({pid: $routeParams.id}, {rating: $scope.shownRating, comment: $scope.ratingComment},
                function() {
                    $scope.rateSucces = "SUCCESS";
                    $scope.canRate = false;
                    $scope.hideRating();
                });
            }
        }
    }

    if (isNew === false) {
        $scope.update($routeParams.id);
    }
});
