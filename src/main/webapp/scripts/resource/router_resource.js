'use strict';

respondecoApp
    .config(function ($routeProvider, $httpProvider, $translateProvider, USER_ROLES) {
            $routeProvider
                .when('/resource', {
                    templateUrl: 'views/resources.html',
                    controller: 'ResourceController',
                    resolve: {
                        resolvedResources: ['Resource', function(Resource) {
                            return Resource.query();
                        }]
                    },
                    access: {
                        authorizedRoles: [USER_ROLES.all]
                    }
                })
    });
