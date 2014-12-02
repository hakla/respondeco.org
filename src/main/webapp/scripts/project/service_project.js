'use strict';

respondecoApp.factory('Project', function ($resource, $http) {
    return $resource('app/rest/projects/:id', {}, {});
}).factory('ResourceRequirement', function($resource) {
	return $resource('app/rest/resourceRequirements', {})
});
