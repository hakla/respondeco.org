'use strict';

describe('SocialMedia Controller Tests ', function () {
    beforeEach(module('respondecoApp'));

    describe('SocialMediaController', function () {
        var scope, location, window, SocialMediaService;

        beforeEach(inject(function($rootScope, $location, $window, SocialMedia) {
            $scope = $rootScope.$new();
            location = $location;
            window = $window;
            SocialMediaService = SocialMedia;

            $controller('SocialMediaController', {$scope: $scope, $location: location, $window: window, SocialMedia: SocialMediaService});
        }));

        it('should get all connections for the current user', function() {

            spyOn(SocialMediaServer, 'getConnections');

            $scope.getConnections();

            expect(SocialMediaService.getConnections).toHaveBeenCalled();

            SocialMediaService.get.calls.mostRecent().args[1](

                !TODO

            });

            expect($scope.twitterConnected).toBe(true);
            expect($scope.facebookConnected).toBe(true);
            expect($scope.xingConnected).toBe(true);
        });

        it('should connect Facebook', function() {
            spyOn(SocialMediaService, 'connectFacebook');

            $scope.connectFacebook();

            expect(SocialMediaService.connectFacebook).toHaveBeencalled();
            SocialMediaService.connectFacebook.calls.mostRecent().args[1]({
                string: 'http://www.facebook.com'
            });
        });

        it('should connect Twitter', function() {

        });

        it('should connect Xing', function() {

        });

        it('should disconnect facebook', function() {
            spyOn(SocialMediaService, 'disconnectFacebook');
            $scope.facebookConnected = true;

            expect($scope.facebookConnected).toBe(true);
            $scope.disconnectFacebook();

            expect(SocialMediaService.disconnectFacebook).toHaveBeencalled();

            SocialMediaService.disconnectFacebook.calls.mostRecent().args[1]();

            expect($scope.facebookConnected).toBe(false);
        });

        it('should disconnect twitter', function() {
            spyOn(SocialMediaService, 'disconnectTwitter');
            $scope.twitterConnected = true;

            expect($scope.twitterConnected).toBe(true);
            $scope.disconnectTwitter();

            expect(SocialMediaService.disconnectTwitter).toHaveBeencalled();

            SocialMediaService.disconnectTwitter.calls.mostRecent().args[1]();

            expect($scope.twitterConnected).toBe(false);
        });

        it('should add an alert', function() {

            $scope.addAlert('info', 'TestAlert');

        });

        it('should clear the url', function() {

        });

        it('should check for redirect parameters', function() {
            
        });
        


    });




});
