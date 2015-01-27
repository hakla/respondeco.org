/**
 * Created by clemens on 22/01/15.
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
        var existingPostings;

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
            };
            scope.hideOrgRatingModal = function () {
            };
            scope.setProjectRatingError = function (error) {
            };
            scope.setOrgRatingError = function (error) {
            };

            existingPostings = {totalElements: 2, postings:[{id:1, information:"test1"}, {id:2, information:"test2"}]};
        }));

        it('should call the backend to delete a posting', function() {
            spyOn(ProjectService, 'deletePosting');
            scope.project = {id: 1};
            scope.deletePosting(2);

            expect(ProjectService.deletePosting).toHaveBeenCalledWith(
                {id: 1, pid: 2},
                jasmine.any(Function)
            );

        });

        it('should refresh postings when deleting a posting', function() {
            spyOn(ProjectService, 'deletePosting');
            spyOn(ProjectService, 'getPostingsByProjectId');
            scope.project = {id: 1};
            scope.deletePosting(2);

            expect(ProjectService.deletePosting).toHaveBeenCalledWith(
                {id: 1, pid: 2},
                jasmine.any(Function));

            routeParams.id = 1;
            scope.postingPage = 1;
            scope.postingPageSize = 20;
            //call refreshPostings()
            ProjectService.deletePosting.calls.mostRecent().args[1]();

            expect(ProjectService.getPostingsByProjectId).toHaveBeenCalledWith(
                {id: 1, page: 1, pageSize: 20},
                jasmine.any(Function)
            );

            ProjectService.getPostingsByProjectId.calls.mostRecent().args[1](existingPostings);

            //check if the number of total postings was set correctly
            expect(scope.postingsTotal).toBe(2);
            //check if postings were added to the scope
            expect(scope.postings[0].id).toBe(1);
        });

        it('should return true if more postings are available', function() {
            scope.postings = {length: 5};
            scope.postingsTotal = 10;
            expect(scope.canShowMorePostings()).toBeTruthy();
        });

        it('should return false if no more postings are available', function() {
            scope.postings = {length: 5};
            scope.postingsTotal = 5;
            expect(scope.canShowMorePostings()).toBeFalsy();
        });

        it('should not add a posting if it is shorter than 5 characters', function() {
            spyOn(ProjectService, 'addPostingForProject');
            scope.postingInformation = {length: 3};
            scope.addPosting();
            expect(ProjectService.addPostingForProject).not.toHaveBeenCalled();
        });

        it('should not add a posting if it is longer than 2048 characters', function() {
            spyOn(ProjectService, 'addPostingForProject');
            scope.postingInformation = {length: 3000};
            scope.addPosting();
            expect(ProjectService.addPostingForProject).not.toHaveBeenCalled();
        });

        it('should add a posting if its length is within bounds', function() {
            spyOn(ProjectService, 'addPostingForProject');
            routeParams.id = 1;
            scope.postingform = {$setPristine: function(){}};
            scope.postingInformation = "this is a test posting";
            scope.postingPage = 0;
            scope.postingsTotal = 2;
            scope.addPosting();
            expect(ProjectService.addPostingForProject).toHaveBeenCalledWith(
                {id: 1}, scope.postingInformation,
                jasmine.any(Function));

            ProjectService.addPostingForProject.calls.mostRecent().args[2]({id: 2, information: "this is a test posting"});
            expect(scope.postings[0].information).toBe("this is a test posting");
            expect(scope.postingsTotal).toBe(3);
        });

        it('should post on facebook if the flag is set', function() {
            spyOn(SocialMediaService, 'createFacebookPost');
            location.url = function() {return "test.url"};
            scope.postingInformation = "test posting";
            scope.postOnFacebook = true;
            scope.addPosting();
            expect(SocialMediaService.createFacebookPost).toHaveBeenCalledWith(
                {urlPath: "test.url", post: scope.postingInformation});
        });

        it('should post on twitter if the flag is set', function() {
            spyOn(SocialMediaService, 'createTwitterPost');
            location.url = function() {return "test.url"};
            scope.postingInformation = "test posting";
            scope.postOnTwitter = true;
            scope.addPosting();
            expect(SocialMediaService.createTwitterPost).toHaveBeenCalledWith(
                {urlPath: "test.url", post: scope.postingInformation});
        });

        it('should post on xing if the flag is set', function() {
            spyOn(SocialMediaService, 'createXingPost');
            location.url = function() {return "test.url"};
            scope.postingInformation = "test posting";
            scope.postOnXing = true;
            scope.addPosting();
            expect(SocialMediaService.createXingPost).toHaveBeenCalledWith(
                {urlPath: "test.url", post: scope.postingInformation});
        });

    });
});
