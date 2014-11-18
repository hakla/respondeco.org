'use strict';

respondecoApp.factory('ResourceTag', function ($resource) {
        return $resource('app/rest/resourceTags/:id', {}, {
            'query': { method: 'GET', isArray: true},
            'get': { method: 'GET'}
        });
    });
