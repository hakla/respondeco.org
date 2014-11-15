'use strict';

respondecoApp.factory('Resource', function ($resource) {
	return $resource('app/rest/resources/:id', {}, {
		'query': { method: 'GET', isArray: true},
		'get' : { method: 'GET'}
	});
});