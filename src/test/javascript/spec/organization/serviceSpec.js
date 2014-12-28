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
            //Request on app init
            httpBackend.expectGET('i18n/de.json').respond(200, '');
        }));
        //make sure no expectations were missed in your tests.
        //(e.g. expectGET or expectPOST)
        afterEach(function() {
            httpBackend.verifyNoOutstandingExpectation();
            httpBackend.verifyNoOutstandingRequest();
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
            //flush the backend to "execute" the request to do the expected POST assertion.
            httpBackend.flush();
        });

        it('should call backend when querying organization', function(){
            var textMessages = [{ receiver: "test", content: "test" }];

            httpBackend.expectGET('app/rest/organizations').respond(textMessages);

            //WHEN
            serviceTested.query();
            httpBackend.flush();
        });

        it('should call backend when deleting an organization', function(){
            var returnData = { result: 'ok' };

            httpBackend.expectDELETE('app/rest/organizations').respond(returnData);

            //WHEN
            serviceTested.delete(1);
            httpBackend.flush();
        });

        it('should call backend for aggregated organization rating', function(){
            var returnData = {count: 1, rating: 2.0};
            httpBackend.expectGET('app/rest/organizations/2/ratings').respond(returnData);

            //WHEN
            serviceTested.getAggregatedRating({id: 2});
            //flush the backend to "execute" the request to do the expected GET assertion.
            httpBackend.flush();
        });

        it('should call backend to create an organization rating', function(){
            var testRating = {rating: 3, comment: "kinda mediocre"};
            httpBackend.expectPOST('app/rest/organizations/2/ratings', testRating).respond(200);

            //WHEN
            serviceTested.rateOrganization({id: 2}, testRating);
            //flush the backend to "execute" the request to do the expected POST assertion.
            httpBackend.flush();
        });

        it('should call backend to get all postings', function() {
            var returnData = [];
            httpBackend.expectGET('app/rest/organizations/1/postings').respond(returnData);

            //WHEN
            serviceTested.getPostingsByOrgId({id: 1});
            //flush the backend to "execute" the request to do the expected GET assertion.
            httpBackend.flush();
        });

        it('should call backend to create a organization posting', function(){
            var testPosting = "postingtest";
            httpBackend.expectPOST('/app/rest/organizations/1/postings', testPosting).respond(200);

            //WHEN
            serviceTested.addPostingForOrganization({id: 1}, testPosting);
            //flush the backend to "execute" the request to do the expected POST assertion.
            httpBackend.flush();
        });

        it('should call backend to delete a  posting', function(){
            httpBackend.expectDELETE('app/rest/organizations/1/postings/1').respond(200);

            //WHEN
            serviceTested.deletePosting({id: 1, pid:1});
            //flush the backend to "execute" the request to do the expected DELETE assertion.
            httpBackend.flush();
        });


    });
});
