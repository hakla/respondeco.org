'use strict';

respondecoApp
    .config(function ($routeProvider, $httpProvider, $translateProvider, USER_ROLES) {
            $routeProvider
                .when('/projectlogo', {
                    templateUrl: 'views/projectlogos.html',
                    controller: 'ProjectLogoController',
                    resolve:{
                        resolvedProjectLogo: ['ProjectLogo', function (ProjectLogo) {
                            return ProjectLogo.query();
                        }],
                        resolvedProject: ['Project', function (Project) {
                            return Project.query();
                        }]
                    },
                    access: {
                        authorizedRoles: [USER_ROLES.all]
                    }
                })
        });
