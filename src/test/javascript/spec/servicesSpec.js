'use strict';

describe('Services Tests ', function () {

    beforeEach(module('respondecoApp'));

    describe('AuthenticationSharedService', function () {
        var serviceTested,
            httpBackend,
            authServiceSpied;

        beforeEach(inject(function($httpBackend, AuthenticationSharedService, authService) {
            serviceTested = AuthenticationSharedService;
            httpBackend = $httpBackend;
            authServiceSpied = authService;

            TestHelper.initHttpBackend(httpBackend);
        }));
        //make sure no expectations were missed in your tests.
        //(e.g. expectGET or expectPOST)

        it('should call backend on logout then call authService.loginCancelled', function(){
            //GIVEN
            //set up some data for the http call to return and test later.
            var returnData = { result: 'ok' };
            //expectGET to make sure this is called once.
            httpBackend.expectGET('app/logout').respond(returnData);

            //Set spy
            spyOn(authServiceSpied, 'loginCancelled');

             //WHEN
            serviceTested.logout();
            //flush the backend to "execute" the request to do the expectedGET assertion.
            TestHelper.flushHttpBackend(httpBackend);

            //THEN
            expect(authServiceSpied.loginCancelled).toHaveBeenCalled();
        });

    });
});
