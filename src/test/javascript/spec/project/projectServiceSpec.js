/**
 * Created by clemens on 01/12/14.
 */

'use strict';

describe('Project Service Tests ', function () {

    beforeEach(module('respondecoApp'));

    describe('Project', function () {
        var serviceTested,
            httpBackend;

        beforeEach(inject(function($httpBackend, Project) {
            serviceTested = Project;
            httpBackend = $httpBackend;

            TestHelper.initHttpBackend(httpBackend);
        }));
        //make sure no expectations were missed in your tests.
        //(e.g. expectGET or expectPOST)
        afterEach(function() {
            TestHelper.flushHttpBackend(httpBackend);
        });

        it('should call backend with filter parameter when querying for projects by name', function() {
            var returnData = {};
            //expectGET to make sure this is called once.
            httpBackend.expectGET('app/rest/projects?filter=test').respond(returnData);

            //WHEN
            serviceTested.query({filter: "test"});
        });

        it('should call the backend with tags parameter when querying for projects by tags', function(){
            var returnData = {};
            httpBackend.expectGET('app/rest/projects?tags=test,foo,bar').respond(returnData);

            //WHEN
            serviceTested.query({tags: "test,foo,bar"});
        });

        it('should call backend with filter and tags parameter when querying for projects by name and tags', function(){
            var returnData = {};
            httpBackend.expectGET('app/rest/projects?filter=test&tags=test,foo,bar').respond(returnData);

            //WHEN
            serviceTested.query({filter: "test", tags: "test,foo,bar"});
        });

        it('should call backend for aggregated project rating', function(){
            var returnData = {count: 1, rating: 2.0};
            httpBackend.expectGET('app/rest/projects/2/ratings').respond(returnData);

            //WHEN
            serviceTested.getAggregatedRating({pid: 2});
        });

        it('should call backend to create a project rating', function(){
            var testRating = {rating: 3, comment: "kinda mediocre"};
            httpBackend.expectPOST('app/rest/projects/2/ratings', testRating).respond(200);

            //WHEN
            serviceTested.rateProject({pid: 2}, testRating);
        });

        it('should call backend to check if the user has the right to rate a project', function(){
            var testPermisson = "project";
            var testRatingPermissions = [{matchid: null, allowed: true}];
            httpBackend.expectGET('app/rest/projects/2/ratings?permission=project')
                .respond(testRatingPermissions);

            //WHEN
            serviceTested.checkIfRatingPossible({pid: 2, permission: testPermisson});
        });

        it('should call backend to check if the user has the right to rate matches of the project', function(){
            var testPermisson = "matches";
            var testMatches = "3,4,5"
            var testRatingPermissions = [
                {matchid: 3, allowed: true},
                {matchid: 4, allowed: true},
                {matchid: 5, allowed: true}
            ];
            httpBackend.expectGET('app/rest/projects/2/ratings?matches=3,4,5&permission=matches')
                .respond(testRatingPermissions);

            //WHEN
            serviceTested.checkIfRatingPossible({pid: 2, permission: testPermisson, matches: testMatches});
        });

        it('should call backend to create new project apply', function() {
            var testProjectApply = {
                resourceOfferId: 1,
                resourceRequirementId: 1,
                organizationId: 1,
                projectId: 2
            };
            httpBackend.expectPOST('/app/rest/projects/apply', testProjectApply).respond(201);

            //WHEN
            serviceTested.apply(testProjectApply);
        });

        it('should call backend to get all postings', function() {
            var returnData = [];
            httpBackend.expectGET('app/rest/projects/2/postings').respond(returnData);

            //WHEN
            serviceTested.getPostingsByProjectId({id: 2});
        });

        it('should call backend to create a project posting', function(){
            var testPosting = "postingtest";
            httpBackend.expectPOST('/app/rest/projects/2/postings', testPosting).respond(200);

            //WHEN
            serviceTested.addPostingForProject({id: 2}, testPosting);
        });

        it('should call backend to delete a project posting', function(){
            httpBackend.expectDELETE('app/rest/projects/2/postings/5').respond(200);

            //WHEN
            serviceTested.deletePosting({id: 2, pid:5});
        });

        it('should call project backend to create a follow entry', function(){
            httpBackend.expectPOST('app/rest/projects/1/follow').respond(201);
            //WHEN
            serviceTested.follow({id:1}, null);
        });

        it('should call project backend to delete follow entry', function(){
            httpBackend.expectDELETE('app/rest/projects/1/unfollow').respond(200);
            //WHEN
            serviceTested.unfollow({id:1});
        });

        it('should call backend to get projects following state', function() {
            var returnData = {"state": true};
            httpBackend.expectGET('app/rest/projects/1/followingstate').respond(returnData);

            //WHEN
            serviceTested.followingState({id: 1}, null);
        });

    });
});
