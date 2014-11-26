'use strict';

respondecoApp.factory('Image', function ($resource) {
        return $resource('app/rest/images/:id', {}, {
            'query': { method: 'GET', isArray: true},
            'get': { method: 'GET'}
        });
    });
