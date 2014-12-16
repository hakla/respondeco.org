'use strict';

respondecoApp.controller('ProjectController', function($scope, Project, Organization, ResourceRequirement,
                                                       PropertyTagNames, $location, $routeParams, $sce, Account, $modal) {
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

    $scope.organizationRatings = new Object();
    $scope.organizationRatingCounts = new Object();


    $scope.collected = 0;
    $scope.collectedEssential = 0;

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

    // project apply
    var organization;
    var account;
    var allowedToApply = false;
    var selectedResourceOffer;

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

        $.map($scope.project.resourceRequirements, function(req) { delete req.matches; delete req.sum; delete req.essentialSum; return req; });

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

            if ($scope.project.concrete === true) {
                var startDate = new XDate($scope.project.startDate);

                if (startDate.diffDays() < 0) {
                    $scope.remainingDays = Math.round(startDate.diffDays() * -1) + 1;
                } else {
                    $scope.remainingDays = true;
                }
            }

            $scope.resourceRequirementsWithMatches = $scope.project.resourceRequirements.slice(0);

            Project.getResourceMatchesByProjectId({id:id}, function(matches) {

                //assign matches to requirement and calculate amount
                 $scope.resourceRequirementsWithMatches.forEach(function(req) {
                    req.matches = [];
                    req.sum = 0;
                    req.essentialSum = 0;

                    matches.forEach(function(match) {
                        if(match.resourceRequirement.id == req.id) {
                            req.matches.push(match);
                            req.sum = req.sum + match.amount;

                            if(match.resourceRequirement.isEssential === true) {
                                req.essentialSum = req.essentialSum + match.amount;
                            }

                            //if there is no rating for this org already, get it
                            if(!$scope.organizationRatings[match.organization.id]) {
                                $scope.organizationRatings[match.organization.id] =
                                    Organization.getAggregatedRating({id: match.organization.id}, function(rating) {
                                        $scope.organizationRatings[match.organization.id] = rating.rating;
                                        $scope.organizationRatingCounts[match.organization.id] = rating.count;
                                    }
                                )
                            }
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
        var percentageEssential = 0;
        var countEssential = 0;

        if(reqs.length>0) {
            quantifier = 100 / reqs.length;

            reqs.forEach(function(req) {
                percentage = percentage + (req.sum / req.originalAmount / reqs.length);

                if(req.isEssential === true) {
                    countEssential++;
                    percentageEssential = percentageEssential + (req.sum / req.originalAmount );
                }
            });
        }

        $scope.collected = percentage*100 || 0;
        $scope.collectedEssential = percentageEssential/countEssential*100 || 0;

        $scope.collected = Math.round($scope.collected);
        $scope.collectedEssential = Math.round($scope.collectedEssential);
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
            Project.rateProject({pid: $routeParams.id}, {rating: $scope.shownRating, comment: $scope.ratingComment},
                function() {
                    $scope.rateSucces = "SUCCESS";
                    $scope.canRate = false;
                    $scope.hideRating();
                    Project.getAggregatedRating({pid: $routeParams.id},
                        function(rating) {
                            $scope.shownRating = rating.rating;
                            $scope.ratingCount = rating.count;
                        });
                });

        }
    }

    $scope.rateOrganization = function(matchid, orgid) {
        Organization.rateOrganization({id: orgid}, {
            matchid: matchid, rating: $scope.organizationRatings[orgid], comment: ""},
            function() {
                Organization.getAggregatedRating({id: orgid}, function(rating) {
                    $scope.organizationRatings[orgid] = rating.rating;
                    $scope.organizationRatingCounts[orgid] = rating.count;
                });
            });
    }

    $scope.projectApply = function(resourceRequirement, $event) {
        $event.stopPropagation();
        $event.preventDefault();

        $scope.selectedRequirement = resourceRequirement;

        $('#apply').modal('toggle');
    };

    $scope.isAllowedToApply = function() {
        return allowedToApply;
    };

    $scope.selectResourceOffer = function(offer, $event) {
        var $target = $($event.target);

        $target.closest("ul").find(".selected").removeClass("selected");

        if ($target.is("li") === false) {
            $target = $target.closest("li");
        } else {
            $target = $target;
        }

        $target.addClass("selected");
        selectedResourceOffer = offer;
    };

    $scope.projectApplySubmit = function() {
        // submit projectApply request to backend
        //
        // Params
        // $scope.project
        // $scope.selectedRequirement
        // organization
        // selectedResourceOffer
        var req = $scope.selectedRequirement;
        var data = {
            resourceOfferId: selectedResourceOffer.id,
            resourceRequirementId: req.id,
            organizationId: organization.id,
            projectId: $scope.project.id
        }
        Project.apply(data, function(data){
            getOffers();
        });
    };

    function getOffers() {
        Account.get(function (acc) {
            account = acc;
            Organization.get({
                id: acc.organizationId
            }, function (org) {
                organization = org;
                if (organization != null && organization.owner.id === acc.id &&
                    $scope.project.organizationId != organization.id) {
                    // owner
                    allowedToApply = true;
                    Organization.getResourceOffers({
                        id: organization.id
                    }, function (offers) {
                        var arr = [];
                        for (var i = 0, len = offers.length; i < len; i++) {
                            if (offers[i].amount > 0) {
                                arr.push(offers[i]);
                            }
                        }
                        $scope.resourceOffers = arr;
                        if(arr.length == 0){
                            allowedToApply = false;
                        }
                    });
                }
            });
        });
    }

    getOffers();

    if (isNew === false) {
        $scope.update($routeParams.id);
    }
});
