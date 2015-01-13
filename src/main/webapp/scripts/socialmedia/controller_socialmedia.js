'use strict';

respondecoApp.controller('SocialMediaController', function($scope, $location, $routeParams, $window, Resource, Account, SocialMedia) {

	$scope.code = {string: null}
	$scope.post = {string: null}

	$scope.twitterConnected = false;
	$scope.facebookConnected = false;
	$scope.xingConnected = false;

	/**
	 *	Gets all active connection for the currently logged in user
	 *	and if the connection for the specific provider exists, it
	 *	sets the connected variable for the appropriate provider to true.	
	 */
	$scope.getConnections = function() {
		SocialMedia.getConnections(function(response) {
			response.forEach(function(connection) {
				if(connection.provider === 'twitter') {
					$scope.twitterConnected = true;
				} else if(connection.provider === 'facebook') {
					$scope.facebookConnected = true;
				} else if(connection.provider === 'xing') {
					$scope.xingConnected = true;
				}
			});
		});
	};

	/**
	 *	Calls connectFacebook from the SocialMedia service and redirects
	 *	the user to facebook, where he can grant permission for
	 *	respondeco.
	 */
	$scope.connectFacebook = function() {
		SocialMedia.connectFacebook(function(redirectURL) {
			$window.location.href = redirectURL.string;
		});
	};

	$scope.connectGoogle = function() {
		SocialMedia.connectGoogle(function(redirectURL) {
			console.log(redirectURL.string);
			$window.location.href = redirectURL.string;
		})
	};

	/**
	 *	Calls connectTwitter from the SocialMedia service and redirects
	 *	the user to Twitter, where he can grant permission for
	 *	respondeco.
	 */
	$scope.connectTwitter = function() {
		SocialMedia.connectTwitter(function(redirectURL) {
			console.log(redirectURL.string);
			$window.location.href = redirectURL.string;
		})
	};

	/**
	 *	Calls connectXing from the SocialMedia service and redirects
	 *	the user to Twitter, where he can grant permission for
	 *	respondeco.
	 */
	$scope.connectXing = function() {
		SocialMedia.connectXing(function(redirectURL) {
			console.log(redirectURL.string);
			$window.location.href = redirectURL.string;
		});
	};




	/**
	 * Disconnects the users account from Twitter
	 */
	$scope.disconnectTwitter = function() {
		//TODO
		$scope.twitterConnected = false;
	};


	/**
	 * Creates a new twitter post on behalf of the user
	 */
	$scope.createTwitterPost = function(post) {
		$scope.post.string = post;
		SocialMedia.createTwitterPost($scope.post, function(response) {
			console.log(response);
		});
	};

	/**
	 * Creates a new facebook post on behalf of the user
	 */
	$scope.createFacebookPost = function() {
		var post = 'Ich habe meinen Facebook-Account gerade mit Respondeco.org verbunden!' ;
		SocialMedia.createFacebookPost({string: post}, function(response) {
			console.log(response);
		});
	};

	/**
	 * Creates a new xing post on behalf of the user
	 */
	$scope.createXingPost = function() {
		var post = 'Ich habe meinen Xing-Account gerade mit Respondeco.org verbunden!' ;
		SocialMedia.createXingPost({string: post}, function(response) {
			console.log(response);
		});
	};

	/**
	 * Used for authorization via OAuth. Therefor the controller checks if
	 * the url contains a parameter which will be sent to the server for
	 * OAuth. Part of OAuth dance.
	 */
	$scope.checkForRedirectParams = function() {

		// used for facebook after user grants permission and facebook redirects
		if(Respondeco.Helpers.Url.param("code") !== undefined) {
			$scope.code.string = Respondeco.Helpers.Url.param("code");
			SocialMedia.createFacebookConnection($scope.code, function(response) {
				console.log(response);

				$scope.getConnections();
				$scope.shareConnectionModal();
			});
		};

		// used for twitter after user grants permission and twitter redirects
		if(Respondeco.Helpers.Url.param("oauth_token") !== undefined) {
			
			var token = Respondeco.Helpers.Url.param("oauth_token");
			var verifier = Respondeco.Helpers.Url.param("oauth_verifier");
			var request = {token: token, verifier: verifier};

			console.log(token);
			console.log(verifier);

			//if oauth verifier length is 4 it's a redirect from xing
			if(verifier.length == 4) {
				console.log("calling create Xing connection")
				SocialMedia.createXingConnection(request, function(response) {
					console.log(response);

					//update connections
					$scope.getConnections();

					$location.search( 'oauth_token', null );
					$scope.shareConnectionModal();

				});
			} else {
				SocialMedia.createTwitterConnection(request, function(response) {
					console.log(response);

					//update connections
					$scope.getConnections();

					$location.search( 'oauth_token', null );
					$scope.shareConnectionModal();

				});
			}
		};
	};
	
	$scope.shareConnectionModal = function() {
        $('#shareConnectionModal').modal('show');
    };

	$scope.checkForRedirectParams();
	$scope.getConnections();

});