/**
 * Created by clemens on 01/12/14.
 */
'use strict';

describe('Project Controller Tests ', function() {
    beforeEach(module('respondecoApp'));

    describe('ProjectController', function() {
        var scope, location, routeParams, ProjectNamesService, PropertyTagsNamesService, ProjectService,
            ResourceRequirementService, AccountService, sce, OrganizationService, translate, SocialMediaService,
            rootScope, AuthenticationSharedService;
        var fakeDeferred;
        var emptyProject;
        var existingProject;

        beforeEach(inject(function($rootScope, $controller, $location, $q, $sce, $routeParams, ProjectNames,
                                   PropertyTagNames, Project, ResourceRequirement, Account, Organization, $translate, SocialMedia,
                                   AuthenticationSharedService) {
            scope = $rootScope.$new();
            location = $location;
            translate = $translate;
            ProjectNamesService = ProjectNames;
            PropertyTagsNamesService = PropertyTagNames;
            ProjectService = Project;
            ResourceRequirementService = ResourceRequirement;
            OrganizationService = Organization;
            routeParams = $routeParams;
            AccountService = Account;
            OrganizationService = Organization;
            SocialMediaService = SocialMedia;
            sce = $sce;
            rootScope = $rootScope;

            fakeDeferred = {
                $promise: {
                    then: function(object) {}
                }
            };
            emptyProject = {
                id: null,
                name: null,
                purpose: null,
                concrete: false,
                startDate: null,
                endDate: null,
                logo: null,
                propertyTags: [],
                resourceRequirements: []
            };

            existingProject = {
                id: 1
            };

            $controller('ProjectController', {
                $scope: scope,
                Project: ProjectService,
                Organization: OrganizationService,
                ResourceRequirement: ResourceRequirementService,
                PropertyTagNames: PropertyTagsNamesService,
                $location: location,
                $routeParams: routeParams,
                $sce: sce,
                $translate: translate,
                ProjectNames: ProjectNamesService,
                Account: AccountService,
                SocialMedia: SocialMediaService,
                $rootScope: rootScope
            });

            scope.showOrgRatingModal = function() {}
            scope.hideOrgRatingModal = function() {}
            scope.setProjectRatingError = function(error) {}
            scope.setOrgRatingError = function(error) {}
        }));

        it('should set the logo after upload', function() {
            scope.onUploadComplete(null, {
                id: 1
            });

            expect(scope.project.logo.id).toEqual(1);
        });


        it('should call create', function() {
            spyOn(ProjectService, 'update');
            scope.project = emptyProject;
            scope.create();

            expect(ProjectService.update).toHaveBeenCalled();
            ProjectService.update.calls.mostRecent().args[1]();
        });

        it('should toggle calendars', function() {
            scope.openStart({
                stopPropagation: function() {}
            });
        });

        it('should call delete', function() {
            spyOn(ProjectService, 'delete');

            routeParams.id = 1;
            scope.delete();
            expect(ProjectService.delete).toHaveBeenCalledWith({
                id: 1
            }, jasmine.any(Function));
            ProjectService.delete.calls.mostRecent().args[1]();
            expect(location.path()).toBe('/projects');
        });

        it('should redirect', function() {
            scope.createProject();
            expect(location.path()).toEqual("/project/create");
        });

        it('should map resourceRequirements', function() {
            scope.project = emptyProject;
            scope.selectedResourceTags = [{
                name: "1"
            }, {
                name: "2"
            }];
            scope.createRequirement();

            expect(scope.resource.resourceTags).toEqual([{
                name: "1"
            }, {
                name: "2"
            }]);
        });

        it('should clear the resource requirement', function() {

            scope.resource = {name: 'test', amount: 2, isEssential: true};
            scope.clearRequirement();

            expect(scope.resource).toEqual({resourceTags:[], isEssential: false, originalAmount: null});
        });

        it('should remove a requirement', function() {
            scope.project.resourceRequirements = [{name:'res1'}, {name:'res2'}];

            scope.removeRequirement(1);

            expect(scope.project.resourceRequirements).toEqual([{name:'res1'}]);
        });

        it('should edit a requirement', function() {
            scope.showResourceModal = function () {
            }; //skip opening the resource modal

            scope.project.resourceRequirements = [{name: 'res1'}, {name: 'res2'}];

            scope.editRequirement(0);

            expect(scope.resource).toEqual({name: 'res1'});
        });

        it('should set rating success if a rating was sent successfully', function () {
            scope.ratedMatch = 5;
            scope.shownRating = 3;
            scope.ratingComment = "meh";
            routeParams.id = 3;

            spyOn(ProjectService, "rateProject");

            scope.rateProject();

            expect(ProjectService.rateProject).toHaveBeenCalledWith(
                {pid: 3}, {matchid: 5, rating: 3, comment: "meh"},
                jasmine.any(Function),
                jasmine.any(Function));

            //Simulate call to success callback
            ProjectService.rateProject.calls.mostRecent().args[2]();
            expect(scope.ratingSuccess).toBe("SUCCESS");
            expect(scope.projectRatingError).toBeNull();
            expect(scope.isRating).toBe(false);
            expect(scope.ratingComment).toBeNull();
        });

        it('should set rating error if the rating was not successful', function () {
            scope.ratedMatch = 5;
            scope.shownRating = 3;
            scope.ratingComment = "meh";
            routeParams.id = 3;
            var error = {status: 500}

            spyOn(ProjectService, "rateProject");

            scope.rateProject();

            expect(ProjectService.rateProject).toHaveBeenCalledWith(
                {pid: 3}, {matchid: 5, rating: 3, comment: "meh"},
                jasmine.any(Function),
                jasmine.any(Function));

            //Simulate call to error callback
            ProjectService.rateProject.calls.mostRecent().args[3](error);
            expect(scope.ratingSuccess).toBeNull();
            expect(scope.projectRatingError).toBe("ERROR");
            expect(scope.ratingComment).toBe("meh");
        });

        it('should prepare values for the org rating modal if a match is rated', function () {
            scope.matchRatingPermissions[5] = true;
            scope.resourceMatches[5] = {organization: {id: 3}};
            scope.organizationRatings[3] = {rating: 4};

            scope.rateMatch(5);

            expect(scope.currentMatchId).toBe(5);
            expect(scope.currentOrgRating).toBe(4);
        });

        it('should disable rating for a match if it was rated successfully', function () {
            scope.currentMatchId = 5;
            scope.resourceMatches[5] = {organization: {id: 3}};
            scope.currentOrgRating = 3;
            scope.currentOrgRatingComment = "test";

            spyOn(OrganizationService, "rateOrganization");
            spyOn(OrganizationService, "getAggregatedRating");
            scope.rateOrganization();

            expect(OrganizationService.rateOrganization).toHaveBeenCalledWith(
                {id: 3}, {matchid: 5, rating: 3, comment: "test"},
                jasmine.any(Function),
                jasmine.any(Function));

            //Simulate call to success callback
            OrganizationService.rateOrganization.calls.mostRecent().args[2]();
            expect(OrganizationService.getAggregatedRating).toHaveBeenCalledWith({id: 3}, jasmine.any(Function));
            expect(scope.matchRatingPermissions[5]).toBe(false);
        });

        it('should set org rating error if an organization could not be rated successfully', function () {
            scope.currentMatchId = 5;
            scope.resourceMatches[5] = {organization: {id: 3}};
            scope.currentOrgRating = 3;
            scope.currentOrgRatingComment = "test";
            var error = {status: 500}

            spyOn(OrganizationService, "rateOrganization");
            scope.rateOrganization();

            expect(OrganizationService.rateOrganization).toHaveBeenCalledWith(
                {id: 3}, {matchid: 5, rating: 3, comment: "test"},
                jasmine.any(Function),
                jasmine.any(Function));

            //Simulate call to success callback
            OrganizationService.rateOrganization.calls.mostRecent().args[3](error);
            expect(scope.orgRatingError).toBe("ERROR");
        });
        it('should get all available offers for apply', function(){
            // set current project organization id
            scope.project.organizationId = 2;
            spyOn(AccountService, 'get');
            spyOn(OrganizationService, 'getResourceOffers');


            scope.getOffers();
            expect(AccountService.get).toHaveBeenCalled();

            AccountService.get.calls.mostRecent().args[0]({
                id: 1,
                organization: { id: 1 }
            });
            expect(scope.ProjectApply.account.id).toBe(1);

            expect(OrganizationService.getResourceOffers).toHaveBeenCalledWith({ id: 1}, null, jasmine.any(Function));

            OrganizationService.getResourceOffers.calls.mostRecent().args[2](
                [
                    { amount: 10 },
                    { amount: 20 },
                ]
            );
            expect(scope.resourceOffers.length).toBe(2);
        });

        it('should get project follow state', function(){

            //predefine some value(s) for test
            scope.project = existingProject;
            routeParams.id = scope.project.id;

            spyOn(ProjectService, 'followingState');

            scope.followingState();

            expect(ProjectService.followingState).toHaveBeenCalledWith({id: scope.project.id}, jasmine.any(Function));
            //simulate json result
            ProjectService.followingState.calls.mostRecent().args[1]({state: true});
            expect(scope.following).toBe(true);
            expect(scope.showFollow()).toBe(false);
            expect(scope.showUnfollow()).toBe(true);
        });

        it('should add poject as followed by current user', function(){
            //predefine some value(s) for test
            scope.project = existingProject;
            routeParams.id = scope.project.id;

            spyOn(ProjectService, "follow");

            scope.follow();

            expect(ProjectService.follow).toHaveBeenCalledWith({id: scope.project.id}, null, jasmine.any(Function));
            //simulate callback
            ProjectService.follow.calls.mostRecent().args[2]();
            expect(scope.following).toBe(true);
            expect(scope.showFollow()).toBe(false);
            expect(scope.showUnfollow()).toBe(true);
        });

        it('should remove project from following list of current user', function(){
            //predefine some value(s) for test
            scope.project = existingProject;
            routeParams.id = scope.project.id;

            spyOn(ProjectService, "unfollow");

            scope.unfollow();

            expect(ProjectService.unfollow).toHaveBeenCalledWith({id: scope.project.id}, jasmine.any(Function));
            //simulate callback
            ProjectService.unfollow.calls.mostRecent().args[1]();
            expect(scope.following).toBe(false);
            expect(scope.showFollow()).toBe(true);
            expect(scope.showUnfollow()).toBe(false);
        });

        it('should refresh the map to actual coordinates', function() {

            var searchBox = {
                getPlaces: function() {
                    var places = [{
                        geometry: {
                            location:{
                                lat:function(){return 20.0;},
                                lng:function(){return 40.0;}
                            }
                        }
                    }]; return places;}}

            //create mocks
            scope.map.control.refresh = function(obj) {}
            spyOn(scope.map.control, 'refresh');

            scope.placeToMarker(searchBox);

            expect(scope.map.control.refresh).toHaveBeenCalledWith({latitude:20.0, longitude:40.0})
        });

        it('should create a static map', function() {
            scope.project = {projectLocation: {latitude: null, longitude: null}};

            scope.project.projectLocation.latitude = 20;
            scope.project.projectLocation.longitude = 20;

            var link = scope.createStaticMapLink();

            expect(link).toEqual("https://maps.google.com/maps/api/staticmap?center=20%2C20&format=jpg&maptype=terrain&size=533x190&zoom=14&markers=20%2C20");
        });

        it('should get all connections for the current user', function() {

            spyOn(SocialMediaService, 'getConnections');

            scope.getConnections();

            expect(SocialMediaService.getConnections).toHaveBeenCalled();

            SocialMediaService.getConnections.calls.mostRecent().args[0]([
                {provider: 'twitter'},
                {provider: 'facebook'},
                {provider: 'xing'}
            ]);

            expect(scope.twitterConnected).toBe(true);
            expect(scope.facebookConnected).toBe(true);
            expect(scope.xingConnected).toBe(true);
        });

        it('should calculate the collected amount of resources',function() {

            //mock
            scope.resourceRequirementsWithMatches = [
                { 
                    amount: 10,
                    originalAmount: 10,
                    sum: 10,
                    isEssential: true
                },
                { 
                    amount: 5,
                    originalAmount: 10,
                    sum: 5
                },
                { 
                    amount: 5,
                    originalAmount: 10,
                    sum: 5
                }
            ];


            scope.calculateCollected();

            expect(scope.collected).toEqual(67);
            expect(scope.collectedEssential).toEqual(100);

        });


/*        it('should update the project information', function() {
            spyOn(ProjectService, 'get');

            scope.update();

            expect(ProjectService.get).toHaveBeenCalled();
            ProjectService.get.calls.mostRecent().args[1]({
                resourceRequirements: {}
            });


        });*/

    });
});
