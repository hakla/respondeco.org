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

    });
});
