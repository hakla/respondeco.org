/**
 * Created by clemens on 01/12/14.
 */
'use strict';

describe('Project Controller Tests ', function() {
    beforeEach(module('respondecoApp'));

    describe('ProjectController', function() {
        var scope, location, routeParams, translate, ProjectNamesService, PropertyTagsNamesService,
            ProjectService, ResourceRequirementService, OrganizationService;
        var fakeDeferred;
        var emptyProject;

        beforeEach(inject(function($rootScope, $controller, $location, $q, $sce, $routeParams, $translate,
                                   ProjectNames, PropertyTagNames, Project, ResourceRequirement, Organization) {
            scope = $rootScope.$new();
            location = $location;
            translate = $translate;
            ProjectNamesService = ProjectNames;
            PropertyTagsNamesService = PropertyTagNames;
            ProjectService = Project;
            ResourceRequirementService = ResourceRequirement;
            OrganizationService = Organization;
            routeParams = $routeParams;
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

            $controller('ProjectController', {
                $scope: scope,
                $location: location,
                $routeParams: routeParams,
                $translate: translate,
                Project: ProjectService,
                ProjectNames: ProjectNamesService,
                PropertyTagNames: PropertyTagsNamesService,
                ResourceRequirement: ResourceRequirementService,
                Organization: OrganizationService
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
            scope.delete(1);
            expect(ProjectService.delete).toHaveBeenCalledWith({
                id: 1
            }, jasmine.any(Function));
            ProjectService.delete.calls.mostRecent().args[1]();
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

            expect(scope.resource).toEqual({resourceTags:[], isEssential: false});
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
    });
});
