'use strict';

respondecoApp
    .config(function ($routeProvider, $httpProvider, $translateProvider, USER_ROLES) {
        $routeProvider
            .when('/newsfeed', {
                templateUrl: '/views/newsfeed.html',
                controller: 'NewsfeedController',
                access: {
                    authorizedRoles: [USER_ROLES.all]
                }
            });
    });
