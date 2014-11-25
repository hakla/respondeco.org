'use strict';

describe('Controllers Tests ', function () {

    beforeEach(module('respondecoApp'));

    describe('OrganizationsController', function () {
        var $scope;
        var location;

        beforeEach(inject(function ($rootScope, $controller, $location) {
            $scope = $rootScope.$new();
            location = $location;
            var mockOrganizationService = jasmine.createSpyObj('Organization', ['save', 'query']);
            $controller('OrganizationsController', {$scope: $scope, $location: $location, Organization: mockOrganizationService});
        }));

        it('should redirect to detail view of an organization', function() {
            $scope.redirectToOrganization('Caritas');

            expect(location.path()).toEqual('/organization/Caritas');
        });
    });
});
