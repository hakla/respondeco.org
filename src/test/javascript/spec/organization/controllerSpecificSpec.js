'use strict';

describe('Controllers Tests ', function () {

    beforeEach(module('respondecoApp'));

    describe('OrganizationController', function () {
        var $scope;
        var location;
        var OrganizationService;
        var AccountService;
        var UserService;
        var existingOrganization;
        var routeParams;
        var SocialMediaService;

        beforeEach(inject(function ($rootScope, $controller, $location, $routeParams, Organization, User, Account, SocialMedia) {
            $scope = $rootScope.$new();
            location = $location;
            OrganizationService = Organization;
            AccountService = Account;
            UserService = User;
            routeParams = $routeParams;
            SocialMediaService = SocialMedia;

            existingOrganization = {id: 1};

            spyOn(AccountService, 'get');
            $routeParams.id = 1;
            $controller('OrganizationController', {$scope: $scope, $location: $location, $routeParams: $routeParams, resolvedOrganization: [1],
                Organization: Organization, Account: Account, User: User, SocialMedia: SocialMediaService});
        }));

        it('should call Organization.get', function() {
            spyOn(OrganizationService, 'get').and.returnValue({id:1});
            spyOn(OrganizationService, 'getMembers');

            $scope.update("test");
            expect(OrganizationService.get).toHaveBeenCalledWith({ id: 'test' }, jasmine.any(Function));
            OrganizationService.get.calls.mostRecent().args[1]();
            expect(OrganizationService.getMembers).toHaveBeenCalledWith({
                id: 1}, jasmine.any(Function));

            OrganizationService.getMembers.calls.mostRecent().args[1](
                'test'
            );
            expect($scope.members).toEqual('test');
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
                id: '1'
            };
            $scope.redirectToEdit();
            expect(location.path()).toEqual('/organization/edit/1');
        });

        it('should redirect to own resources', function() {
            $scope.redirectToOwnResources();
            expect(location.path()).toEqual('/ownresource');
        });

        it('should call delete on Organization', function() {
            spyOn(OrganizationService, 'delete');
            spyOn(OrganizationService, 'query');
            $scope.organization = {
                id: 1
            };
            $scope.delete(1);
        });

        it('should get organization follow state', function(){

            //predefine some value(s) for test
            $scope.organization = existingOrganization;
            routeParams.id = $scope.organization.id;

            spyOn(OrganizationService, 'followingState');

            $scope.followingState();

            expect(OrganizationService.followingState).toHaveBeenCalledWith({id: $scope.organization.id}, jasmine.any(Function));
            //simulate json result
            OrganizationService.followingState.calls.mostRecent().args[1]({state: true});
            expect($scope.following).toBe(true);
            expect($scope.showFollow()).toBe(false);
            expect($scope.showUnfollow()).toBe(true);
        });

        it('should add organization as followed by current user', function(){
            //predefine some value(s) for test
            $scope.organization = existingOrganization;
            routeParams.id = $scope.organization.id;

            spyOn(OrganizationService, "follow");

            $scope.follow();

            expect(OrganizationService.follow).toHaveBeenCalledWith({id: $scope.organization.id}, null, jasmine.any(Function));
            //simulate callback
            OrganizationService.follow.calls.mostRecent().args[2]();
            expect($scope.following).toBe(true);
            expect($scope.showFollow()).toBe(false);
            expect($scope.showUnfollow()).toBe(true);
        });

        it('should remove organization from following list of current user', function(){
            //predefine some value(s) for test
            $scope.organization = existingOrganization;
            routeParams.id = $scope.organization.id;

            spyOn(OrganizationService, "unfollow");

            $scope.unfollow();

            expect(OrganizationService.unfollow).toHaveBeenCalledWith({id: $scope.organization.id}, jasmine.any(Function));
            //simulate callback
            OrganizationService.unfollow.calls.mostRecent().args[1]();
            expect($scope.following).toBe(false);
            expect($scope.showFollow()).toBe(true);
            expect($scope.showUnfollow()).toBe(false);
        });

        it('should get all connections for the current user', function() {

            spyOn(SocialMediaService, 'getConnections');

            $scope.getConnections();

            expect(SocialMediaService.getConnections).toHaveBeenCalled();

            SocialMediaService.getConnections.calls.mostRecent().args[0]([
                {provider: 'twitter'},
                {provider: 'facebook'},
                {provider: 'xing'}
            ]);

            expect($scope.twitterConnected).toBe(true);
            expect($scope.facebookConnected).toBe(true);
            expect($scope.xingConnected).toBe(true);
        });

        it('should add a posting', function() {
            spyOn(SocialMediaService, 'createTwitterPost');
            spyOn(SocialMediaService, 'createFacebookPost');
            spyOn(SocialMediaService, 'createXingPost');
            spyOn(OrganizationService, 'addPostingForOrganization');

            $scope.postingInformation = 'posting';


            $scope.postOnTwitter = true;
            $scope.postOnXing = true;            
            $scope.postOnFacebook = true;

            $scope.addPosting();

            expect(OrganizationService.addPostingForOrganization).toHaveBeenCalledWith({ id : 1 }, 'posting', jasmine.any(Function));
            expect(SocialMediaService.createTwitterPost).toHaveBeenCalledWith({urlPath: '', post: 'posting'});
            expect(SocialMediaService.createFacebookPost).toHaveBeenCalledWith({urlPath: '', post: 'posting'});
            expect(SocialMediaService.createXingPost).toHaveBeenCalledWith({urlPath: '', post: 'posting'});
        });

        it('should return if posting is too short', function() {
            $scope.postingInformation = 'post';
            $scope.addPosting();
        });

        it('should delete Posting', function() {
            spyOn(OrganizationService, 'deletePosting');

            $scope.postings = [{
                id: 1
            }, {

                id: 2
            }];

            $scope.deletePosting(1);

            expect(OrganizationService.deletePosting).toHaveBeenCalled();
            OrganizationService.deletePosting.calls.mostRecent().args[1]();
        });

        it('should update user', function() {
            $scope.updateUser('item', 'modal', 'label');
            expect($scope.selectedUser).toEqual('item');
        });

        it('should show more postings', function() {
            spyOn(OrganizationService, 'getPostingsByOrgId');

            $scope.showMorePostings();

            expect(OrganizationService.getPostingByOrgId);
            OrganizationService.getPostingsByOrgId.calls.mostRecent().args[1]({
                totalElements: 5,
                postings: [
                {post: 1}, {post:2}]
            });
        });

        it('should get donated resources', function() {
            spyOn(OrganizationService, 'getDonatedResources');

            $scope.getDonatedResources();

            expect(OrganizationService.getDonatedResources).toHaveBeenCalled();

            OrganizationService.getDonatedResources.calls.mostRecent().args[2]({
                resourceMatches: [
                    {
                        project: {
                            id: 1
                        }
                    },
                    {
                        project: {
                            id: 2
                        }
                    }
                ]
            });

        });

    });
});
