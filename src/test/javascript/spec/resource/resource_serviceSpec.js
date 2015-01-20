'use strict';

describe('Resource Service Tests ', function () {

    beforeEach(module('respondecoApp'));

    describe('Resource', function () {
        var serviceTested,
            httpBackend;

        beforeEach(inject(function($httpBackend, Resource) {
            serviceTested = Resource;
            httpBackend = $httpBackend;

            TestHelper.initHttpBackend(httpBackend);
        }));

        it('should call the backend when resource gets saved', function(){
            var returnData = { result: 'ok' };
            var resource = { name: 'Resource', amount: '1', dateStart: '2014-11-11',
                dateEnd: '2014-11-11', isCommercial: 'true'};
            var organizationId = 1;

            httpBackend.expectPOST('app/rest/resourceoffers').respond(returnData);
            serviceTested.save(resource);

            TestHelper.flushHttpBackend(httpBackend);
        });

        it('should get resources from the backend', function() {

            httpBackend.expectGET('app/rest/resourceoffers').respond(
                {
                    resourceOffers: [{
                        id: 0, name: 'TestResource', amount: '7', dateStart: '2014-11-11',
                        dateEnd: '2014-11-11', isCommercial: 'true'
                    }, {
                        id: 1, name: 'TestResource2', amount: '7', dateStart: '2014-11-11',
                        dateEnd: '2014-11-11', isCommercial: 'false'
                    }, {
                        id: 2, name: 'TestResource3', amount: '7', dateStart: '2014-11-11',
                        dateEnd: '2014-11-11', isCommercial: 'true'
                    }],
                    totalItems: 3
                    
                });

            var resources;

            serviceTested.query(function (response) {
                resources = response.resourceOffers;
            });

            expect(resources).toBeUndefined();
            TestHelper.flushHttpBackend(httpBackend);
            expect(resources).toBeDefined();
        });
    });
});
