'use strict';

respondecoApp.controller('ResourceController', function($scope, $location, $routeParams, Resource, Account, Organization) {

	$scope.resource = {resourceTags: [], isCommercial: false, isRecurrent: false};
	$scope.organization = null;

	var id = $routeParams.id;
	var isNew = id === 'new';

	$scope.resources = Resource.query();

	Account.get(null, function(account) {
		$scope.resource.organizationId = account.organizationId;

		$scope.organization = Organization.getById({id: account.organizationId}, function(organization) {
			$scope.organization = organization;
		});
	});



	$scope.redirectToResource = function(id) {
		$location.path('resource/' + id);
	}

	$scope.search = function(filter) {
		Resource.query({filter: filter}, 
			function(res) {
				$scope.resources = res;
			});
	}

	$scope.update = function(id) {
		console.log("update" + id);
		Resource.get({id: id}, function(resource) {
			$scope.resource = resource;
		});
	}

	$scope.create = function() {
		Resource[isNew ? 'save' : 'update']($scope.resource, 
		function() {	
			$scope.redirectToResource('');
		}, 
		function() {
			$scope.form.saveError = true;
		});
	}

	$scope.delete = function(id) {
		Resource.delete({id: id},
			function() {
				$scope.resources = Resource.query();
			});
	}

	$scope.clear = function() {
		$scope.resource = {id: null, name: null, description: null, resourceTags: null, 
			amount: null, startDate: null, endDate: null, isCommercial: null, isRecurrent: null};
	}

	if(isNew == false) {
		$scope.update(id);
	}

});
