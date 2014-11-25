'use strict';

respondecoApp
    .config(function ($routeProvider, $httpProvider, $translateProvider, USER_ROLES) {
            $routeProvider
                .when('/resourceOfferJoinResourceRequirement', {
                    templateUrl: 'views/resourceOfferJoinResourceRequirements.html',
                    controller: 'ResourceOfferJoinResourceRequirementController',
                    resolve:{
                        resolvedResourceOfferJoinResourceRequirement: ['ResourceOfferJoinResourceRequirement', function (ResourceOfferJoinResourceRequirement) {
                            return ResourceOfferJoinResourceRequirement.query().$promise;
                        }]
                    },
                    access: {
                        authorizedRoles: [USER_ROLES.all]
                    }
                })
        });
