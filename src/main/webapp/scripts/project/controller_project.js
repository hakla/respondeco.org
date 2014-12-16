'use strict';

respondecoApp.controller('ProjectController', function($scope, Project, Organization, ResourceRequirement,
                                                       PropertyTagNames, $location, $routeParams, $sce, $translate,
                                                       Account, $modal) {


    $scope.project = {
        id: null,
        name: null,
        purpose: null,
        concrete: false,
        startDate: null,
        logo: null,
        propertyTags: [],
        resourceRequirements: []
    };
    $scope.projects = Project.query();

    $scope.collected = 0;
    $scope.collectedEssential = 0;

    $scope.resourceMatches = new Object();
    $scope.resourceRequirementsWithMatches = [];

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
    $scope.dateOptions = {
        formatYear: 'yy',
        startingDay: 1
    };

    $scope.openStart = function($event) {
        $event.stopPropagation();
        $scope.openedStartDate = true;
    };

    $scope.onUploadComplete = function(fileItem, response) {
        $scope.project.logo = response;
    };

    $scope.create = function() {
        var startDate = $scope.project.startDate || null;

        if (startDate != null) {
            startDate = new XDate(startDate).toString("yyyy-MM-dd");
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

                //for each match, put it in a map with the id as key
                matches.forEach(function(match) {
                    $scope.resourceMatches[match.matchId] = match;
                });
                //get rating permissions for the matches
                var matchIds = $.map(matches, function(match) {return match.matchId}).join(",");
                Project.checkIfRatingPossible({pid: $routeParams.id, permission: 'matches', matches: matchIds},
                    function(permissions) {
                        permissions.forEach(function(permission) {
                            $scope.matchRatingPermissions[permission.matchid] = permission.allowed;
                        })
                    });

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
                                    $scope.refreshOrganizationRating(match.organization.id);
                            }
                        }
                    });
                });

                calculateCollected();

            });
        });

        Project.editable({
            id: id
        }, function() {
            $scope.editable = true;
        }, function() {
            $scope.editable = false;
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
        $scope.showResourceModal();
        $scope.resource = $scope.project.resourceRequirements[index];
        $scope.selectedResourceTags = $scope.resource.resourceTags;
    };

    $scope.showResourceModal = function() {
        $('#addResource').modal('toggle');
    }

    if (isNew === false) {
        $scope.update($routeParams.id);
    }

    //RATING
    $scope.canRate = false;
    $scope.ratedMatch = null;
    $scope.isRating = false;
    $scope.shownRating = 0;
    $scope.ratingCount = 0;
    $scope.ratingComment = null;
    $scope.currentMatchId = null;
    $scope.currentOrgRating = 0;
    $scope.currentOrgRatingComment = "";

    $scope.ratingSuccess = null;
    $scope.projectRatingError = null;
    $scope.orgRatingError = null;

    $scope.organizationRatings = new Object();
    $scope.matchRatingPermissions = new Object();

    $scope.refreshProjectRating = function() {
        //get new aggregated rating
        Project.getAggregatedRating({pid: $routeParams.id},
            function(rating) {
                $scope.shownRating = rating.rating;
                $scope.ratingCount = rating.count;
            });
        //check if rating is possible
        Project.checkIfRatingPossible({pid: $routeParams.id, permission: 'project'},
            function(permissions) {
                $scope.canRate = permissions[0].allowed;
                $scope.ratedMatch = permissions[0].matchid;
            });
    }
    $scope.refreshProjectRating();

    $scope.refreshOrganizationRating = function(orgid) {
        Organization.getAggregatedRating({id: orgid}, function(rating) {
            $scope.organizationRatings[orgid] = {rating: rating.rating, count: rating.count};
        });
    }

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
            Project.rateProject({pid: $routeParams.id},
                {matchid: $scope.ratedMatch, rating: $scope.shownRating, comment: $scope.ratingComment},
                function() {
                    $scope.ratingSuccess = "SUCCESS";
                    $scope.projectRatingError = null;
                    $scope.hideRating();
                    $scope.clearRating();
                    $scope.refreshProjectRating();
                },
                function(error) {
                    $scope.projectRatingError = "ERROR";
                    $scope.ratingSuccess = null;
                    if(error.status == 400) {
                        console.log("translating " + error.data.key);
                        $translate(error.data.key).then(function(translated) {
                            $scope.setProjectRatingError(translated);
                        });
                    }
                });

        }
    }

    $scope.rateMatch = function(id) {
        if(!$scope.matchRatingPermissions[id]) {
            return;
        }
        $scope.currentMatchId = id;
        var match = $scope.resourceMatches[id];
        $scope.currentOrgRating = $scope.organizationRatings[match.organization.id].rating;
        $scope.showOrgRatingModal();
    }

    $scope.rateOrganization = function() {
        var matchid = $scope.currentMatchId;
        var orgid = $scope.resourceMatches[matchid].organization.id;
        Organization.rateOrganization({id: orgid}, {
            matchid: matchid, rating: $scope.currentOrgRating,
                comment: $scope.currentOrgRatingComment},
            function() {
                $scope.matchRatingPermissions[matchid] = false;
                $scope.refreshOrganizationRating(orgid);
                $scope.clearRating();
            },
            function(error) {
                $scope.orgRatingError = "ERROR";
                if(error.status == 400) {
                    console.log("translating " + error.data.key);
                    $translate(error.data.key).then(function(translated) {
                        $scope.setOrgRatingError(translated);
                    });
                }

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

    $scope.clearRating = function() {
        $scope.hideOrgRatingModal();
        $scope.isRating = false;
        $scope.ratingComment = null;
        $scope.currentMatchId = null;
        $scope.currentOrgRating = 0;
        $scope.currentOrgRatingComment = null;
        $scope.projectRatingError = null;
        $scope.orgRatingError = null;
    }

    $scope.showOrgRatingModal = function() {
        $('#rateMatchModal').modal('show');
    }

    $scope.hideOrgRatingModal = function() {
        $('#rateMatchModal').modal('hide');
    }

    $scope.setProjectRatingError = function(error) {
        $("#projectRatingError").text(error);
    }

    $scope.setOrgRatingError = function(error) {
        $("#orgRatingError").text(error);
    }

});
