'use strict';

respondecoApp
    .config(function ($routeProvider, $httpProvider, $translateProvider, USER_ROLES) {
            $routeProvider
                .when('/resourceRequirement', {
                    templateUrl: 'views/resourceRequirements.html',
                    controller: 'ResourceRequirementController',
                    resolve:{
                        resolvedResourceRequirement: ['ResourceRequirement', function (ResourceRequirement) {
                            return ResourceRequirement.query().$promise;
                        }],
                        resolvedResourceTag: ['ResourceTag', function (ResourceTag) {
                            return ResourceTag.query().$promise;
                        }],
                        resolvedProject: ['Project', function (Project) {
                            return Project.query().$promise;
                        }],
                        resolvedResourceOfferJoinResourceRequirement: ['ResourceOfferJoinResourceRequirement', function (ResourceOfferJoinResourceRequirement) {
                            return ResourceOfferJoinResourceRequirement.query().$promise;
                        }]
                    },
                    access: {
                        authorizedRoles: [USER_ROLES.all]
                    }
                })
        });
