/**
 * Created by Clemens Puehringer on 10/11/14.
 */
'use strict';

describe('Organization Service Tests ', function () {

    beforeEach(module('respondecoApp'));

    describe('Organization', function () {
        var serviceTested,
            httpBackend;

        beforeEach(inject(function($httpBackend, Organization) {
            serviceTested = Organization;
            httpBackend = $httpBackend;

            TestHelper.initHttpBackend(httpBackend);
        }));

        afterEach(function() {
            TestHelper.flushHttpBackend(httpBackend);
        });

        it('should call backend when querying an organization', function(){
            //GIVEN
            //set up some data for the http call to return and test later.
            var returnData = { result: 'ok' };
            var textMessage = { receiver: "test", content: "test" };
            //expectGET to make sure this is called once.
            httpBackend.expectPOST('app/rest/organizations').respond(returnData);

            //WHEN
            serviceTested.save(textMessage);
        });

        it('should call backend when querying organization', function(){
            var organizations = {};

            httpBackend.expectGET('app/rest/organizations').respond(organizations);

            //WHEN
            serviceTested.query();
        });

        it('should call backend when deleting an organization', function(){
            var returnData = { result: 'ok' };

            httpBackend.expectDELETE('app/rest/organizations').respond(returnData);

            //WHEN
            serviceTested.delete(1);
        });

        it('should call backend for aggregated organization rating', function(){
            var returnData = {count: 1, rating: 2.0};
            httpBackend.expectGET('app/rest/organizations/2/ratings').respond(returnData);

            //WHEN
            serviceTested.getAggregatedRating({id: 2});
        });

        it('should call backend to create an organization rating', function(){
            var testRating = {rating: 3, comment: "kinda mediocre"};
            httpBackend.expectPOST('app/rest/organizations/2/ratings', testRating).respond(200);

            //WHEN
            serviceTested.rateOrganization({id: 2}, testRating);
        });

        it('should call backend to get all postings', function() {
            var returnData = {};
            httpBackend.expectGET('app/rest/organizations/1/postings').respond(returnData);

            //WHEN
            serviceTested.getPostingsByOrgId({id: 1});
        });

        it('should call backend to create a organization posting', function(){
            var testPosting = "postingtest";
            httpBackend.expectPOST('/app/rest/organizations/1/postings', testPosting).respond(200);

            //WHEN
            serviceTested.addPostingForOrganization({id: 1}, testPosting);
        });

        it('should call backend to delete a  posting', function(){
            httpBackend.expectDELETE('app/rest/organizations/1/postings/1').respond(200);

            //WHEN
            serviceTested.deletePosting({id: 1, pid:1});
        });


        it('should call Organization backend to create a follow entry', function(){
            httpBackend.expectPOST('app/rest/organizations/1/follow').respond(201);
            //WHEN
            serviceTested.follow({id:1}, null);
        });

        it('should call Organization backend to delete follow entry', function(){
            httpBackend.expectDELETE('app/rest/organizations/1/unfollow').respond(200);
            //WHEN
            serviceTested.unfollow({id:1});
        });

        it('should call backend to get Organization following state', function() {
            var returnData = {"state": true};
            httpBackend.expectGET('app/rest/organizations/1/followingstate').respond(returnData);

            //WHEN
            serviceTested.followingState({id: 1}, null);
        });

    });
});
