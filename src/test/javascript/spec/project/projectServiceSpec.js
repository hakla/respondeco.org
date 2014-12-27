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
            //Request on app init
            httpBackend.expectGET('i18n/de.json').respond(200, '');
        }));
        //make sure no expectations were missed in your tests.
        //(e.g. expectGET or expectPOST)
        afterEach(function() {
            httpBackend.verifyNoOutstandingExpectation();
            httpBackend.verifyNoOutstandingRequest();
        });

        it('should call backend with filter parameter when querying for projects by name', function() {
            var returnData = {};
            //expectGET to make sure this is called once.
            httpBackend.expectGET('app/rest/projects?filter=test').respond(returnData);

            //WHEN
            serviceTested.query({filter: "test"});
            //flush the backend to "execute" the request to do the expected GET assertion.
            httpBackend.flush();
        });

        it('should call the backend with tags parameter when querying for projects by tags', function(){
            var returnData = {};
            httpBackend.expectGET('app/rest/projects?tags=test,foo,bar').respond(returnData);

            //WHEN
            serviceTested.query({tags: "test,foo,bar"});
            //flush the backend to "execute" the request to do the expected GET assertion.
            httpBackend.flush();
        });

        it('should call backend with filter and tags parameter when querying for projects by name and tags', function(){
            var returnData = {};
            httpBackend.expectGET('app/rest/projects?filter=test&tags=test,foo,bar').respond(returnData);

            //WHEN
            serviceTested.query({filter: "test", tags: "test,foo,bar"});
            //flush the backend to "execute" the request to do the expected GET assertion.
            httpBackend.flush();
        });

        it('should call backend for aggregated project rating', function(){
            var returnData = {count: 1, rating: 2.0};
            httpBackend.expectGET('app/rest/projects/2/ratings').respond(returnData);

            //WHEN
            serviceTested.getAggregatedRating({pid: 2});
            //flush the backend to "execute" the request to do the expected GET assertion.
            httpBackend.flush();
        });

        it('should call backend to create a project rating', function(){
            var testRating = {rating: 3, comment: "kinda mediocre"};
            httpBackend.expectPOST('app/rest/projects/2/ratings', testRating).respond(200);

            //WHEN
            serviceTested.rateProject({pid: 2}, testRating);
            //flush the backend to "execute" the request to do the expected POST assertion.
            httpBackend.flush();
        });

        it('should call backend to check if the user has the right to rate a project', function(){
            var testPermisson = "project";
            var testRatingPermissions = [{matchid: null, allowed: true}];
            httpBackend.expectGET('app/rest/projects/2/ratings?permission=project')
                .respond(testRatingPermissions);

            //WHEN
            serviceTested.checkIfRatingPossible({pid: 2, permission: testPermisson});
            //flush the backend to "execute" the request to do the expected POST assertion.
            httpBackend.flush();
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
            //flush the backend to "execute" the request to do the expected POST assertion.
            httpBackend.flush();
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
            //flush the backend to "execute" the request to do the expected POST assertion.
            httpBackend.flush();
        });

    });
});
