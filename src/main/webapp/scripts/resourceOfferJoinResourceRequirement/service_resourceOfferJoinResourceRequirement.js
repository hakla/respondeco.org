'use strict';

respondecoApp.factory('ResourceOfferJoinResourceRequirement', function ($resource) {
        return $resource('app/rest/resourceOfferJoinResourceRequirements/:id', {}, {
            'query': { method: 'GET', isArray: true},
            'get': { method: 'GET'}
        });
    });
