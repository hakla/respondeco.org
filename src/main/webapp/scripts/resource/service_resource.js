'use strict';

respondecoApp.factory('Resource', function ($resource) {
	return $resource('app/rest/resourceOffers/:id', {id: '@id'}, {
		'query': { method: 'GET', isArray: true},
		'get' : { method: 'GET'}
	});
});