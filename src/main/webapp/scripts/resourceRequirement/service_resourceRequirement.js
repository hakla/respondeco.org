'use strict';

respondecoApp.factory('ResourceRequirement', function ($resource) {
        return $resource('app/rest/resourceRequirements/:id', {}, {
            'query': { method: 'GET', isArray: true},
            'get': { method: 'GET'}
        });
    });
