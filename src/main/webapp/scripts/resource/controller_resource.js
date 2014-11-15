'use strict';

respondecoApp.controller('ResourceController', function($scope, Resource, resolvedResources) {

	$scope.resources = Resource.query();

	$scope.search = function(filter) {
		//TODO
	}

	$scope.create = function() {
		Resource.save($scope.resource, 
			function() {
				$('#newResourceModal').modal('hide');
				$scope.clear();
			}, 
			function() {
				$scope.form.saveError = true;
		});
		console.log($scope.resource);
	}

	$scope.delete = function(id) {
		Resource.delete({id: id},
			function() {
				$scope.resources = Resource.query();
			});
	}

	$scope.clear = function() {
		$scope.resource = {id: null, name: null, amount: null, dateStart: null, dateEnd: null, isCommercial: null, isReccurent: null};
	}



});
