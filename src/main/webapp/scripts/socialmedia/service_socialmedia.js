'use strict';

respondecoApp.factory('SocialMedia', function ($resource) {
	return $resource('app/rest/connect/', {}, {
		'getConnections' : {method: 'GET', isArray:true, url: 'app/rest/connections'},
		'connectFacebook' : { method: 'GET', url: 'app/rest/connect/facebook'},
		'connectFacebookCreate' : {method: 'POST', url: 'app/rest/connect/facebook/createconnection'},
		'connectGoogle' : {method: 'GET', url: 'app/rest/connect/google'},
		'connectTwitter' : {method: 'GET', url: 'app/rest/connect/twitter'},
		'createTwitterConnection' : {method: 'POST', url: 'app/rest/connect/twitter/createconnection'},
		'createTwitterPost' : {method: 'POST', url: 'app/rest/socialmedia/twitter/post'},
		'createFacebookPost' : {method: 'POST', url: 'app/rest/socialmedia/facebook/post'}
	});
});