/**
 * Created by Clemens Puehringer on 10/11/14.
 */
'use strict';

describe('TextMessage Service Tests ', function () {

    beforeEach(module('respondecoApp'));

    describe('TextMessage', function () {
        var serviceTested,
            httpBackend;

        beforeEach(inject(function($httpBackend, TextMessage) {
            serviceTested = TextMessage;
            httpBackend = $httpBackend;
            //Request on app init
            httpBackend.expectGET('i18n/en.json').respond(200, '');
        }));
        //make sure no expectations were missed in your tests.
        //(e.g. expectGET or expectPOST)
        afterEach(function() {
            httpBackend.verifyNoOutstandingExpectation();
            httpBackend.verifyNoOutstandingRequest();
        });

        it('should call backend when saving a text message', function(){
            //GIVEN
            //set up some data for the http call to return and test later.
            var returnData = { result: 'ok' };
            var textMessage = { receiver: "test", content: "test" };
            //expectGET to make sure this is called once.
            httpBackend.expectPOST('app/rest/textmessages').respond(returnData);

            //WHEN
            serviceTested.save(textMessage);
            //flush the backend to "execute" the request to do the expected POST assertion.
            httpBackend.flush();
        });

        it('should call backend when querying textmessages', function(){
            var textMessages = [{ receiver: "test", content: "test" }];

            httpBackend.expectGET('app/rest/textmessages').respond(textMessages);

            //WHEN
            serviceTested.query();
            httpBackend.flush();
        });

        it('should call backend when deleting a textmessage', function(){
            var returnData = { result: 'ok' };

            httpBackend.expectDELETE('app/rest/textmessages').respond(returnData);

            //WHEN
            serviceTested.delete(1);
            httpBackend.flush();
        });

    });
});