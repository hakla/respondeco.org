'use strict';

respondecoApp.controller('ResourceController', function($scope, $location, Resource, Account) {

	$scope.resource = {id: null, name: null, description: null, tags: null, amount: null, 
		startDate: null, endDate: null, isCommercial: false, isRecurrent: false, organizationId: null};
	$scope.resources = Resource.query();


	Account.get(null, function(account) {
		$scope.resource.organizationId = account.organizationId;
		console.log($scope.resource.organizationId);
	});

	$scope.redirectToResource = function(id) {
		$location.path('resource/' + id);
	}

	$scope.search = function(filter) {
		Resource.query({filter: filter}, 
			function(res) {
				$scope.resources = res;
			})
	}

	$scope.create = function() {
		if($scope.resource.organizationId == null) {
			console.log("error");

		} else {
			Resource.save($scope.resource, 
			function() {
				$scope.redirectToResource('');
			}, 
			function() {
				$scope.form.saveError = true;
			});
		}
	}

	$scope.delete = function(id) {
		Resource.delete({id: id},
			function() {
				$scope.resources = Resource.query();
			});
	}

	$scope.clear = function() {
		$scope.resource = {id: null, name: null, description: null, tags: null, 
			amount: null, startDate: null, endDate: null, isCommercial: null, isRecurrent: null};
	}

	

});
