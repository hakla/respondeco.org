'use strict';

respondecoApp.controller('SocialMediaController', function($rootScope, $scope, $location, $window, SocialMedia) {

	$scope.code = {string: null}
	$scope.post = {string: null}

	$scope.loading = false;
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
		$scope.loading = true;
		SocialMedia.connectFacebook(function(redirectURL) {
			$window.location.href = redirectURL.string;
		});
	};

	/**
	 *	Calls connectTwitter from the SocialMedia service and redirects
	 *	the user to Twitter, where he can grant permission for
	 *	respondeco.
	 */
	$scope.connectTwitter = function() {
		SocialMedia.connectTwitter(function(redirectURL) {
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
			$window.location.href = redirectURL.string;
		});
	};


	/**
	 * Disconnects the users account from Facebook
	 */ 
	 $scope.disconnectFacebook = function() {
	 	$scope.loading = true;
	 	SocialMedia.disconnectFacebook(function(response) {
	 		$scope.loading = false;
	 		$scope.facebookConnected = false
	 		$scope.addAlert('info','Die Verbindung zwischen ihrem Account und Facebook wurde erfolgreich aufgehoben');
	 	}, function(response) {
	 		console.log(response);
	 	});


	 }

	/**
	 * Disconnects the users account from Twitter
	 */
	$scope.disconnectTwitter = function() {
		SocialMedia.disconnectTwitter(function(response) {
			$scope.twitterConnected = false;
			$scope.addAlert('info','Die Verbindung zwischen ihrem Respondeco-Account und Twitter wurde aufgehoben. '+
				'\nUm den Vorgang abzuschließen gehen Sie bitte auf <a href="https://twitter.com/settings/applications">'+
				'https://twitter.com/settings/applications</a> und widerrufen Sie den Zugriff für Respondeco!');
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
		if(Respondeco.Helpers.Url.param("code") !== undefined && $scope.facebookConnected == false) {
			$scope.code.string = Respondeco.Helpers.Url.param("code");
			SocialMedia.createFacebookConnection($scope.code, function() {
				$scope.clearURL();
				$scope.getConnections();
			});

		};

		// used for twitter after user grants permission and twitter redirects
		if(Respondeco.Helpers.Url.param("oauth_token") !== undefined && $scope.twitterConnected == false) {
			
			var token = Respondeco.Helpers.Url.param("oauth_token");
			var verifier = Respondeco.Helpers.Url.param("oauth_verifier");
			var request = {token: token, verifier: verifier};

			//if oauth verifier length is 4 it's a redirect from xing
			if(verifier.length == 4) {
				console.log("calling create Xing connection")
				SocialMedia.createXingConnection(request, function(response) {
					console.log(response);

					//update connections
					$scope.getConnections();
				});
			} else {
				SocialMedia.createTwitterConnection(request, function(response) {
					$scope.clearURL();

					//update connections
					$scope.getConnections();
				}, function() {
					$scope.clearURL();
				});
			}
		};

		//error case
		if(Respondeco.Helpers.Url.param("error") !== undefined ||
				Respondeco.Helpers.Url.param("denied") !== undefined) {
			$scope.clearURL();
		}
	};
	
	
	/**
	 * Used to add global alert messages to the actual site
	 */
	$scope.addAlert = function(type, message) {
		$rootScope.globalAlerts.push({type:type, msg:message, timeout:3});
	}

	/**
	 * Clears the url from parameters after callback
	 */
	$scope.clearURL = function() {
		$window.location.href = "/#/social-networks";
	}

	$scope.getConnections();
	$scope.checkForRedirectParams();
});