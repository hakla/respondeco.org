'use strict';

respondecoApp.factory('SocialMedia', function ($resource) {
	return $resource('app/rest/connect/', {}, {
		'connectFacebook' : { method: 'GET', url: 'app/rest/connect/facebook'},
		'connectFacebookCreate' : {method: 'POST', url: 'app/rest/connect/facebook/createconnection'},
		'connectGoogle' : {method: 'GET', url: 'app/rest/connect/google'},
		'connectTwitter' : {method: 'GET', url: 'app/rest/connect/twitter'},
		'createTwitterConnection' : {method: 'POST', url: 'app/rest/connect/twitter/createconnection'}
	});
});