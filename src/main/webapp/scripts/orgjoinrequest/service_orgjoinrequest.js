'use strict';

respondecoApp.factory('OrgJoinRequest', function ($resource) {
        return $resource('app/rest/orgjoinrequests/:id', {}, {
            'query': { method: 'GET', isArray: true},
            'get': { method: 'GET', isArray: true},
            'accept': { method: 'DELETE', url: '/app/rest/orgjoinrequests/accept/:id' },
            'decline': { method: 'DELETE', url: '/app/rest/orgjoinrequests/decline/:id' },
            'getCurrent': { method: 'GET', url: '/app/rest/orgjoinrequest/current', isArray: true },
            'delete': { method: 'DELETE', url: '/app/rest/orgjoinrequests/:id' }
        });
    });
