'use strict';

respondecoApp
    .config(function ($routeProvider, $httpProvider, $translateProvider, USER_ROLES) {
        $routeProvider
            .when('/social-networks', {
                templateUrl: '/views/social-networks.html',
                controller: 'SocialMediaController',
                access: {
                    authorizedRoles: [USER_ROLES.all]
                }
            });
    });
