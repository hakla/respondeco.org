'use strict';

respondecoApp.factory('Resource', function ($resource) {
	return $resource('app/rest/resourceoffers/:id', {id: '@id'}, {
		'get' : { method: 'GET'},
		'update' : {method: 'PUT'},
		'getByOrgId' : {method: 'GET', isArray:true, url: 'app/rest/organizations/:id/resourceoffers'},
		'claimResource' : {method: 'POST', url: 'app/rest/resourcerequests'},
		'updateRequest' : {method: 'PUT', url: 'app/rest/resourcerequests/:id'},
		'query' : {method: 'GET', url: 'app/rest/resourceoffers'}
	});
});