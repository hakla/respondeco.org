'use strict';

respondecoApp.factory('Project', function($resource, $http) {
    return $resource('app/rest/projects/:id', {}, {
        'update': {method: 'PUT', url: 'app/rest/projects/:id'},
        'getProjectsByOrgId': {method: 'GET', isArray:true, url: 'app/rest/organizations/:organizationId/projects'},
        'getProjectRequirements' : {method: 'GET', isArray:true, url: 'app/rest/projects/:id/resourceRequirements'},
        'getResourceMatchesByProjectId' : {method: 'GET', isArray:true, url: 'app/rest/projects/:id/resourcematches'},
        'getAggregatedRating' : {method: 'GET', url: 'app/rest/projects/:pid/ratings'},
        'rateProject' : {method: 'POST', url: 'app/rest/projects/:pid/ratings'},
        'checkIfRatingPossible' : {method: 'GET', isArray: true, url: 'app/rest/projects/:pid/ratings'}
    });
}).factory('ResourceRequirement', function($resource) {
    return $resource('app/rest/resourceRequirements', {})
});
