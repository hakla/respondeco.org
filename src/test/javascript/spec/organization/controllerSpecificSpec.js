'use strict';

describe('Controllers Tests ', function () {

    beforeEach(module('respondecoApp'));

    describe('OrganizationController', function () {
        var $scope;
        var location;
        var OrganizationService;
        var AccountService;
        var UserService;

        beforeEach(inject(function ($rootScope, $controller, $location, $routeParams, Organization, User, Account) {
            $scope = $rootScope.$new();
            location = $location;
            OrganizationService = Organization;
            AccountService = Account;
            UserService = User;
            spyOn(AccountService, 'get');
            $routeParams.id = 1;
            $controller('OrganizationController', {$scope: $scope, $location: $location, $routeParams: $routeParams, resolvedOrganization: [1], 
                Organization: Organization, Account: Account, User: User});
        }));

        it('should set organizations to resolvedOrganization', function() {
            expect($scope.organizations[0]).toEqual(1);
        });

        it('should query the current users account', function() {
            expect(AccountService.get).toHaveBeenCalled();
            AccountService.get.calls.mostRecent().args[1]();
        });

        it('should call Organization.get', function() {
            spyOn(OrganizationService, 'get').and.callThrough();
            $scope.update("test");
            expect(OrganizationService.get).toHaveBeenCalledWith({ id: 'test' }, jasmine.any(Function));
            OrganizationService.get.calls.mostRecent().args[1]();
        });

        it('should return false for isOwner', function() {
            expect($scope.isOwner()).toEqual(false);
        });

        it('should redirect to overview', function() {
            $scope.redirectToOverview();
            expect(location.path()).toEqual('/organization');
        });

        it('should redirect to edit', function() {
            $scope.organization = {
                name: 'test'
            };
            $scope.redirectToEdit();
            expect(location.path()).toEqual('/organization/edit/test');
        });

        it('should redirect to own resources', function() {
            $scope.redirectToOwnResources();
            expect(location.path()).toEqual('/ownresource');
        });

        it('should call delete on Organization', function() {
            spyOn(OrganizationService, 'delete');
            $scope.organization = {
                id: 1
            };
            $scope.delete(1);
        });
    });
});
