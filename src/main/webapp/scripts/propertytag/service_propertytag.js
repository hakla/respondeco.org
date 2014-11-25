'use strict';

respondecoApp.factory('PropertyTag', function ($resource) {
        return $resource('app/rest/propertytags/:id', {}, {
            'query': { method: 'GET', isArray: true},
            'get': { method: 'GET'}
        });
    });
