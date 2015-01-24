/**
 * Created by clemens on 23/01/15.
 */

'use strict';

describe('Project Controller Tests ', function() {
    beforeEach(module('respondecoApp'));

    describe('ProjectController', function () {
        var scope, location, routeParams, ProjectNamesService, PropertyTagsNamesService, ProjectService,
            ResourceRequirementService, AccountService, sce, OrganizationService, translate, SocialMediaService,
            rootScope, AuthenticationSharedService;
        var fakeDeferred;
        var emptyProject;
        var existingProject;

        beforeEach(inject(function ($rootScope, $controller, $location, $q, $sce, $routeParams, ProjectNames,
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
                    then: function (object) {
                    }
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

            scope.showOrgRatingModal = function () {
            }
            scope.hideOrgRatingModal = function () {
            }
            scope.setProjectRatingError = function (error) {
            }
            scope.setOrgRatingError = function (error) {
            }
        }));

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

        it('should show the rating comment window if the user is allowed to rate', function () {
            scope.canRate = true;
            scope.showRating();
            expect(scope.isRating).toBeTruthy();
        });

        it('should refresh the organization rating', function () {
            spyOn(OrganizationService, 'getAggregatedRating');
            scope.refreshOrganizationRating(1);
            expect(OrganizationService.getAggregatedRating).toHaveBeenCalledWith({id: 1}, jasmine.any(Function));
            OrganizationService.getAggregatedRating.calls.mostRecent().args[1]({rating: 5, count: 10});
            expect(scope.organizationRatings[1].rating).toBe(5);
        });

        it('should call the service to refresh the rating', function () {
            spyOn(ProjectService, 'getAggregatedRating');
            routeParams.id = 1;
            scope.refreshProjectRating();
            expect(ProjectService.getAggregatedRating).toHaveBeenCalledWith({pid: 1}, jasmine.any(Function));
            ProjectService.getAggregatedRating.calls.mostRecent().args[1]({rating: 4, count: 10});
            expect(scope.shownRating).toBe(4);
            expect(scope.ratingCount).toBe(10);
        });

        it('should call the service to refresh rating permissions', function () {
            spyOn(ProjectService, 'checkIfRatingPossible');
            routeParams.id = 1;
            scope.refreshProjectRating();
            expect(ProjectService.checkIfRatingPossible).toHaveBeenCalledWith(
                {pid: 1, permission: "project"}, jasmine.any(Function));
            ProjectService.checkIfRatingPossible.calls.mostRecent().args[1]([{allowed: true, matchid: 10}]);
            expect(scope.canRate).toBe(true);
            expect(scope.ratedMatch).toBe(10);
        });

    });
});
