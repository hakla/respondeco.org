'use strict';

respondecoApp
    .config(function ($routeProvider, $httpProvider, $translateProvider, USER_ROLES) {
            $routeProvider
                .when('/resource', {
                    templateUrl: 'views/resources.html',
                    controller: 'ResourceController',
                    access: {
                        authorizedRoles: [USER_ROLES.all]
                    }
                })
    });
