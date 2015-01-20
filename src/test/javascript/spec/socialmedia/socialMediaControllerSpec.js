'use strict';

describe('SocialMedia Controller Tests ', function () {
    beforeEach(module('respondecoApp'));

    describe('SocialMediaController', function () {
        var scope, location, windowMock, SocialMediaService, rootScope;

        beforeEach(inject(function($rootScope, $controller, SocialMedia) {
            rootScope = $rootScope;
            scope = $rootScope.$new();
            
            var fakeLocation = {};

            fakeLocation.absUrl = function() {
                return 'http://respondeco.org/#/social-networks?verifier=1234';
            }
            
            //mock for $window, otherwise a full page reload would happen
            windowMock = {
                document: {
                    location: {
                        search: {
                            substring: {}
                        }
                    }
                },
                location: {
                    href: ''
                }
            };

            //here the verification codes for facebook and twitter are concatenated
            //otherwise a new describe block would be necessary
            windowMock.document.location.search.substring = function(data) {
                return 'oauth_token=123123123123&oauth_verifier=1234&code=123456789asdfasdfasdf';
            }

            SocialMediaService = SocialMedia;

            $controller('SocialMediaController', {$rootScope: rootScope, $scope: scope, $location: fakeLocation, $window: windowMock, SocialMedia: SocialMediaService});
        }));

        it('should get all connections for the current user', function() {

            spyOn(SocialMediaService, 'getConnections');

            scope.getConnections();

            expect(SocialMediaService.getConnections).toHaveBeenCalled();

            SocialMediaService.getConnections.calls.mostRecent().args[0]([
                {provider: 'twitter'},
                {provider: 'facebook'},
                {provider: 'xing'}
            ]);

            expect(scope.twitterConnected).toBe(true);
            expect(scope.facebookConnected).toBe(true);
            expect(scope.xingConnected).toBe(true);
        });

        it('should connect Facebook', function() {
            spyOn(SocialMediaService, 'connectFacebook');

            scope.connectFacebook();

            expect(SocialMediaService.connectFacebook).toHaveBeenCalled();
            SocialMediaService.connectFacebook.calls.mostRecent().args[0]({
                string: 'http://www.facebook.com'
            });
        });

        it('should show an error if connect Facebook fails', function() {
            spyOn(SocialMediaService, 'connectFacebook');
            spyOn(scope, 'addAlert');

            scope.connectFacebook();

            expect(SocialMediaService.connectFacebook).toHaveBeenCalled();
            SocialMediaService.connectFacebook.calls.mostRecent().args[1]({
                string: 'http://www.facebook.com'
            });

            expect(scope.addAlert).toHaveBeenCalled();
        });

        it('should connect Twitter', function() {
            spyOn(SocialMediaService, 'connectTwitter');

            scope.connectTwitter();

            expect(SocialMediaService.connectTwitter).toHaveBeenCalled();
            SocialMediaService.connectTwitter.calls.mostRecent().args[0]({
                string: 'http://www.twitter.com'
            });
        });

        it('should show an error if connect Twitter fails', function() {
            spyOn(SocialMediaService, 'connectTwitter');
            spyOn(scope, 'addAlert');

            scope.connectTwitter();

            expect(SocialMediaService.connectTwitter).toHaveBeenCalled();
            SocialMediaService.connectTwitter.calls.mostRecent().args[1]({
                string: 'http://www.facebook.com'
            });

            expect(scope.addAlert).toHaveBeenCalled();
        });

        it('should connect Xing', function() {
            spyOn(SocialMediaService, 'connectXing');

            scope.connectXing();

            expect(SocialMediaService.connectXing).toHaveBeenCalled();
            SocialMediaService.connectXing.calls.mostRecent().args[0]({
                string: 'http://www.xing.com'
            });
        });

         it('should show an error if connect Xing fails', function() {
            spyOn(SocialMediaService, 'connectXing');
            spyOn(scope, 'addAlert');

            scope.connectXing();

            expect(SocialMediaService.connectXing).toHaveBeenCalled();
            SocialMediaService.connectXing.calls.mostRecent().args[1]({
                string: 'http://www.facebook.com'
            });

            expect(scope.addAlert).toHaveBeenCalled();
        });


        it('should disconnect facebook', function() {
            spyOn(SocialMediaService, 'disconnectFacebook');
            scope.facebookConnected = true;

            expect(scope.facebookConnected).toBe(true);
            scope.disconnectFacebook();

            expect(SocialMediaService.disconnectFacebook).toHaveBeenCalled();

            SocialMediaService.disconnectFacebook.calls.mostRecent().args[0]();

            expect(scope.facebookConnected).toBe(false);
        });

        it('should disconnect twitter', function() {
            spyOn(SocialMediaService, 'disconnectTwitter');
            scope.twitterConnected = true;

            expect(scope.twitterConnected).toBe(true);
            scope.disconnectTwitter();

            expect(SocialMediaService.disconnectTwitter).toHaveBeenCalled();

            SocialMediaService.disconnectTwitter.calls.mostRecent().args[0]();

            expect(scope.twitterConnected).toBe(false);
        });

        it('should disconnect xing', function() {
            spyOn(SocialMediaService, 'disconnectXing');
            spyOn(scope, 'addAlert');

            scope.xingConnected = true;

            expect(scope.xingConnected).toBe(true);
            scope.disconnectXing();

            expect(SocialMediaService.disconnectXing).toHaveBeenCalled();
            
            SocialMediaService.disconnectXing.calls.mostRecent().args[0]();

            expect(scope.addAlert).toHaveBeenCalled();
            expect(scope.xingConnected).toBe(false);
        });

        it('should add an alert', function() {
            rootScope.globalAlerts.push = function() {};

            spyOn(rootScope.globalAlerts, 'push');
            scope.addAlert('info', 'TestAlert');

            expect(rootScope.globalAlerts.push).toHaveBeenCalled();
        });

        it('should clear the url', function() {
            scope.clearURL();

            expect(windowMock.location.href).toEqual("/#/social-networks");
        });

        it('should check for redirect parameters', function() {

            spyOn(SocialMediaService, 'createXingConnection');
            spyOn(SocialMediaService, 'createTwitterConnection');

            scope.checkForRedirectParams();

            expect(SocialMediaService.createTwitterConnection).toHaveBeenCalledWith({token: '123123123123', verifier: '1234'}, 
                jasmine.any(Function), jasmine.any(Function));

            expect(SocialMediaService.createXingConnection).toHaveBeenCalledWith({string: 1234},
                jasmine.any(Function), jasmine.any(Function));

            SocialMediaService.createXingConnection.calls.mostRecent().args[1]();
            SocialMediaService.createXingConnection.calls.mostRecent().args[2]();

            SocialMediaService.createTwitterConnection.calls.mostRecent().args[1]();
            SocialMediaService.createTwitterConnection.calls.mostRecent().args[2]();
        });
        
        it('should check for facebook redirect parameters', function() {
            spyOn(SocialMediaService, 'createFacebookConnection');
            scope.facebookConnected = false;

            scope.checkForRedirectParams();

            expect(SocialMediaService.createFacebookConnection).toHaveBeenCalledWith({string: '123456789asdfasdfasdf'}, 
                jasmine.any(Function), jasmine.any(Function));

            SocialMediaService.createFacebookConnection.calls.mostRecent().args[1]();

            SocialMediaService.createFacebookConnection.calls.mostRecent().args[2]();
        });


    });




});
