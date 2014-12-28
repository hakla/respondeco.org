'use strict';

respondecoApp
    .config(function ($routeProvider, $httpProvider, $translateProvider, USER_ROLES) {
            $routeProvider
                .when('/projects', {
                    templateUrl: 'views/projects_search.html',
                    controller: 'ProjectSearchController',
                    access: {
                        authorizedRoles: [USER_ROLES.all]
                    }
                })
        });

respondecoApp
    .config(function ($routeProvider, $httpProvider, $translateProvider, USER_ROLES) {
        $routeProvider
            .when('/projects/edit/:id', {
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
            .when('/projects/:id', {
                templateUrl: 'views/projects_details.html',
                controller: 'ProjectController',
                access: {
                    authorizedRoles: [USER_ROLES.all]
                }
            })
    });

respondecoApp
    .config(function ($routeProvider, $httpProvider, $translateProvider, USER_ROLES) {
        $routeProvider
            .when('/projectsnearyou', {
                templateUrl: 'views/projectsnearyou.html',
                controller: 'ProjectLocationController',
                access: {
                    authorizedRoles: [USER_ROLES.all]
                }
            })
    });

