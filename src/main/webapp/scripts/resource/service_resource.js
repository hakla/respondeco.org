'use strict';

respondecoApp.factory('Resource', function ($resource) {
	return $resource('app/rest/resourceOffers/:id', {id: '@id'}, {
		'query': { method: 'GET', isArray: true},
		'get' : { method: 'GET'},
		'update' : {method: 'PUT'},
		'getByOrgId' : {method: 'GET', isArray:true, url: 'app/rest/organizations/:id/resourceOffers'}
	});
});
