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

	$scope.connectGoogle = function() {
		SocialMedia.connectGoogle(function(redirectURL) {
			console.log(redirectURL.string);
			$window.location.href = redirectURL.string;
		})
	}

	$scope.connectTwitter = function() {
		SocialMedia.connectTwitter(function(redirectURL) {
			console.log(redirectURL.string);
		})
	}

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


	if(Respondeco.Helpers.Url.param("oauth_token") !== undefined) {


		var token = Respondeco.Helpers.Url.param("oauth_token");
		var verifier = Respondeco.Helpers.Url.param("oauth_verifier");

		
		var request = {token: token, verifier: verifier};

		SocialMedia.createTwitterConnection(request, function(response) {
			console.log(response);
		});

	}

});