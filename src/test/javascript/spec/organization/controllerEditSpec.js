'use strict';

describe('Controllers Tests ', function () {

    beforeEach(module('respondecoApp'));

    describe('OrganizationControllerEdit', function () {
        var $scope;
        var location;
        var OrganizationService;
        var OrgJoinRequestService;
        var AccountService;
        var UserService;
        var TextMessageService;

        beforeEach(inject(function ($rootScope, $controller, $location, $routeParams, Organization, User, Account, OrgJoinRequest, TextMessage) {
            $scope = $rootScope.$new();
            location = $location;
            OrganizationService = Organization;
            OrgJoinRequestService = OrgJoinRequest;
            AccountService = Account;
            UserService = User;
            TextMessageService = TextMessage;

            spyOn(AccountService, 'get');
            $routeParams.id = 'new';

            $controller('OrganizationControllerEdit', {$scope: $scope, $location: $location, $routeParams: $routeParams, resolvedOrganization: [1],
                Organization: Organization, Account: Account, User: User, OrgJoinRequest: OrgJoinRequest, TextMessage: TextMessage});

            // success callback
            AccountService.get.calls.mostRecent().args[1]({
                login: 'admin'
            });
        }));

        it('should be new', function() {
            expect(AccountService.get).toHaveBeenCalled();
        });

        it('should return that it is new', function() {
            expect($scope.isNew()).toEqual(true);
        });

        it('should call Organization.save if new', function() {
            spyOn(OrganizationService, 'save');
            $scope.setRootScopeOrganization = function(value) {};
            $scope.organization = {
                npo: false,
                name: 'test',
                description: 'desc',
                email: 'email@email.at'
            };
            $scope.create();
            expect(OrganizationService.save).toHaveBeenCalledWith({
                npo: false,
                name: 'test',
                description: 'desc',
                email: 'email@email.at',
                logo: undefined
            }, jasmine.any(Function), jasmine.any(Function));

            // success callback
            OrganizationService.save.calls.mostRecent().args[1]();

            // error callback
            OrganizationService.save.calls.mostRecent().args[2]({
                data: {
                    message: "fail"
                }
            });
        });

        it('should call Organization.delete', function() {
            spyOn(OrganizationService, 'delete');
            $scope.setRootScopeOrganization = function(value) {};
            $scope.delete(1);
            $scope.delete(1);
            expect(OrganizationService.delete).toHaveBeenCalledWith({
                id: 1
            }, jasmine.any(Function));

            // success callback
            OrganizationService.delete.calls.mostRecent().args[1]();
        });

        it('should call OrgJoinRequest.save', function() {
            spyOn(OrgJoinRequestService, 'save');
            spyOn(TextMessageService, 'save');
            $scope.selectedUser = {
                login: 'admin'
            };
            $scope.sendInvite();
            expect(OrgJoinRequestService.save).toHaveBeenCalled();

            // success callback
            OrgJoinRequestService.save.calls.mostRecent().args[1]({
                user: {
                    id: 1
                }
            });

            expect(TextMessageService.save).toHaveBeenCalled();

            // error callback
            OrgJoinRequestService.save.calls.mostRecent().args[2]({
                status: 400
            });

            expect($scope.alerts.length).toEqual(1);
        });

        it('should call update', function() {
            spyOn(OrganizationService, 'get');
            spyOn(OrganizationService, 'getInvitableUsers');
            spyOn(OrganizationService, 'getOrgJoinRequests');
            spyOn(OrganizationService, 'getMembers');

            $scope.update('test');
            $scope.organization = {
                id: 1
            };
            expect(OrganizationService.get).toHaveBeenCalled();

            OrganizationService.get.calls.mostRecent().args[1]({
                id: 1
            });

            expect(OrganizationService.getInvitableUsers).toHaveBeenCalled();
            expect(OrganizationService.getOrgJoinRequests).toHaveBeenCalled();
            expect(OrganizationService.getMembers).toHaveBeenCalled();
        });
    });
});
