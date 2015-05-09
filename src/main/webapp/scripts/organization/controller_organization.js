(function() {
  'use strict';
  respondecoApp.controller('OrganizationController', function($scope, $location, $routeParams, Organization, Account, SocialMedia, AuthenticationSharedService, $rootScope) {
    var isOwner, refreshPostings, user;
    isOwner = false;
    user = void 0;
    $scope.twitterConnected = false;
    $scope.facebookConnected = false;
    $scope.xingConnected = false;
    $rootScope.title = 'Organisation';
    $scope.shownRating = 0;
    $scope.ratingCount = 0;
    $scope.titlesForView = {
      summary: function() {
        return "";
      },
      about_us: function() {
        return "- Über die Organisation";
      },
      members: function() {
        return "- Mitarbeiter";
      },
      news: function() {
        return "- Neuigkeiten";
      },
      transactions: function() {
        return "- Transaktionsübersicht";
      },
      settings: function() {
        return "- Einstellungen";
      },
      prices: function() {
        return "- Auszeichnungen";
      },
      projects: function() {
        return "- Projekte";
      }
    };
    $scope.view = 'summary';
    $scope.currentView = function() {
      var ref;
      $rootScope.title = ((ref = $scope.organization) != null ? ref.name : void 0) + " " + ($scope.titlesForView[$scope.view]());
      return "/template/organization/" + $scope.view + ".html";
    };
    $scope.update = function(name) {
      return $scope.organization = Organization.get({
        id: name,
        fields: 'logo,projects(name,projectLogo),email,description,members,postingFeed(postings(createdDate))'
      }, function() {
        Organization.getMembers({
          id: $scope.organization.id
        }, function(data) {
          return $scope.members = data;
        });
        Account.get(null, function(account) {
          var ref;
          user = account;
          return isOwner = user !== void 0 && user.login === ((ref = $scope.organization.owner) != null ? ref.login : void 0);
        });
        return $scope.getConnections();
      });
    };

    /*
     *  Gets all active connection for the currently logged in user
     *  and if the connection for the specific provider exists, it
     *  sets the connected variable for the appropriate provider to true.
     */
    $scope.getConnections = function() {
      return SocialMedia.getConnections(function(response) {
        return response.forEach(function(connection) {
          $scope.twitterConnected = connection.provider === 'twitter';
          $scope.facebookConnected = connection.provider === 'facebook';
          return $scope.xingConnected = connection.provider === 'xing';
        });
      });
    };
    $scope.refreshOrganizationRating = function() {
      return Organization.getAggregatedRating({
        id: $routeParams.id
      }, function(rating) {
        $scope.shownRating = rating.rating;
        return $scope.ratingCount = rating.count;
      });
    };
    $scope.refreshOrganizationRating();
    $scope["delete"] = function(id) {
      id = id || $scope.organization.id;
      if (confirm('Wirklich löschen?') !== false) {
        return Organization["delete"]({
          id: id
        }, function() {
          return $scope.organizations = Organization.query();
        });
      }
    };
    $scope.redirectToEdit = function() {
      return $location.path('organization/edit/' + $scope.organization.id);
    };
    $scope.isOwner = function() {
      return isOwner;
    };
    $scope.updateUser = function($item, $model, $label) {
      return $scope.selectedUser = $item;
    };
    $scope.redirectToOverview = function() {
      return $location.path('organization');
    };
    $scope.redirectToOwnResources = function() {
      return $location.path('ownresource');
    };
    $scope.redirectToNewProject = function() {
      return $location.path('projects/edit/new');
    };
    $scope.redirectToProjects = function() {
      return $location.path('organization/' + $scope.organization.id + '/projects');
    };
    $scope.redirectToProject = function(projectId) {
      return $location.path('project/' + projectId);
    };
    if ($routeParams.id !== void 0) {
      $scope.update($routeParams.id);
    }

    /*
     * Button Event. Try to follow the current Organization Newsfeed
     */
    $scope.follow = function() {
      return Organization.follow({
        id: $scope.organization.id
      }, null, function(result) {
        return $scope.following = true;
      });
    };

    /*
     * Button Event. Try to un-follow the current Organization Newsfeed
     */
    $scope.unfollow = function() {
      return Organization.unfollow({
        id: $scope.organization.id
      }, function(result) {
        return $scope.following = false;
      });
    };

    /*
     * show or hide the Un-Follow Button. Show only if the current organization is being followed by user
     * @returns {boolean} true => show, else hide
     */
    $scope.showUnfollow = function() {
      return $scope.following === true;
    };

    /*
     * show or hide the Follow Button. Show only if the current organization is not being followed by user
     * @returns {boolean} true => show, else hide
     */
    $scope.showFollow = function() {
      return $scope.following === false;
    };
    $scope.postingPage = -1;
    $scope.postingPageSize = 5;
    $scope.postingInformation = null;
    $scope.postingsTotal = null;
    $scope.postings = [];
    refreshPostings = function() {
      return Organization.getPostingsByOrgId({
        id: $routeParams.id,
        page: $scope.postingPage,
        pageSize: $scope.postingPageSize
      }, function(data) {
        $scope.postingsTotal = data.totalElements;
        return $scope.postings = $scope.postings.concat(data.postings);
      });
    };
    $scope.canShowMorePostings = function() {
      return $scope.postings.length < $scope.postingsTotal;
    };
    $scope.showMorePostings = function() {
      $scope.postingPage = $scope.postingPage + 1;
      return refreshPostings();
    };
    $scope.showMorePostings();
    $scope.addPosting = function() {
      var urlPath;
      if ($scope.postingInformation.length < 5 || $scope.postingInformation.length > 2048) {
        return;
      }
      Organization.addPostingForOrganization({
        id: $routeParams.id
      }, $scope.postingInformation, function(newPosting) {
        $scope.postings.unshift(newPosting);
        $scope.postingsTotal = $scope.postingsTotal + 1;
        $scope.postings = $scope.postings.slice(0, ($scope.postingPage + 1) * $scope.postingPageSize);
        $scope.postingInformation = null;
        return $scope.postingform.$setPristine();
      });
      urlPath = $location.url();
      if ($scope.postOnTwitter) {
        SocialMedia.createTwitterPost({
          urlPath: urlPath,
          post: $scope.postingInformation
        });
      } else if ($scope.postOnFacebook) {
        SocialMedia.createFacebookPost({
          urlPath: urlPath,
          post: $scope.postingInformation
        });
      } else if ($scope.postOnXing) {
        SocialMedia.createXingPost({
          urlPath: urlPath,
          post: $scope.postingInformation
        });
      }
      return $scope.focused = false;
    };
    $scope.deletePosting = function(id) {
      Organization.deletePosting({
        id: $scope.organization.id,
        pid: id
      }, function() {
        var i;
        for (i in $scope.postings) {
          if ($scope.postings[i].id === id) {
            $scope.postings.splice(i, 1);
            break;
          }
        }
      });
    };

    /**
     * Resolve the follow state for the current organization of logged in user
     */
    $scope.followingState = function() {
      return Organization.followingState({
        id: $routeParams.id
      }, function(follow) {
        return $scope.following = follow.state;
      });
    };
    $scope.getDonatedResources = function() {
      return Organization.getDonatedResources({
        id: $routeParams.id
      }, {
        pageSize: 20,
        page: 0
      }, function(response) {
        var projects;
        projects = {};
        $scope.donatedResources = response;
        response.resourceMatches.map(function(x) {
          x.project.match = [x];
          return x.project;
        }).forEach(function(project) {
          if (projects[project.id] === void 0) {
            return projects[project.id] = project;
          } else {
            return projects[project.id].match = projects[project.id].match.concat(project.match);
          }
        });
        return $scope.projects = projects;
      });
    };
    if ($routeParams.id !== 'new' || $routeParams.id !== 'null' || $routeParams.id !== 'undefined') {
      $scope.followingState();
      return $scope.getDonatedResources();
    }
  });

}).call(this);

//# sourceMappingURL=controller_organization.js.map
