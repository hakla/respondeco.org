'use strict';

respondecoApp
    .config(function ($routeProvider, $httpProvider, $translateProvider, USER_ROLES) {
            $routeProvider
                .when('/propertytag', {
                    templateUrl: 'views/propertytags.html',
                    controller: 'PropertyTagController',
                    resolve:{
                        resolvedPropertyTag: ['PropertyTag', function (PropertyTag) {
                            return PropertyTag.query();
                        }]
                    },
                    access: {
                        authorizedRoles: [USER_ROLES.all]
                    }
                })
        });
