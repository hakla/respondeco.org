'use strict';

respondecoApp.factory('TextMessage', function ($resource) {
        return $resource('app/rest/textmessages/:id', {}, {
            'query': { method: 'GET', isArray: true},
            'get': { method: 'GET'}
        });
    });
