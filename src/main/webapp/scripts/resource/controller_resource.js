'use strict';

respondecoApp.controller('ResourceController', function($scope, $location, $routeParams, Resource, Account, Organization, Project) {

	$scope.resource = {resourceTags: [], isCommercial: false, isRecurrent: false};
	$scope.projects = [];
	$scope.organization = null;
	$scope.formSaveError = null;
 	$scope.selectedTags = [];
 	$scope.searchTags = [];

	$scope.resourceSearch = {name: null, isCommercial: null};

	$scope.resourceRequirements = [];
	$scope.showRequirements = false;

	$scope.requests = [];

	var id = $routeParams.id;
	$scope.isNew = id === 'new';

	$scope.orgId = null;
	$scope.claim = {};

	$scope.resources = Resource.query();

	$scope.getAccount = function() {
		Account.get(null, function(account) {
	  		$scope.orgId = account.organizationId;

	  		if($location.path() === '/ownresource') {
		    	Resource.getByOrgId({
		        	id: $scope.orgId
		      	}, function(data) {
		      		$scope.resources = data;
		      	});
		  	} else {
		    	$scope.resources = Resource.query();
			}

			if($location.path() === '/requests') {
				$scope.loadRequests();
			}

			if($scope.isNew == false) {
				$scope.update(id);
			}

			$scope.organization = Organization.get({id: account.organizationId}, function(organization) {
				$scope.organization = organization;
			});
		});

		
	};

	//Claim Resource
	$scope.updateProjects = function() {
		$scope.projects = Project.getProjectsByOrgId({organizationId:$scope.orgId}, function() {
			console.log($scope.projects);
		});
	}

	$scope.selectProject = function(project) {
		$scope.resourceRequirements = Project.getProjectRequirements({id:project.id}, function() {
			$scope.showRequirements = true;
			$scope.claim.projectId = project.id;
		});
	}

	$scope.selectRequirement = function(requirement) {
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

		if(resource.organization.id == $scope.orgId) {
			isClaimable = false;
		}

		console.log(resource.name);
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
		Resource.query({
				name: $scope.resourceSearch.name,
				commercial: $scope.resourceSearch.isCommercial
			}, function(res) {
				$scope.resources = res;
			}, function(error) {
				$scope.searchError = true;
			});
	}

	$scope.create = function() {
        $scope.resource.startDate = new XDate($scope.resource.startDate).toString("yyyy-MM-dd");
        $scope.resource.endDate = new XDate($scope.resource.endDate).toString("yyyy-MM-dd");

		$scope.resource.resourceTags = $.map($scope.selectedTags, function(tag) {return tag.name}); //object-array to string-array

		Resource[$scope.isNew ? 'save' : 'update']($scope.resource,
		function() {
			$scope.redirectToOwnResource('');
		},
		function() {
			$scope.formSaveError = true;
		});
	}

	$scope.update = function(id) {
		Resource.get({id: id}, function(resource) {
			$scope.resource = resource;

			$scope.selectedTags = $.map($scope.resource.resourceTags, function(tag) {return {name: tag}}); //string-array to object-array
		}, function() {
			$scope.redirectToResource('new');
		});
	}

	$scope.clear = function() {
		$scope.resource = {id: null, name: null, description: null, resourceTags: [],
			amount: null, startDate: null, endDate: null, isCommercial: false, isRecurrent: false};
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
