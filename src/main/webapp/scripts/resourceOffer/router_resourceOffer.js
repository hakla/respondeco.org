'use strict';

respondecoApp
    .config(function ($routeProvider, $httpProvider, $translateProvider, USER_ROLES) {
            $routeProvider
                .when('/resourceOffer', {
                    templateUrl: 'views/resourceOffers.html',
                    controller: 'ResourceOfferController',
                    resolve:{
                        resolvedResourceOffer: ['ResourceOffer', function (ResourceOffer) {
                            return ResourceOffer.query().$promise;
                        }],
                        resolvedResourceTag: ['ResourceTag', function (ResourceTag) {
                            return ResourceTag.query().$promise;
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
