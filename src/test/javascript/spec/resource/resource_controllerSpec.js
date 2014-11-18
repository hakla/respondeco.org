'use strict';

describe('Resource Controller Tests ', function () {
    beforeEach(module('respondecoApp'));

    describe('ResourceController', function () {
        var $scope, ResourceService;

        beforeEach(inject(function ($rootScope, $controller, Resource) {
            $scope = $rootScope.$new();
            ResourceService = Resource;
            $controller('ResourceController', {$scope: $scope, Resource: ResourceService});
        }));

        it('should create a new resource',function() {
            $scope.resource.name = "Resource";
            $scope.resource.amount = 1;
            $scope.resource.dateStart = "11.11.2014";
            $scope.resource.dateEnd = "12.12.2014";
            $scope.resource.isCommercial = true;
            $scope.resource.isRecurrent = true;

            spyOn(ResourceService, "save");
            $scope.create();

            expect(ResourceService.save).toHaveBeenCalled();
        });

    });
});
