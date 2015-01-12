'use strict';

respondecoApp.factory('Resource', function ($resource) {
	return $resource('app/rest/resourceoffers/:id', {id: '@id'}, {
		'get' : { method: 'GET'},
		'update' : {method: 'PUT'},
		'getByOrgId' : {method: 'GET', url: 'app/rest/organizations/:id/resourceoffers', isArray:true},
		'claimResource' : {method: 'POST', url: 'app/rest/resourcerequests'},
		'updateResource' : {method: 'PUT', url: 'app/rest/resourcerequests/:id'},
		'query' : {method: 'GET', url: 'app/rest/resourceoffers'}
	});
});
