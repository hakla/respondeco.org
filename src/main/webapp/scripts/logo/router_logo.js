'use strict';

respondecoApp
    .config(function ($routeProvider, $httpProvider, $translateProvider, USER_ROLES) {
            $routeProvider
                .when('/logo', {
                    templateUrl: 'views/logos.html',
                    controller: 'LogoController',
                    resolve:{
                        resolvedLogo: ['Logo', function (Logo) {
                            return Logo.query();
                        }]
                    },
                    access: {
                        authorizedRoles: [USER_ROLES.all]
                    }
                })
        });
