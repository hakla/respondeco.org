'use strict';

respondecoApp.factory('ResourceOfferJoinResourceTag', function ($resource) {
        return $resource('app/rest/resourceOfferJoinResourceTags/:id', {}, {
            'query': { method: 'GET', isArray: true},
            'get': { method: 'GET'}
        });
    });
