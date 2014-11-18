'use strict';

respondecoApp.controller('ResourceController', function($scope, $location, Resource) {

	$scope.resource = {id: null, name: null, amount: null, dateStart: null, dateEnd: null, isCommercial: null, isRecurrent: null};
	$scope.resources = Resource.query();


	$scope.redirectToResource = function(name) {
		$location.path('resource/' + name);
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
				$scope.resources = Resource.query();
				$('#newResourceModal').modal('hide');
				$scope.clear();
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
		$scope.resource = {id: null, name: null, amount: null, dateStart: null, dateEnd: null, isCommercial: null, isRecurrent: null};
	}



});
