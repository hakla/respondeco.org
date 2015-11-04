(function() {
  'use strict';
  respondecoApp.controller('OrganizationController', function($scope, $location, $routeParams, Organization, Account, SocialMedia, AuthenticationSharedService, $rootScope, $filter, Session, IsoCategories) {
    var isOwner, mapToDTO, refreshPostings, user;
    isOwner = false;
    user = void 0;
    $scope.twitterConnected = false;
    $scope.facebookConnected = false;
    $scope.xingConnected = false;
    $scope.shownRating = 0;
    $scope.ratingCount = 0;
    $scope.posting = {};
    $scope.user = function() {
      return user;
    };
    $scope.map = {
      center: {
        latitude: 48,
        longitude: 16
      },
      zoom: 8
    };
    $scope.clearCategory = function(category) {
      $scope._subcategory = null;
      return category;
    };
    $scope.createGrid = function() {
      var filtersCallback, filtersContainer, gridContainer, wrap;
      gridContainer = $('#grid-container');
      filtersContainer = $('#filters-container');
      wrap = void 0;
      filtersCallback = void 0;

      /*******************************
          init cubeportfolio
      #*****************************
       */
      return gridContainer.cubeportfolio({
        layoutMode: 'grid',
        rewindNav: true,
        scrollByPage: false,
        mediaQueries: [
          {
            width: 1100,
            cols: 4
          }, {
            width: 800,
            cols: 4
          }, {
            width: 500,
            cols: 4
          }, {
            width: 320,
            cols: 1
          }
        ],
        defaultFilter: '*',
        animationType: 'rotateSides',
        gapHorizontal: 10,
        gapVertical: 10,
        gridAdjustment: 'responsive',
        caption: 'overlayBottomPush',
        displayType: 'sequentially',
        displayTypeSpeed: 100,
        singlePageInlineDelegate: '.cbp-singlePageInline',
        singlePageInlinePosition: 'below',
        singlePageInlineInFocus: true,
        singlePageInlineCallback: function(url, element) {
          return this.updateSinglePageInline($(element).parents('.cbp-item-wrapper').find('.inline-content').html());
        }
      });
    };
    $scope.isoCategories = IsoCategories.query(function() {
      return $scope.isoCategories = $scope.isoCategories.isocategorys;
    });
    $scope.update = function(name) {
      return $scope.organization = Organization.get({
        id: name,
        fields: 'logo,projects(name,projectLogo,resourceRequirements(amount,originalAmount)),email,description,members,postingFeed(postings(createdDate)),verified,website'
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
        $scope.organization.description = $scope.organization.description || '';
        setTimeout(function() {
          return $scope.createGrid();
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
      if (Session.valid()) {
        return SocialMedia.getConnections(function(response) {
          return response.forEach(function(connection) {
            $scope.twitterConnected = connection.provider === 'twitter';
            $scope.facebookConnected = connection.provider === 'facebook';
            return $scope.xingConnected = connection.provider === 'xing';
          });
        });
      }
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
    $scope.post = function(posting) {
      var ref, ref1;
      if (((ref = posting.information) != null ? ref.length : void 0) < 5 || ((ref1 = posting.title) != null ? ref1.length : void 0) < 5) {
        return;
      }
      return Organization.addPostingForOrganization({
        id: $routeParams.id
      }, posting, function(newPosting) {
        $scope.postings.unshift(newPosting);
        $scope.postingsTotal = $scope.postingsTotal + 1;
        $scope.postings = $scope.postings.slice(0, ($scope.postingPage + 1) * $scope.postingPageSize);
        $scope.posting = {};
        return $scope.postingform.$setPristine();
      });
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
      if (Session.valid()) {
        return Organization.followingState({
          id: $routeParams.id
        }, function(follow) {
          return $scope.following = follow.state;
        });
      }
    };
    $scope.getDonatedResources = function() {
      return Organization.getDonatedResources({
        id: $routeParams.id
      }, {
        pageSize: 20,
        page: 0
      }, function(response) {
        var projects, ref;
        projects = {};
        $scope.donatedResources = response;
        if ((ref = response.resourceMatches) != null) {
          ref.map(function(x) {
            x.project.match = [x];
            return x.project;
          }).forEach(function(project) {
            if (projects[project.id] === void 0) {
              return projects[project.id] = project;
            } else {
              return projects[project.id].match = projects[project.id].match.concat(project.match);
            }
          });
        }
        return $scope.projects = projects;
      });
    };
    mapToDTO = function(model) {
      return {
        name: model.name,
        id: model.id,
        description: model.description,
        email: model.email,
        website: model.website,
        logo: model.logo
      };
    };
    $scope.save = function() {
      return Organization.update(mapToDTO($scope.organization));
    };
    if ($routeParams.id !== 'new' || $routeParams.id !== 'null' || $routeParams.id !== 'undefined') {
      $scope.followingState();
      return $scope.getDonatedResources();
    }
  });

}).call(this);

//# sourceMappingURL=controller_organization.js.map
