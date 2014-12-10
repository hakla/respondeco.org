'use strict';

respondecoApp.factory('Project', function($resource, $http) {
    return $resource('app/rest/projects/:id', {}, {
        'update': {method: 'PUT', url: 'app/rest/projects/:id'},
        'rate': {method: 'POST', url: 'app/rest/projects/:id/projectrating'},
        'updateRating': {method: 'PUT', url: 'app/rest/projects/:id/projectrating'},
        'getProjectsByOrgId': {method: 'GET', isArray:true, url: 'app/rest/organizations/:organizationId/projects'},
        'getProjectRequirements' : {method: 'GET', isArray:true, url: 'app/rest/projects/:id/resourceRequirements'},
        'getResourceMatchesByProjectId' : {method: 'GET', isArray:true, url: 'app/rest/projects/:id/resourcematches'}
    });
}).factory('ResourceRequirement', function($resource) {
    return $resource('app/rest/resourceRequirements', {})
});
