'use strict';

respondecoApp.controller('ResourceOfferController', function ($scope, resolvedResourceOffer, ResourceOffer, resolvedResourceTag, resolvedResourceOfferJoinResourceRequirement) {

        $scope.resourceOffers = resolvedResourceOffer;
        $scope.resourceTags = resolvedResourceTag;
        $scope.resourceOfferJoinResourceRequirements = resolvedResourceOfferJoinResourceRequirement;

        $scope.create = function () {
            ResourceOffer.save($scope.resourceOffer,
                function () {
                    $scope.resourceOffers = ResourceOffer.query();
                    $('#saveResourceOfferModal').modal('hide');
                    $scope.clear();
                });
        };

        $scope.update = function (id) {
            $scope.resourceOffer = ResourceOffer.get({id: id});
            $('#saveResourceOfferModal').modal('show');
        };

        $scope.delete = function (id) {
            ResourceOffer.delete({id: id},
                function () {
                    $scope.resourceOffers = ResourceOffer.query();
                });
        };

        $scope.clear = function () {
            $scope.resourceOffer = {amount: null, description: null, organisationId: null, createBy: null, createdDate: null, lastModifiedBy: null, lastModifiedDate: null, isActive: null, id: null};
        };
    });
