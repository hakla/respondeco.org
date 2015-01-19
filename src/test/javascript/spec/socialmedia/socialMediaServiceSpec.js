'use strict';

describe('SocialMedia Service Tests ', function () {

    beforeEach(module('respondecoApp'));

    describe('SocialMedia', function () {
        var serviceTested,
            httpBackend;

        beforeEach(inject(function($httpBackend, SocialMedia) {
            serviceTested = SocialMedia;
            httpBackend = $httpBackend;
        }));
        //make sure no expectations were missed in your tests.
        //(e.g. expectGET or expectPOST)
        afterEach(function() {
            httpBackend.verifyNoOutstandingExpectation();
            httpBackend.verifyNoOutstandingRequest();
        });

        it('should call backend for all connections of the currently logged in user', function() {
            var returnData = {};
            
            httpBackend.expectGET('app/rest/connections').respond(returnData);

            serviceTested.getConnections();
            httpBackend.flush();
        });

        it('should call backend to start connecting the user with facebook', function() {
            var returnData = {};
            
            httpBackend.expectGET('app/rest/connect/facebook').respond(returnData);

            serviceTested.connectFacebook();
            httpBackend.flush();
        });

        it('should call backend to create the facebook connection', function() {
            var returnData = {};
            
            httpBackend.expectPOST('app/rest/connect/facebook/createconnection').respond(returnData);

            serviceTested.createFacebookConnection();
            httpBackend.flush();
        });

        it('should call backend to create a new facebook post', function() {
            var returnData = {};
            
            httpBackend.expectPOST('app/rest/socialmedia/facebook/post').respond(returnData);

            serviceTested.createFacebookPost();
            httpBackend.flush();
        });

        it('should call backend to disconnect the users account from facebook', function() {
            var returnData = {};
            
            httpBackend.expectDELETE('app/rest/socialmedia/facebook/disconnect').respond(returnData);

            serviceTested.deleteFacebook();
            httpBackend.flush();
        });

        it('should call backend to start connecting the user with twitter', function() {
            var returnData = {};
            
            httpBackend.expectGET('app/rest/connect/twitter').respond(returnData);

            serviceTested.connectTwitter();
            httpBackend.flush();
        });

        it('should call backend to create the twitter connection', function() {
            var returnData = {};
            
            httpBackend.expectPOST('app/rest/connect/twitter/createconnection').respond(returnData);

            serviceTested.createTwitterConnection();
            httpBackend.flush();
        });

        it('should call backend to create a new twitter post', function() {
            var returnData = {};
            
            httpBackend.expectPOST('app/rest/socialmedia/twitter/post').respond(returnData);

            serviceTested.createTwitterPost();
            httpBackend.flush();
        });

        it('should call backend to disconnect the users account from twitter', function() {
            var returnData = {};
            
            httpBackend.expectDELETE('app/rest/socialmedia/twitter/disconnect').respond(returnData);

            serviceTested.deleteTwitter();
            httpBackend.flush();
        });

         it('should call backend to start connecting the user with xing', function() {
            var returnData = {};
            
            httpBackend.expectGET('app/rest/connect/xing').respond(returnData);

            serviceTested.connectXing();
            httpBackend.flush();
        });

        it('should call backend to create the xing connection', function() {
            var returnData = {};
            
            httpBackend.expectPOST('app/rest/connect/xing/createconnection').respond(returnData);

            serviceTested.createXingConnection();
            httpBackend.flush();
        });

        it('should call backend to create a new xing post', function() {
            var returnData = {};
            
            httpBackend.expectPOST('app/rest/socialmedia/xing/post').respond(returnData);

            serviceTested.createXingPost();
            httpBackend.flush();
        });

        it('should call backend to disconnect the users account from xing', function() {
            var returnData = {};
            
            httpBackend.expectDELETE('app/rest/socialmedia/xing/disconnect').respond(returnData);

            serviceTested.deleteXing();
            httpBackend.flush();
        });





    });
});
