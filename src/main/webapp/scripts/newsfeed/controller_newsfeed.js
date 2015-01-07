'use strict';

respondecoApp.controller('NewsfeedController', function($scope, $location, $routeParams, Account) {

	$scope.account = {};

	$scope.onPageLoad = function() {
		Account.get(null, function(account) {
			$scope.account = account;


			Account.getNewsfeed({pageSize: 20, page: 0}, function(news) {
    			$scope.postingPage = news;
	    	});

		});
	}
   
    $scope.onPageLoad();

});