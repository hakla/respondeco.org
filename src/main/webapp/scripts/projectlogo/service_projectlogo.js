'use strict';

respondecoApp.factory('ProjectLogo', function ($resource) {
        return $resource('app/rest/projectlogos/:id', {}, {
            'query': { method: 'GET', isArray: true},
            'get': { method: 'GET'}
        });
    });
