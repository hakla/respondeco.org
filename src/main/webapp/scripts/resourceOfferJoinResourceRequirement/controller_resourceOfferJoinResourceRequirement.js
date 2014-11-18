'use strict';

respondecoApp.controller('ResourceOfferJoinResourceRequirementController', function ($scope, resolvedResourceOfferJoinResourceRequirement, ResourceOfferJoinResourceRequirement) {

        $scope.resourceOfferJoinResourceRequirements = resolvedResourceOfferJoinResourceRequirement;

        $scope.create = function () {
            ResourceOfferJoinResourceRequirement.save($scope.resourceOfferJoinResourceRequirement,
                function () {
                    $scope.resourceOfferJoinResourceRequirements = ResourceOfferJoinResourceRequirement.query();
                    $('#saveResourceOfferJoinResourceRequirementModal').modal('hide');
                    $scope.clear();
                });
        };

        $scope.update = function (id) {
            $scope.resourceOfferJoinResourceRequirement = ResourceOfferJoinResourceRequirement.get({id: id});
            $('#saveResourceOfferJoinResourceRequirementModal').modal('show');
        };

        $scope.delete = function (id) {
            ResourceOfferJoinResourceRequirement.delete({id: id},
                function () {
                    $scope.resourceOfferJoinResourceRequirements = ResourceOfferJoinResourceRequirement.query();
                });
        };

        $scope.clear = function () {
            $scope.resourceOfferJoinResourceRequirement = {resourceOfferId: null, resourceRequirementId: null, amount: null, createdBy: null, createdDate: null, lastModifiedBy: null, lastModifiedDate: null, isActive: null, id: null};
        };
    });
