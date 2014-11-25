'use strict';

respondecoApp.factory('ResourceOffer', function ($resource) {
        return $resource('app/rest/resourceOffers/:id', {}, {
            'query': { method: 'GET', isArray: true},
            'get': { method: 'GET'}
        });
    });
