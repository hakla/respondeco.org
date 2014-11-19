'use strict';

respondecoApp.controller('ResourceController', function($scope, $location, Resource) {

	$scope.resource = {id: null, name: null, description: null, tags: null, amount: null, 
		dateStart: null, dateEnd: null, isCommercial: null, isRecurrent: null};
	$scope.resources = Resource.query();

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
		Resource.save($scope.resource, 
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
		$scope.resource = {id: null, name: null, description: null, tags: null, 
			amount: null, dateStart: null, dateEnd: null, isCommercial: null, isRecurrent: null};
	}

});
