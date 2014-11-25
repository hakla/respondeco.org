'use strict';

respondecoApp.factory('Organization', function ($resource) {
        return $resource('app/rest/organizations/:id', {}, {
            'query': { method: 'GET', isArray: true},
            'get': { method: 'GET'},
            'update': { method: 'POST', url: 'app/rest/organizations/updateOrganization' },
            'getById': { method: 'GET', url: 'app/rest/organization/:id' },
            'getMembers': { method: 'GET', url: 'app/rest/organizations/:id/members', isArray: true }
        });
    }).factory('User', function($resource) {
        return $resource('app/rest/users/:loginName', {}, {
            'get': { method: 'GET'},
            'getById': { method: 'GET', url: 'app/rest/users/byId/:id' },
            'getByOrgId': { method: 'GET', url: 'app/rest/users/getByOrgId/:id', isArray: true },
            'getInvitableUsers': { method: 'GET', url: 'app/rest/users/getInvitableUsersByOrgId/:id', isArray: true }
        });
    });
