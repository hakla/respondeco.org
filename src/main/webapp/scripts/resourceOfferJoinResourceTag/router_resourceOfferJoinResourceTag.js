'use strict';

respondecoApp
    .config(function ($routeProvider, $httpProvider, $translateProvider, USER_ROLES) {
            $routeProvider
                .when('/resourceOfferJoinResourceTag', {
                    templateUrl: 'views/resourceOfferJoinResourceTags.html',
                    controller: 'ResourceOfferJoinResourceTagController',
                    resolve:{
                        resolvedResourceOfferJoinResourceTag: ['ResourceOfferJoinResourceTag', function (ResourceOfferJoinResourceTag) {
                            return ResourceOfferJoinResourceTag.query().$promise;
                        }],
                        resolvedResourceOffer: ['ResourceOffer', function (ResourceOffer) {
                            return ResourceOffer.query().$promise;
                        }],
                        resolvedResourceTag: ['ResourceTag', function (ResourceTag) {
                            return ResourceTag.query().$promise;
                        }]
                    },
                    access: {
                        authorizedRoles: [USER_ROLES.all]
                    }
                })
        });
