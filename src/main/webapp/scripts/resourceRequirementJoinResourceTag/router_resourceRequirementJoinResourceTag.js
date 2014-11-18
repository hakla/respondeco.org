'use strict';

respondecoApp
    .config(function ($routeProvider, $httpProvider, $translateProvider, USER_ROLES) {
            $routeProvider
                .when('/resourceRequirementJoinResourceTag', {
                    templateUrl: 'views/resourceRequirementJoinResourceTags.html',
                    controller: 'ResourceRequirementJoinResourceTagController',
                    resolve:{
                        resolvedResourceRequirementJoinResourceTag: ['ResourceRequirementJoinResourceTag', function (ResourceRequirementJoinResourceTag) {
                            return ResourceRequirementJoinResourceTag.query().$promise;
                        }],
                        resolvedResourceTag: ['ResourceTag', function (ResourceTag) {
                            return ResourceTag.query().$promise;
                        }],
                        resolvedResourceRequirement: ['ResourceRequirement', function (ResourceRequirement) {
                            return ResourceRequirement.query().$promise;
                        }]
                    },
                    access: {
                        authorizedRoles: [USER_ROLES.all]
                    }
                })
        });
