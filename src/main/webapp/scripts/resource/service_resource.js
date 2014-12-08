'use strict';

respondecoApp.factory('Resource', function ($resource) {
	return $resource('app/rest/resourceOffers/:id', {id: '@id'}, {
		'get' : { method: 'GET'},
		'update' : {method: 'PUT'},
		'getByOrgId' : {method: 'GET', isArray:true, url: 'app/rest/organizations/:id/resourceOffers'},
		'claimResource' : {method: 'POST', url: 'app/rest/resourcerequests'}
	});
});
