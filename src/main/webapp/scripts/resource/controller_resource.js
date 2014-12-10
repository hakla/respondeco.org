'use strict';

respondecoApp.controller('ResourceController', function($scope, $location, $routeParams, Resource, Account, Organization, Project) {

	$scope.resource = {resourceTags: [], isCommercial: false, isRecurrent: false};
	$scope.projects = [];
	$scope.organization = null;
	$scope.formSaveError = null;
 	$scope.selectedTags = [];
 	$scope.searchTags = [];

	$scope.resourceSearch = {name: null, organization: null, tags: null, available: false};
	
	$scope.resourceRequirements = [];
	$scope.showRequirements = false;

	$scope.requests = [];

	var id = $routeParams.id;
	$scope.isNew = id === 'new';

	var orgId;
	var claim = {};

	if($location.path() === '/ownresource') {
		Account.get(null, function(account) {
			orgId = account.organizationId;

			$scope.resources = Resource.getByOrgId({id:orgId});
		});
	} else {
		$scope.resources = Resource.query();
	}

	if($location.path() === '/requests') {
		Account.get(null, function(account) {
			orgId = account.organizationId;
			loadRequests();
		});
	}

	//Claim Resource
	var updateProjects = function() {
		Account.get(null, function(account) {
			orgId = account.organizationId;

			$scope.projects = Project.getProjectsByOrgId({organizationId:orgId}, function() {
				console.log($scope.projects);
			});
		});
	}
		

	$scope.selectProject = function(project) {
		$scope.resourceRequirements = Project.getProjectRequirements({id:project.id}, function() {
			$scope.showRequirements = true;
			claim.projectId = project.id;
		});
	}

	$scope.selectRequirement = function(requirement) {
		claim.resourceRequirementId = requirement.id;
		claim.organizationId = requirement.organizationId;
	}

	$scope.claimResource = function(res) {
		claim.resourceOfferId = res.id;
		updateProjects();
	}

	$scope.clearClaimResource = function() {
		$scope.showRequirements = false;
		claim = {organizationId: null, projectId: null, resourceOfferId: null, resourceRequirementId: null};
		$scope.resourceRequirements = [];
	}

	$scope.sendClaimRequest = function() {
		Resource.claimResource(claim, function() {
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
			loadRequests();
		});
	}

	$scope.declineRequest = function(request) {
		Resource.updateRequest({id:request.matchId},{accepted:false}, function() {
			loadRequests();
		});
	}

	var loadRequests = function() {

		$scope.requests = Organization.getResourceRequests({id:orgId}, function() {
				
		}, function() {
			console.log("error");
		});
	}

	$scope.redirectToOrganization = function() {
		$location.path('organization/' + orgId);
	}


	Account.get(null, function(account) {
		$scope.resource.organizationId = account.organizationId;

		$scope.organization = Organization.get({id: account.organizationId}, function(organization) {
			$scope.organization = organization;
		});
	});

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
		$scope.resourceSearch.tags = $.map($scope.searchTags, function(tag) {return tag.name}).join(","); //create comma separated list
		console.log($scope.resourceSearch.tags);
		Resource.query({
				name: $scope.resourceSearch.name, 
				organization: $scope.resourceSearch.organization,
				tags: $scope.resourceSearch.tags,
				available: $scope.resourceSearch.available,
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

		$scope.resource.resourceTags = $.map($scope.selectedTags, function(tag) {return tag.name});

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

	if($scope.isNew == false) {
		$scope.update(id);
	}

	var deleteState = false;
    $scope.deleteType = "default";
    $scope.deleteMessage = "resource.own.delete";

    $scope.delete = function(id) {
        Resource.delete({
            id: id
        }, function() {
            $scope.resources = Resource.getByOrgId({id:orgId});
        });
    };

});