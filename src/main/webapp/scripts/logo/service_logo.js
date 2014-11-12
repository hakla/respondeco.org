'use strict';

respondecoApp.factory('Logo', function ($resource) {
        return $resource('app/rest/logos/:id', {}, {
            'query': { method: 'GET', isArray: true},
            'get': { method: 'GET'}
        });
    });
