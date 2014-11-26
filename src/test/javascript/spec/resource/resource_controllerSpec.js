'use strict';

describe('Resource Controller Tests ', function () {
    beforeEach(module('respondecoApp'));

    describe('ResourceController', function () {
        var scope, mockResourceService, location, createController;
        var deferred;
        var route;

        beforeEach(inject(function ($rootScope, $controller, $location) {
            scope = $rootScope.$new();
            mockResourceService = jasmine.createSpyObj('Resource', ['save', 'query', 'delete']);
            location = $location;

            createController = $controller('ResourceController', {$scope: scope, $location: location, Resource: mockResourceService});
        }));

        it('should create a new resource',function() {

            scope.resource.id = "1";
            scope.resource.name = "Resource";
            scope.resource.amount = 1;
            scope.resource.dateStart = "11.11.2014";
            scope.resource.dateEnd = "12.12.2014";
            scope.resource.isCommercial = true;
            scope.resource.isRecurrent = true;

            scope.create();

            expect(mockResourceService.save).toHaveBeenCalled();
        });

        it('should redirect to given id', function() {
            scope.redirectToResource('1');

            expect(location.path()).toBe('/resource/1')
        });

        it('should clear the resource', function() {
            scope.resource.id = "1";
            scope.resource.name = "Resource";
            scope.resource.amount = 1;
            scope.resource.dateStart = "11.11.2014";
            scope.resource.dateEnd = "12.12.2014";
            scope.resource.isCommercial = true;
            scope.resource.isRecurrent = true;

            scope.clear();

            expect(scope.resource).toEqual({'id': null, 'name': null, 'description': null, 'tags': null, 'amount': null,
                'dateStart': null, 'dateEnd': null, 'isCommercial': null, 'isRecurrent': null});
        });

        it('should delete the resource with given id', function() {
            scope.delete(1);
            expect(mockResourceService.delete).toHaveBeenCalled();
        });

        it('should search for resources', function() {
            scope.search("filter");
            expect(mockResourceService.query).toHaveBeenCalledWith();
        });



    });
});
