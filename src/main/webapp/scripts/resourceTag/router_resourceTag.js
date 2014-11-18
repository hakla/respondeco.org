'use strict';

respondecoApp
    .config(function ($routeProvider, $httpProvider, $translateProvider, USER_ROLES) {
            $routeProvider
                .when('/resourceTag', {
                    templateUrl: 'views/resourceTags.html',
                    controller: 'ResourceTagController',
                    resolve:{
                        resolvedResourceTag: ['ResourceTag', function (ResourceTag) {
                            return ResourceTag.query().$promise;
                        }]
                    },
                    access: {
                        authorizedRoles: [USER_ROLES.all]
                    }
                })
        });
