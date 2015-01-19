'use strict';

respondecoApp.controller('ProjectController', function($scope, Project, Organization, ResourceRequirement,
                                                       PropertyTagNames, $location, $routeParams, $sce, $translate,
                                                       Account, $modal, AuthenticationSharedService, SocialMedia) {

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

    $scope.twitterConnected = false;
    $scope.facebookConnected = false;
    $scope.xingConnected = false;

    //initial latlng coordinates belong to Austria (via googleplaces)
    $scope.map = { control: {}, center: { latitude: 47.516231, longitude: 14.550072 }, zoom: 7 };

    $scope.marker = {
      id: 0,
      options: { draggable: true },
      coords: {latitude: null, longitude: null}
    };

    /**
     * $scope.placeToMarker
     *
     * @description This function is called whenever a new place is entered in the searchbox.
     * Therefor the map position is set to the found place and the coordinates of the marker
     * are also set.
     * @param searchBox input field for search
     */
    $scope.placeToMarker = function(searchBox) {
        var place = searchBox.getPlaces();

        if(!place || place == 'undefined' || place.length == 0) {
            return;
        }

        $scope.map.control.refresh({latitude: place[0].geometry.location.lat(), longitude: place[0].geometry.location.lng()});

        $scope.marker.coords = {latitude: place[0].geometry.location.lat(), longitude: place[0].geometry.location.lng()};
        $scope.marker.address = place[0].formatted_address;

        $scope.map.zoom = 14;
    };

    var searchBoxEvents = {
        places_changed: function (searchBox) {
            var id = 0;
            $scope.placeToMarker(searchBox, id);
        }
    };

    $scope.searchBox = { template:'searchBox.template.html', events:searchBoxEvents, parentdiv: "searchBoxParent"};

    // details mock
    $scope.status = {
        open1: true
    };

    /**
     *  Gets all active connection for the currently logged in user
     *  and if the connection for the specific provider exists, it
     *  sets the connected variable for the appropriate provider to true.
     */
    $scope.getConnections = function() {
        SocialMedia.getConnections(function(response) {
            response.forEach(function(connection) {
                if(connection.provider === 'twitter') {
                    $scope.twitterConnected = true;
                } else if(connection.provider === 'facebook') {
                    $scope.facebookConnected = true;
                } else if(connection.provider === 'xing') {
                    $scope.xingConnected = true;
                }
            })
        });
    };

    /**
     * Resolve the follow state for the current project of logged in user
     */
    $scope.followingState = function(){
        Project.followingState({id: $routeParams.id}, function(follow){
            $scope.following = follow.state;
        });
    };

    var searchText = null;
    $scope.isNew = $routeParams.id === 'new' || $routeParams.id === 'null' || $routeParams.id === 'undefined';

    //allow execute follow state only if project ID is set!
    if ($scope.isNew == false){
        $scope.followingState();
    }

    // project apply

    $scope.ProjectApply =
    {
        account: null,
        allowedToApply: false,
        selectedResourceOffer: null,
        selectedRequirement: null
    };

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

        if($scope.marker.coords.latitude !== null) {
            project.projectLocation = {
                address: $scope.marker.address,
                latitude: $scope.marker.coords.latitude,
                longitude: $scope.marker.coords.longitude,
                projectId: $scope.project.id}
        }

        Project[$scope.isNew ? 'save' : 'update'](project,
            function() {
                $scope.projects = Project.query();
                $scope.clear();
            });
    };

    $scope.edit = function() {
        $location.path("/projects/edit/" + $scope.project.id)
    };

    $scope.createStaticMapLink = function() {
        var lat = $scope.project.projectLocation.latitude;
        var lng = $scope.project.projectLocation.longitude;
        var zoom = 14;

        var link = "https://maps.google.com/maps/api/staticmap?center=" + lat + "%2C" +
        + lng +"&format=jpg&maptype=terrain&size=533x190&zoom=" + zoom + "&markers=" + lat + "%2C" +
        lng;

        return link;
    };

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

            //google maps
            if(typeof $scope.project.projectLocation !== 'undefined') {
                if($location.path().indexOf('edit') < 0) {
                    $scope.staticMap = $scope.createStaticMapLink();
                } else {
                    $scope.map.control.refresh({latitude: $scope.project.projectLocation.latitude, longitude: $scope.project.projectLocation.longitude});
                    $scope.marker.coords = {latitude: $scope.project.projectLocation.latitude, longitude: $scope.project.projectLocation.longitude};
                    $scope.marker.address = $scope.project.projectLocation.address;
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
            $scope.getConnections();
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
    };

    $scope.delete = function() {
        Project.delete({
                id: $routeParams.id
            },
            function() {
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
    };
    $scope.selectedResourceTags = [];

    $scope.createRequirement = function() {
        $scope.resource.resourceTags = $scope.selectedResourceTags;
        var resource = $scope.resource;

        if (edit == false) {
            $scope.project.resourceRequirements.push(resource);
        }
    };

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
    };

    $scope.editRequirement = function(index) {
        edit = true;
        $scope.showResourceModal();
        $scope.resource = $scope.project.resourceRequirements[index];
        $scope.selectedResourceTags = $scope.resource.resourceTags;
    };

    $scope.showResourceModal = function() {
        $('#addResource').modal('toggle');
    };

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
    };
    $scope.refreshProjectRating();

    $scope.refreshOrganizationRating = function(orgid) {
        Organization.getAggregatedRating({id: orgid}, function(rating) {
            $scope.organizationRatings[orgid] = {rating: rating.rating, count: rating.count};
        });
    };

    $scope.showRating = function() {
        if($scope.canRate) {
            $scope.isRating = true;
        }
    };

    $scope.hideRating = function() {
        $scope.isRating = false;
    };

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
    };

    $scope.rateMatch = function(id) {
        if(!$scope.matchRatingPermissions[id]) {
            return;
        }
        $scope.currentMatchId = id;
        var match = $scope.resourceMatches[id];
        $scope.currentOrgRating = $scope.organizationRatings[match.organization.id].rating;
        $scope.showOrgRatingModal();
    };

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
    };

    $scope.projectApply = function(resourceRequirement, $event) {
        $event.stopPropagation();
        $event.preventDefault();
        resourceRequirement.$target = $($event.target).closest("div.panel-heading");
        $scope.ProjectApply.selectedRequirement = resourceRequirement;

        $('#apply').modal('toggle');
    };

    $scope.isAllowedToApply = function() {
        return $scope.ProjectApply.allowedToApply;
    };

    $scope.selectResourceOffer = function(offer, $event) {
        var $target = $($event.target);

        var selected = $target.closest("ul").find(".selected");
        selected.removeClass("selected");

        if ($target.is("li") === false) {
            selected = $target = $target.closest("li");
        }
        offer.$target = selected;

        $target.addClass("selected");
        $scope.ProjectApply.selectedResourceOffer = offer;
    };

    $scope.projectApplySubmit = function($event) {
        var data = {
            resourceOfferId: $scope.ProjectApply.selectedResourceOffer.id,
            resourceRequirementId: $scope.ProjectApply.selectedRequirement.id,
            organizationId: $scope.ProjectApply.account.organization.id,
            projectId: $scope.project.id
        };
        // please do not remove this variable. some user operation can be faster than timeout.
        // this cause an exception.
        var reqTarget = $scope.ProjectApply.selectedRequirement.$target;
        Project.apply(data, function(data){
            $scope.ProjectApply.selectedResourceOffer.$target.removeClass("selected");
            $scope.ProjectApply.selectedResourceOffer = null;
            $scope.ProjectApply.selectedRequirement = null;
            $scope.getOffers();
        }, function(error){
            reqTarget.css("background-color", "#EBCCD1");
            setTimeout(function(){ reqTarget.css("background-color", ""); }, 5000);
        });
    };

    /**
     * Get all resource offer from the organization the user currently in.
     */
    $scope.getOffers = function() {
        //first of all we need the account information!
        Account.get(function (acc) {
            $scope.ProjectApply.account = acc;
            //since organization is a part of an account, we do not need to load organization over the rest
            if(
                $scope.ProjectApply.account.organization &&
                $scope.project.organizationId != $scope.ProjectApply.account.organization.id
            ){
                //get available resource
                $scope.ProjectApply.allowedToApply = true;
                Organization.getResourceOffers({
                    id: $scope.ProjectApply.account.organization.id
                }, null, function (offers) {
                    var arr = [];
                    //and remove those who have no amount.
                    for (var i = 0, len = offers.length; i < len; i++) {
                        if (offers[i].amount > 0) {
                            arr.push(offers[i]);
                        }
                    }
                    $scope.resourceOffers = arr;
                    //if array is empty, the current account do not have any resources to apply
                    if(arr.length == 0){
                        $scope.ProjectApply.allowedToApply = false;
                    }
                });
            }
            else{
                $scope.ProjectApply.allowedToApply = false;
            }
        });
    };

    $scope.getOffers();

    if ($scope.isNew === false) {
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
    };

    $scope.showOrgRatingModal = function() {
        $('#rateMatchModal').modal('show');
    };

    $scope.hideOrgRatingModal = function() {
        $('#rateMatchModal').modal('hide');
    };

    $scope.setProjectRatingError = function(error) {
        $("#projectRatingError").text(error);
    };

    $scope.setOrgRatingError = function(error) {
        $("#orgRatingError").text(error);
    };

    /**
     * Button Event. Try to follow the current Project Newsfeed
     */
    $scope.follow = function(){
        Project.follow({id: $scope.project.id}, null, function () {
            $scope.following = true;
        });
    };

    /**
     * Button Event. Try to un-follow the current Project Newsfeed
     */
    $scope.unfollow = function(){
        Project.unfollow({id: $scope.project.id}, function () {
            $scope.following = false;
        });
    };

    /**
     * show or hide the Un-Follow Button. Show only if the current project is being followed by user
     * @returns {boolean} true => show, else hide
     */
    $scope.showUnfollow = function(){
        return $scope.following == true;// && $scope.editable == false;
    };

    /**
     * show or hide the Follow Button. Show only if the current project is not being followed by user
     * @returns {boolean} true => show, else hide
     */
    $scope.showFollow = function() {
        return $scope.following == false;// && $scope.editable == false;
    };

    //Posting for project
    $scope.postingPage = -1;
    $scope.postingPageSize = 5;
    $scope.postingInformation = null;
    $scope.postingsTotal = null;
    $scope.postings = [];

    //function to refresh postings for the project in the scope; get postings in the given pagesize
    var refreshPostings = function() {
        Project.getPostingsByProjectId({
            id: $routeParams.id,
            page: $scope.postingPage,
            pageSize: $scope.postingPageSize },
            function(data) {
                $scope.postingsTotal = data.totalElements;
                $scope.postings = $scope.postings.concat(data.postings);
        });
    };

    var resetPostings = function() {
        $scope.postingPage = -1;
        $scope.postingsTotal = null;
        $scope.postings = [];
        $scope.showMorePostings();
    }

    $scope.canShowMorePostings = function() {
        return $scope.postings.length < $scope.postingsTotal;
    };

    //shows more postings by incrementing the postingCount (default 5 + 5)
    $scope.showMorePostings = function() {
        $scope.postingPage = $scope.postingPage + 1;
        refreshPostings();
    };

    //show first page of postings
    $scope.showMorePostings();

    //method to add postings for the project in the scope; lenght of posting has to be at least 5 char and
    // at max 2048 chars long; refreshing postings after adding
    $scope.addPosting = function() {
        if($scope.postingInformation.length < 5 || $scope.postingInformation.length > 2048) {
            return;
        }
        Project.addPostingForProject({id:$routeParams.id}, $scope.postingInformation,
            function(newPosting) {
                //add new posting and cut array down to current number of shown postings
                $scope.postings.unshift(newPosting);
                $scope.postingsTotal = $scope.postingsTotal + 1;
                $scope.postings = $scope.postings.slice(0, ($scope.postingPage + 1) * $scope.postingPageSize);
                $scope.postingInformation = null;
                $scope.postingform.$setPristine();
            });

        //post on social media
        if($scope.postOnTwitter === true) {
            SocialMedia.createTwitterPost({string: $scope.postingInformation});
        }

        if($scope.postOnFacebook === true) {
            SocialMedia.createFacebookPost({string: $scope.postingInformation});
        }

        if($scope.postOnXing === true) {
            var urlPath = $location.url();
            SocialMedia.createXingPost({urlPath: urlPath, post: $scope.postingInformation});
        }

    };

    //delete posting and refresh after deletion
    $scope.deletePosting = function(id) {
        Project.deletePosting({id:$scope.project.id,
            pid:id},
            function() {
            refreshPostings();
        });
    };

});
