'use strict';

respondecoApp.factory('SocialMedia', function ($resource) {
	return $resource('app/rest/connect/', {}, {
		'getConnections' : {method: 'GET', isArray:true, url: 'app/rest/connections'},
		'connectFacebook' : { method: 'GET', url: 'app/rest/connect/facebook'},
		'createFacebookConnection' : {method: 'POST', url: 'app/rest/connect/facebook/createconnection'},
		'createFacebookPost' : {method: 'POST', url: 'app/rest/socialmedia/facebook/post'},
		'connectGoogle' : {method: 'GET', url: 'app/rest/connect/google'},
		'connectTwitter' : {method: 'GET', url: 'app/rest/connect/twitter'},
		'createTwitterConnection' : {method: 'POST', url: 'app/rest/connect/twitter/createconnection'},
		'createTwitterPost' : {method: 'POST', url: 'app/rest/socialmedia/twitter/post'},
		'connectXing' : {method: 'GET', url: 'app/rest/connect/xing'},
		'createXingConnection' : {method: 'POST', url: 'app/rest/connect/xing/createconnection'},
		'createXingPost' : {method: 'POST', url: 'app/rest/socialmedia/xing/post'},
	});
});