'use strict';

describe('Resource Controller Tests ', function () {
    beforeEach(module('respondecoApp'));

    describe('ResourceController', function () {
        var $scope, ResourceService, location, createController;

        beforeEach(inject(function ($rootScope, $controller, $location, $routeParams, Resource) {
            $scope = $rootScope.$new();
            location = $location;
            ResourceService = Resource;
            $routeParams.id = 'new';

            $controller('ResourceController', {$scope: $scope, $routeParams: $routeParams, $location: location, Resource: ResourceService});
        }));

        it('should create a new resource',function() {
            $scope.resource = {
              "name": "Resource",
              "description": "Desc",
              "id": 1,
              "endDate": "2015-12-12",
              "startDate": "2015-12-12",
              "resourceTags": [
                "Computer", "Test"
              ],
              "amount": 5,
              "isRecurrent": false,
              "isCommercial": false,
              "organizationId": 1
            }

            spyOn(ResourceService, "save");

            $scope.create();

            expect(ResourceService.save).toHaveBeenCalled();
        });


        it('should show an error message', function() {
            $scope.resource = {
              "name": "Resource",
              "description": "Desc",
              "id": 1,
              "endDate": "2015-12-12",
              "startDate": "2015-12-12",
              "resourceTags": [
                "Computer", "Test"
              ],
              "amount": 5,
              "isRecurrent": false,
              "isCommercial": false,
              "organizationId": 1
            }

            spyOn(ResourceService, "save");

            $scope.create();

            expect(ResourceService.save).toHaveBeenCalled();
            expect(ResourceService.save).toHaveBeenCalledWith($scope.resource, jasmine.any(Function), jasmine.any(Function));

            //Simulate error callback
            ResourceService.save.calls.mostRecent().args[2]({
                data: {
                    error: "Error"
                }
            });

            expect($scope.formSaveError).toBe(true);
        });



        it('should redirect to given id', function() {
            $scope.redirectToResource('1');

            expect(location.path()).toBe('/resource/1');
        });

        it('should clear the resource', function() {
            $scope.resource.id = "1";
            $scope.resource.name = "Resource";
            $scope.resource.amount = 1;
            $scope.resource.dateStart = "11.11.2014";
            $scope.resource.dateEnd = "12.12.2014";
            $scope.resource.isCommercial = true;
            $scope.resource.isRecurrent = true;

            $scope.clear();

            expect($scope.resource).toEqual({'id': null, 'name': null, 'description': null, 'tags': null, 'amount': null,
                'dateStart': null, 'dateEnd': null, 'isCommercial': null, 'isRecurrent': null});
        });

        it('should delete the resource with given id', function() {
            spyOn(ResourceService,"delete");
            $scope.delete(1);
            expect(ResourceService.delete).toHaveBeenCalled();
        });

        it('should search for resources', function() {
            spyOn(ResourceService,"query");
            $scope.search("filter");
            expect(ResourceService.query).toHaveBeenCalledWith({filter:'filter'},
                jasmine.any(Function), jasmine.any(Function));
        });


    });
});
