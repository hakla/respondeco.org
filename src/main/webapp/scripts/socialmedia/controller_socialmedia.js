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
		}, function() {
			$scope.addAlert('danger', "Facebook konnte nicht verbunden werden!");
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
		}, function() {
			$scope.addAlert('danger', "Twitter konnte nicht verbunden werden!");
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
		}, function() {
			$scope.addAlert('danger', "Xing konnte nicht verbunden werden");
		});
	};


	/**
	 * Disconnects the users account from Facebook
	 */ 
	 $scope.disconnectFacebook = function() {
	 	$scope.loading = true;
	 	SocialMedia.disconnectFacebook(function() {
	 		$scope.loading = false;
	 		$scope.facebookConnected = false
	 		$scope.addAlert('info','Die Verbindung zwischen ihrem Account und Facebook wurde erfolgreich aufgehoben');
	 	}, function() {
	 		$scope.addAlert('danger', "Die Verbindung konnte nicht getrennt werden! Der Server ist zurzeit nicht erreichbar");
	 	});


	 }

	/**
	 * Disconnects the users account from Twitter
	 */
	$scope.disconnectTwitter = function() {
		SocialMedia.disconnectTwitter(function() {
			$scope.twitterConnected = false;
			$scope.addAlert('info','Die Verbindung zwischen ihrem Respondeco-Account und Twitter wurde aufgehoben. '+
				'\nUm den Vorgang abzuschließen gehen Sie bitte auf <a href="https://twitter.com/settings/applications">'+
				'https://twitter.com/settings/applications</a> und widerrufen Sie den Zugriff für Respondeco!');
		});
	};


	/**
	 * Disconnects the users account from Xing
	 */
	$scope.disconnectXing = function() {
		SocialMedia.disconnectXing(function() {
			$scope.xingConnected = false;
			$scope.addAlert('info', 'Die Verbindung zwischen ihrem Respondeco-Account und Xing wurde aufgehoben. ' +
					'\nUm den Vorgang abzuschließen gehen Sie bitte auf <a href="https://www.xing.com/app/settings?op=privacy">' +
					'https://www.xing.com/app/settings</a> und widerrufen Sie den Zugriff für Respondeco!');
		});
	}

	/**
	 * This function is used for getting the URL Parameters after returning from socialmedia platforms
	 */
	$scope.getUrlParameter = (function() {
		// Contains all query parameters
		var params = {};

		// Split search parameters by & and iterate over all query parts (x=y)
		$.each($window.document.location.search.substring(1).split(/[&]/), function() {
			// Split query part by =
			var param = this.split("=");

			// query part 0 == key
			// query part 1 == value
			params[param[0]] = param[1];
		});

		return {
			param: function(name) {
				// if no parameter was defined return the params object
				if (name === undefined) {
					return params;
				}

				// if the parameter *name* isset then return it
				if (params[name] !== undefined) {
					return params[name];
				}

				// otherwise return undefined
				return undefined;
			}
		}
	})();



	/**
	 * Used for authorization via OAuth. Therefor the controller checks if
	 * the url contains a parameter which will be sent to the server for
	 * OAuth. Part of OAuth dance.
	 */
	$scope.checkForRedirectParams = function() {

		var url = $location.absUrl();

		//used for xing callback after user grants permission for respondeco using his xing account
		//parse last 4 digits 
		var verifier = parseInt(url.substring(url.length-4));

		if(isNaN(verifier) === false && $scope.xingConnected === false) {
				//we only need the verifier here, because the token gets persisted from getAuthorizationURL step
				SocialMedia.createXingConnection({string: verifier}, function(response) {
					$scope.clearURL();
					$scope.getConnections();
				}, function() {
					$scope.clearURL();
				});
		}

		// used for facebook after user grants permission and facebook redirects
		if($scope.getUrlParameter.param("code") !== undefined && $scope.facebookConnected === false) {
			$scope.code.string = $scope.getUrlParameter.param("code");
			SocialMedia.createFacebookConnection($scope.code, function() {
				$scope.clearURL();
				$scope.getConnections();
			}, function() {
				$scope.clearURL();
			});

		};

		// used for twitter after user grants permission and twitter redirects
		if($scope.getUrlParameter.param("oauth_token") !== undefined && $scope.twitterConnected === false) {
			
			var token = $scope.getUrlParameter.param("oauth_token");
			var verifier = $scope.getUrlParameter.param("oauth_verifier");
			var request = {token: token, verifier: verifier};

			SocialMedia.createTwitterConnection(request, function(response) {
				$scope.clearURL();
				$scope.getConnections();
			}, function() {
				$scope.clearURL();
			});
		};

		//error case
		if($scope.getUrlParameter.param("error") !== undefined ||
				$scope.getUrlParameter.param("denied") !== undefined) {
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