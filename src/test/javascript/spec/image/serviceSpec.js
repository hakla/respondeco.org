/**
 * Created by Clemens Puehringer on 10/11/14.
 */
'use strict';

describe('Image Service Tests ', function () {

    beforeEach(module('respondecoApp'));

    describe('Image', function () {
        var serviceTested,
            httpBackend;

        beforeEach(inject(function($httpBackend, Image) {
            serviceTested = Image;
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
            httpBackend.expectPOST('app/rest/images').respond(returnData);

            //WHEN
            serviceTested.save(textMessage);
        });

    });
});
