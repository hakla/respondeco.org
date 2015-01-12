'use strict';

respondecoApp.controller('SocialMediaController', function($scope, $location, $routeParams, $window, Resource, Account, SocialMedia) {

	$scope.code = {string: null}

	$scope.connectFacebook = function() {
		console.log("click");
		SocialMedia.connectFacebook(function(redirectURL) {
			console.log(redirectURL.string);
			$window.location.href = redirectURL.string;
		});
	};

	if($location.absUrl().indexOf('?code') > 0) {
		var url = $location.absUrl();
		$scope.code.string = url.substring(url.indexOf('?code')+6, url.length-17)
		console.log(url);
		console.log("CODE");
		console.log($scope.code.string);
		SocialMedia.connectFacebookCreate($scope.code, function(response) {
			console.log(response);
		});

	}

});