'use strict';

respondecoApp
    .config(function ($routeProvider, $httpProvider, $translateProvider, USER_ROLES) {
            $routeProvider
                .when('/orgjoinrequest', {
                    templateUrl: 'views/orgjoinrequests.html',
                    controller: 'OrgJoinRequestController',
                    resolve:{
                        resolvedOrgJoinRequest: ['OrgJoinRequest', function (OrgJoinRequest) {
                            return OrgJoinRequest.query();
                        }]
                    },
                    access: {
                        authorizedRoles: [USER_ROLES.all]
                    }
                })
        });
