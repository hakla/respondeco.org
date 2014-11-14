'use strict';

respondecoApp.factory('ProjectIdea', function ($resource) {
        return $resource('app/rest/projectideas/:id', {}, {
            'query': { method: 'GET', isArray: true},
            'get': { method: 'GET'}
        });
    });
