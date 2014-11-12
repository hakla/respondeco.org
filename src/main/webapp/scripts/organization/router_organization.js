'use strict';

respondecoApp
    .config(function ($routeProvider, $httpProvider, $translateProvider, USER_ROLES) {
            $routeProvider
                .when('/organization', {
                    templateUrl: 'views/organizations.html',
                    controller: 'OrganizationController',
                    resolve:{
                        resolvedOrganization: ['Organization', function (Organization) {
                            return Organization.query();
                        }]
                    },
                    access: {
                        authorizedRoles: [USER_ROLES.all]
                    }
                })
        });
