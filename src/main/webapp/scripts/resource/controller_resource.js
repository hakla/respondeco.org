'use strict';

respondecoApp.controller('ResourceController', function($scope, $location, $routeParams, Resource, Account, Organization, Project, $filter) {

	var PAGESIZE = 20;

	$scope.resource = {resourceTags: [], isCommercial: false};
	$scope.projects = [];
	$scope.organization = null;
	$scope.formSaveError = null;
 	$scope.selectedTags = [];
 	$scope.searchTags = [];

	$scope.resourceSearch = {name: null, isFree: null};

	$scope.resourceRequirements = [];
	$scope.showRequirements = false;

	$scope.requests = [];

	var id = $routeParams.id;
	$scope.isNew = id === 'new';
	$scope.isOverview = id === undefined;

	$scope.orgId = null;
	$scope.claim = {};

	$scope.currentPage;

	$scope.filter = {pageSize:PAGESIZE};

	$scope.totalItems = 0;

	$scope.getAccount = function() {
		Account.get(null, function(account) {
				if (account.organization != null) {
	  			$scope.orgId = account.organization.id;
	  		}

	  		if($location.path() === '/ownresource') {
		    	Resource.getByOrgId({
		        	id: $scope.orgId
		      	}, function(data) {
		      		$scope.resources = data;
		      	});
		  	} else {
		  		$scope.search();
			}

			if($location.path() === '/requests') {
				$scope.loadRequests();
			}

			if($scope.isNew === false && $scope.isOverview === false) {
				$scope.update(id);
			}

			if (account.organization != null) {
				$scope.organization = account.organization;
			}
		});
	};

	$scope.onPageChange = function() {
		$scope.filter.page = $scope.currentPage-1;;

		Resource.query($scope.filter, function(response) {
			$scope.resources = response.resourceOffers;
    		$scope.totalItems = response.totalItems;
		})
	}

	$scope.onUploadComplete = function(fileItem, response) {
    	$scope.resource.logoId = response.id;
    	$scope.logo = response;
  	};

	//Claim Resource
	$scope.updateProjects = function() {
		Project.getProjectsByOrgId({organizationId:$scope.orgId}, function(response) {
			$scope.projects = response.projects;

			$scope.projects.forEach(function(project, key) {
				if (project.name === 'ip') {
					project.name = $scope.organization.name;
					project.initialProject = true;
					$scope.projects.splice(key, 1);
				}
			});

			// reverse the array so the projects are ordered by creation date
			// $scope.projects = $filter('orderBy')($scope.projects, "id", true);
		});
	};

	$scope.selectProject = function(project, $event) {
		if (project.initialProject == null || project.initialProject === false) {
			$scope.resourceRequirements = Project.getProjectRequirements({id:project.id}, function() {
				$scope.showRequirements = true;
				$scope.claim.projectId = project.id;
			});
		} else {
			$scope.showRequirements = false;
			$scope.claim.projectId = project.id;
			$scope.clamin.organizationId = $scope.organization.id;
		}
	}

	$scope.selectRequirement = function(requirement, $event) {
		var $target = $($event.target);

		$target.closest("ul").find(".selected").removeClass("selected");

		if ($target.is("li") === false) {
			$target = $target.closest("li");
		} else {
			$target = $target;
		}

		$target.addClass("selected");

		$scope.claim.resourceRequirementId = requirement.id;
		$scope.claim.organizationId = requirement.organizationId;
	}

	$scope.claimResource = function(res) {
		$scope.claim.resourceOfferId = res.id;
		$scope.updateProjects();
	}

	$scope.clearClaimResource = function() {
		$scope.showRequirements = false;
		$scope.claim = {organizationId: null, projectId: null, resourceOfferId: null, resourceRequirementId: null};
		$scope.resourceRequirements = [];
	}

	$scope.isClaimable = function(resource) {
		var isClaimable = true;

		if(resource.organization.id == $scope.orgId || $scope.orgId == undefined) {
			isClaimable = false;
		}

		return isClaimable;
	}


	$scope.sendClaimRequest = function() {
		Resource.claimResource($scope.claim, function() {
			$scope.clearClaimResource();
		}, function(data) {
			$scope.claimError = data.key;
		});
	}

	$scope.redirectToProject = function(id) {
		$location.path('projects/'+id);
	}

	$scope.acceptRequest = function(request) {
		Resource.updateRequest({id:request.matchId},{accepted:true}, function() {
			$scope.loadRequests();
		});
	}

	$scope.declineRequest = function(request) {
		Resource.updateRequest({id:request.matchId},{accepted:false}, function() {
			$scope.loadRequests();
		});
	}

	$scope.loadRequests = function() {
		Organization.getResourceRequests({id:$scope.orgId}, function(data) {
			$scope.requests = data;
		}, function() {
			console.log("error");
		});
	}

	$scope.redirectToOrganization = function() {
		$location.path('organization/' + $scope.orgId);
	}

	//DatePicker
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

	$scope.redirectToResource = function(id) {
		$location.path('resource/' + id);
	}

	$scope.redirectToOwnResource = function() {
		$location.path('ownresource');
	}

	$scope.search = function() {
		$scope.filter.name = $scope.resourceSearch.name;

		if ($scope.resourceSearch.isFree === true) {
			$scope.filter.commercial = false;
		} else {
			$scope.filter.commercial = undefined;	
		}

		$scope.currentPage = 1;
		$scope.filter.page = 0;

		Resource.query($scope.filter, function(response) {
				$scope.resources = response.resourceOffers;
				if($scope.resources.length == 0) {
					$scope.noResourcesFound = true;
				} else {
					$scope.noResourcesFound = false;
				}

				$scope.totalItems = response.totalItems;
			}, function(error) {
				$scope.searchError = true;
			});
	}

	$scope.create = function() {
		//startDate has to be earlier than endDate
		if(new XDate($scope.resource.startDate).diffDays(new XDate($scope.resource.endDate)) >= 0) {
			$scope.resource.startDate = new XDate($scope.resource.startDate).toString("yyyy-MM-dd");
			$scope.resource.endDate = new XDate($scope.resource.endDate).toString("yyyy-MM-dd");

			$scope.resource.organizationId = $scope.orgId;
			$scope.resource.resourceTags = $.map($scope.selectedTags, function(tag) {return tag.name}); //object-array to string-array

			Resource[$scope.isNew ? 'save' : 'update']($scope.resource,
			function() {
				$scope.redirectToOwnResource('');
			},
			function(error) {
				console.log(error);
				$scope.formSaveError = true;
			});
		} else {
			$scope.errorEndDateBeforeStartDate = true;
		}
	}

	$scope.update = function(id) {
		Resource.get({id: id}, function(resource) {
			$scope.resource = resource;
      		$scope.logo = resource.logo;

			$scope.selectedTags = $scope.resource.resourceTags;

		}, function() {
			$scope.redirectToResource('new');
		});
	}

	$scope.clear = function() {
		$scope.resource = {id: null, name: null, description: null, resourceTags: [],
			amount: null, startDate: null, endDate: null, isCommercial: false};
	}

	var deleteState = false;
    $scope.deleteType = "default";
    $scope.deleteMessage = "resource.own.delete";

    $scope.delete = function(id) {
        Resource.delete({
            id: id
        }, function() {
            $scope.resources = Resource.getByOrgId({id:$scope.orgId});
        });
    };

	$scope.getAccount();

});
