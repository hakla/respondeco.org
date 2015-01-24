'use strict';

respondecoApp.factory('SocialMedia', function ($resource) {
	return $resource('app/rest/connect/', {}, {
		'getConnections' : {method: 'GET', isArray:true, url: 'app/rest/connections', ignoreAuthModule: true },
		'connectFacebook' : { method: 'GET', url: 'app/rest/connect/facebook'},
		'createFacebookConnection' : {method: 'POST', url: 'app/rest/connect/facebook/createconnection'},
		'createFacebookPost' : {method: 'POST', url: 'app/rest/socialmedia/facebook/post'},
		'disconnectFacebook' : {method: 'DELETE', url: 'app/rest/socialmedia/facebook/disconnect'},
		'connectTwitter' : {method: 'GET', url: 'app/rest/connect/twitter'},
		'createTwitterConnection' : {method: 'POST', url: 'app/rest/connect/twitter/createconnection'},
		'createTwitterPost' : {method: 'POST', url: 'app/rest/socialmedia/twitter/post'},
		'disconnectTwitter' : {method: 'DELETE', url: 'app/rest/socialmedia/twitter/disconnect'},
		'connectXing' : {method: 'GET', url: 'app/rest/connect/xing'},
		'createXingConnection' : {method: 'POST', url: 'app/rest/connect/xing/createconnection'},
		'createXingPost' : {method: 'POST', url: 'app/rest/socialmedia/xing/post'},
		'disconnectXing' : {method: 'DELETE', url: 'app/rest/socialmedia/xing/disconnect'}
	});
});