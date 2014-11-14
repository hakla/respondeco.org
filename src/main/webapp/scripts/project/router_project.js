'use strict';

respondecoApp
    .config(function ($routeProvider, $httpProvider, $translateProvider, USER_ROLES) {
            $routeProvider
                .when('/projectidea', {
                    templateUrl: 'views/projects.html',
                    controller: 'ProjectIdeaController',
                    resolve:{
                        resolvedProjectIdea: ['ProjectIdea', function (ProjectIdea) {
                            return ProjectIdea.query();
                        }],
                        resolvedOrganization: ['Organization', function (Organization) {
                            return Organization.query();
                        }]
                    },
                    access: {
                        authorizedRoles: [USER_ROLES.all]
                    }
                })
        });
