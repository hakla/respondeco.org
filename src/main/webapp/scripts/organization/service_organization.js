'use strict';

respondecoApp.factory('Organization', function ($resource) {
        return $resource('app/rest/organizations/:id', {}, {
            'query': { method: 'GET' },
            'get': { method: 'GET'},
            'update': { method: 'PUT', url: 'app/rest/organizations' },
            'getMembers': { method: 'GET', url: 'app/rest/organizations/:id/members', isArray: true },
            'getOrgJoinRequests': { method: 'GET', url: 'app/rest/organizations/:id/orgjoinrequests', isArray: true },
            'getInvitableUsers': { method: 'GET', url: 'app/rest/organizations/:id/invitableusers', isArray: true },
            'getResourceRequests' : {method: 'GET', url: 'app/rest/organizations/:id/resourcerequests', isArray:true },
            'rateOrganization' : {method: 'POST', url: 'app/rest/organizations/:id/ratings'},
            'getAggregatedRating' : {method: 'GET', url: 'app/rest/organizations/:id/ratings'},
            'getResourceOffers': { method: 'GET', url: 'app/rest/organizations/:id/resourceoffers', isArray: true },
            'follow' : {method: 'POST', url: 'app/rest/organizations/:id/follow'},
            'unfollow' : {method: 'DELETE', url: 'app/rest/organizations/:id/unfollow'},
            'followingState' : {method: 'GET', url: 'app/rest/organizations/:id/followingstate', ignoreAuthModule: true},
            'getPostingsByOrgId': {method: 'GET', url: 'app/rest/organizations/:id/postings'},
            'addPostingForOrganization': {method: 'POST', url: '/app/rest/organizations/:id/postings'},
            'deletePosting' : {method: 'DELETE', url: 'app/rest/organizations/:id/postings/:pid'},
            'verify': { method: 'POST', url: 'app/rest/organizations/:id/verify' }
        });
    });
