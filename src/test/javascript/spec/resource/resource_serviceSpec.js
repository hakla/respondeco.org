'use strict';

describe('Resource Service Tests ', function () {

    beforeEach(module('respondecoApp'));

    describe('Resource', function () {
        var serviceTested,
            httpBackend;

        beforeEach(inject(function($httpBackend, Resource) {
            serviceTested = Resource;
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
            var resource = { name: 'Resource', amount: '1', dateStart: 'Dec 6, 2014 20:35:02',
                dateEnd: 'Dec 6, 2013 20:35:02', isCommercial: 'true', isRecurrent: 'true',
                company: 'Company' };
            //expectGET to make sure this is called once.
            httpBackend.expectPOST('app/rest/resources').respond(returnData);

            //WHEN
            serviceTested.save(resource);
            //flush the backend to "execute" the request to do the expected POST assertion.
            httpBackend.flush();
        });

        it('should get resources from the backend', function() {

            var resourcesBackend = [{
                id: 0, name: 'TestResource', amount: '7', dateStart: 'Dec 6, 2013 20:35:02',
                dateEnd: 'Dec 6, 2013 20:35:02', isCommercial: 'true', isRecurrent: 'true',
                company: 'Company'
            }, {
                id: 1, name: 'TestResource2', amount: '7', dateStart: 'Dec 6, 2013 20:35:02',
                dateEnd: 'Dec 6, 2013 20:35:02', isCommercial: 'false', isRecurrent: 'true',
                company: 'Company'
            }, {
                id: 2, name: 'TestResource3', amount: '7', dateStart: 'Dec 6, 2013 20:35:02',
                dateEnd: 'Dec 6, 2013 20:35:02', isCommercial: 'true', isRecurrent: 'true',
                company: 'Company'
            }];

            httpBackend.expectGET('app/rest/resources').respond(resourcesBackend);

            serviceTested.query();

            httpBackend.flush();
        })
    });
});
