'use strict';

respondecoApp.controller('ResourceController', function($scope, $location, $routeParams, Resource, Account, Organization) {

	$scope.resource = {resourceTags: [], isCommercial: false, isRecurrent: false};
	$scope.organization = null;
	$scope.formSaveError = null;
 	$scope.selectedTags = [];
 	$scope.searchTags = [];

	$scope.resourceSearch = {name: null, organization: null, tags: null, available: false};

	var id = $routeParams.id;
	$scope.isNew = id === 'new';

	var orgId;


	if($location.path() === 'ownresource') {
		Account.get(null, function(account) {
			orgId = account.organizationId;

			$scope.resources = Resource.getByOrgId({id:orgId});
		});
	} else {
		$scope.resources = Resource.query();
	}


	Account.get(null, function(account) {
		$scope.resource.organizationId = account.organizationId;

		$scope.organization = Organization.get({id: account.organizationId}, function(organization) {
			$scope.organization = organization;
		});
	});

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
		$scope.resource.resourceTags = $.map($scope.selectedTags, function(tag) {return tag.name});
		console.log($.map($scope.resource.resourceTags, function(tag) {return {name: tag}}));

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
        if (deleteState === false) {
            $scope.deleteMessage = "resource.own.confirmDelete";
            $scope.deleteType = "danger";
            deleteState = true;
            return;
        }

        deleteState = true;
        Resource.delete({
            id: id
        }, function() {
            $scope.resources = Resource.getByOrgId({id:orgId});
        });
    };

});