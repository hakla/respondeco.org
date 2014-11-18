'use strict';

respondecoApp
    .config(function ($routeProvider, $httpProvider, $translateProvider, USER_ROLES) {
            $routeProvider
                .when('/resource', {
                    templateUrl: '/views/resources.html',
                    controller: 'ResourceController',
                    resolve: {
                        resolvedResources: ['Resource', function(Resource) {
                            return Resource.query();
                        }]
                    },
                    access: {
                        authorizedRoles: [USER_ROLES.all]
                    }
                }).when('/resource/:id', {
                    templateUrl: '/views/resource-new.html',
                    controller: 'ResourceController',
                    resolve: {
                        resolvedResources: ['Resource', function(Resource) {
                            return Resource.query();
                        }]
                    },
                    access: {
                        authorizedRoles: [USER_ROLES.all]
                    }
                }).when('/ownresource', {
                    templateUrl: '/views/resources-own.html',
                    controller: 'ResourceController',
                    resolve: {
                        resolvedResources: ['Resource', function(Resource) {
                            return Resource.query();
                        }]
                    },
                    access: {
                        authorizedRoles: [USER_ROLES.all]
                    }
                }).otherwise({redirectTo:'/'});
    });
