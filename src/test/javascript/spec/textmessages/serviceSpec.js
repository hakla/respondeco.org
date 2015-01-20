/**
 * Created by Clemens Puehringer on 10/11/14.
 */
'use strict';

describe('TextMessage Service Tests ', function () {

    beforeEach(module('respondecoApp'));
    beforeEach(angular.mock.module('ngMockE2E'));

    describe('TextMessage', function () {
        var serviceTested
        var httpBackend;

        beforeEach(inject(function($httpBackend, TextMessage) {
            serviceTested = TextMessage;
            httpBackend = $httpBackend;

            TestHelper.initHttpBackend(httpBackend);
        }));

        afterEach(function() {
            TestHelper.flushHttpBackend(httpBackend);
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
        });

        it('should call backend when querying textmessages', function(){
            var textMessages = [{ receiver: "test", content: "test" }];

            httpBackend.expectGET('app/rest/textmessages').respond(textMessages);

            //WHEN
            serviceTested.query();
        });

        it('should call backend when deleting a textmessage', function(){
            var returnData = { result: 'ok' };

            httpBackend.expectDELETE('app/rest/textmessages').respond(returnData);

            //WHEN
            serviceTested.delete(1);
        });

    });
});
