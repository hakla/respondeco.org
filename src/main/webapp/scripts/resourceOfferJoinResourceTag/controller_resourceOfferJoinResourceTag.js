'use strict';

respondecoApp.controller('ResourceOfferJoinResourceTagController', function ($scope, resolvedResourceOfferJoinResourceTag, ResourceOfferJoinResourceTag, resolvedResourceOffer, resolvedResourceTag) {

        $scope.resourceOfferJoinResourceTags = resolvedResourceOfferJoinResourceTag;
        $scope.resourceOffers = resolvedResourceOffer;
        $scope.resourceTags = resolvedResourceTag;

        $scope.create = function () {
            ResourceOfferJoinResourceTag.save($scope.resourceOfferJoinResourceTag,
                function () {
                    $scope.resourceOfferJoinResourceTags = ResourceOfferJoinResourceTag.query();
                    $('#saveResourceOfferJoinResourceTagModal').modal('hide');
                    $scope.clear();
                });
        };

        $scope.update = function (id) {
            $scope.resourceOfferJoinResourceTag = ResourceOfferJoinResourceTag.get({id: id});
            $('#saveResourceOfferJoinResourceTagModal').modal('show');
        };

        $scope.delete = function (id) {
            ResourceOfferJoinResourceTag.delete({id: id},
                function () {
                    $scope.resourceOfferJoinResourceTags = ResourceOfferJoinResourceTag.query();
                });
        };

        $scope.clear = function () {
            $scope.resourceOfferJoinResourceTag = {resourceOfferId: null, resourceTagId: null, id: null};
        };
    });
