'use strict';

respondecoApp
    .config(function ($routeProvider, $httpProvider, $translateProvider, USER_ROLES) {
            $routeProvider
                .when('/project', {
                    templateUrl: 'views/projects.html',
                    controller: 'ProjectController',
                    access: {
                        authorizedRoles: [USER_ROLES.all]
                    }
                })
        });

respondecoApp
    .config(function ($routeProvider, $httpProvider, $translateProvider, USER_ROLES) {
        $routeProvider
            .when('/project/create', {
                templateUrl: 'views/projects_create.html',
                controller: 'ProjectController',
                access: {
                    authorizedRoles: [USER_ROLES.all]
                }
            })
    });

respondecoApp
    .config(function ($routeProvider, $httpProvider, $translateProvider, USER_ROLES) {
        $routeProvider
            .when('/project/viewDetails', {
                templateUrl: 'views/projects_details.html',
                controller: 'ProjectController',
                access: {
                    authorizedRoles: [USER_ROLES.all]
                }
            })
    });
