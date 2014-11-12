'use strict';

respondecoApp.factory('OrgJoinRequest', function ($resource) {
        return $resource('app/rest/orgjoinrequests/:id', {}, {
            'query': { method: 'GET', isArray: true},
            'get': { method: 'GET'}
        });
    });
