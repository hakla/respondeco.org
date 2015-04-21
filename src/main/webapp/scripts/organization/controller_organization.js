'use strict';

respondecoApp.controller('OrganizationController', function($scope, $location, $routeParams,
                                                            Organization, Account, SocialMedia, AuthenticationSharedService, $rootScope) {
    var isOwner = false;
    var user;

    $scope.twitterConnected = false;
    $scope.facebookConnected = false;
    $scope.xingConnected = false;

    $rootScope.title = "Organisation";

    $scope.shownRating = 0;
    $scope.ratingCount = 0;

    $scope.update = function(name) {
        $scope.organization = Organization.get({
            id: name
        }, function() {
            Organization.getMembers({
                id: $scope.organization.id
            }, function(data)  {
                $scope.members = data;
            });

            Account.get(null, function(account) {
                user = account;
                isOwner = user !== undefined && user.login === $scope.organization.owner.login;
            });

            $scope.getConnections();

        });
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

    $scope.refreshOrganizationRating = function() {
        //get new aggregated rating
        Organization.getAggregatedRating({id: $routeParams.id},
            function(rating) {
                $scope.shownRating = rating.rating;
                $scope.ratingCount = rating.count;
            });
    };
    $scope.refreshOrganizationRating();

    $scope.delete = function(id) {
        id = id || $scope.organization.id;

        if (confirm("Wirklich löschen?") === false) return;

        Organization.delete({
                id: id
            },
            function() {
                $scope.organizations = Organization.query();
            });
    };

    $scope.redirectToEdit = function() {
        $location.path('organization/edit/' + $scope.organization.id);
    };

    $scope.isOwner = function() {
        return isOwner;
    };

    $scope.updateUser = function($item, $model, $label) {
        $scope.selectedUser = $item;
    };

    $scope.redirectToOverview = function() {
        $location.path('organization');
    };

    $scope.redirectToOwnResources = function() {
        $location.path('ownresource');
    };

    $scope.redirectToNewProject = function() {
        $location.path('projects/edit/new');
    };

    $scope.redirectToProjects = function() {
        $location.path('organization/' + $scope.organization.id + '/projects');
    };

    $scope.redirectToProject = function(projectId) {
        $location.path('project/' + projectId);
    };

    if ($routeParams.id !== undefined) {
        $scope.update($routeParams.id);
    }


    /**
     * Button Event. Try to follow the current Organization Newsfeed
     */
    $scope.follow = function(){
        Organization.follow({id: $scope.organization.id}, null, function (result) {
            $scope.following = true;
        });
    };

    /**
     * Button Event. Try to un-follow the current Organization Newsfeed
     */
    $scope.unfollow = function(){
        Organization.unfollow({id: $scope.organization.id}, function (result) {
            $scope.following = false;
        });
    };

    /**
     * show or hide the Un-Follow Button. Show only if the current organization is being followed by user
     * @returns {boolean} true => show, else hide
     */
    $scope.showUnfollow = function(){
        return $scope.following == true;// && $scope.isOwner() == false;
    };

    /**
     * show or hide the Follow Button. Show only if the current organization is not being followed by user
     * @returns {boolean} true => show, else hide
     */
    $scope.showFollow = function() {
        return $scope.following == false;// && $scope.isOwner() == false;
    };

    //Posting

    //Posting for project
    $scope.postingPage = -1;
    $scope.postingPageSize = 5;
    $scope.postingInformation = null;
    $scope.postingsTotal = null;
    $scope.postings = [];

    //function to refresh postings for the organization in the scope; get postings in the given pagesize
    var refreshPostings = function() {
        Organization.getPostingsByOrgId({
                id: $routeParams.id,
                page: $scope.postingPage,
                pageSize: $scope.postingPageSize },
            function(data) {
                $scope.postingsTotal = data.totalElements;
                $scope.postings = $scope.postings.concat(data.postings);
            });
    };

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

    $scope.addPosting = function() {
        if($scope.postingInformation.length < 5 || $scope.postingInformation.length > 2048) {
            return;
        }
        Organization.addPostingForOrganization({
            id:$routeParams.id
        }, $scope.postingInformation, function(newPosting) {
            //add new posting and cut array down to current number of shown postings
            $scope.postings.unshift(newPosting);
            $scope.postingsTotal = $scope.postingsTotal + 1;
            $scope.postings = $scope.postings.slice(0, ($scope.postingPage + 1) * $scope.postingPageSize);
            $scope.postingInformation = null;
            $scope.postingform.$setPristine();
        });

        if($scope.postOnTwitter === true) {
            var urlPath = $location.url();
            SocialMedia.createTwitterPost({urlPath: urlPath, post: $scope.postingInformation});
        }

        if($scope.postOnFacebook === true) {
            var urlPath = $location.url();
            SocialMedia.createFacebookPost({urlPath: urlPath, post: $scope.postingInformation});
        }

        if($scope.postOnXing === true) {
            var urlPath = $location.url();
            SocialMedia.createXingPost({urlPath: urlPath, post: $scope.postingInformation});
        }

        $scope.focused = false;
    };

    $scope.deletePosting = function(id) {
        Organization.deletePosting({
                id:$scope.organization.id,
                pid:id
            },
            function() {
                for (var i in $scope.postings) {
                    if ($scope.postings[i].id == id) {
                        $scope.postings.splice(i, 1);
                        break
                    }
                }
            }
        );
    };

    /**
     * Resolve the follow state for the current organization of logged in user
     */
    $scope.followingState = function(){
        Organization.followingState({id: $routeParams.id}, function(follow){
            $scope.following = follow.state;
        });
    };

    $scope.getDonatedResources = function() {
        Organization.getDonatedResources({
        id: $routeParams.id
        }, {
            pageSize: 20,
            page: 0
        }, function(response) {
            var projects = {};
            $scope.donatedResources = response;
            response.resourceMatches.map(function(x) {
                x.project.match = [x];
                return x.project;
            }).forEach(function(project) {
                if (projects[project.id] == undefined) {
                    projects[project.id] = project;
                } else {
                    projects[project.id].match = projects[project.id].match.concat(project.match);
                }
            });

            $scope.projects = projects;
        });
    };

    //allow execute follow state only if organization ID is set!
    if($routeParams.id !== 'new' || $routeParams.id !== 'null' || $routeParams.id !== 'undefined') {
        $scope.followingState();
        $scope.getDonatedResources();
    }
});
