'use strict';

describe('ProjectLocation Controller Tests ', function () {
    beforeEach(module('respondecoApp'));

    describe('ProjectLocationController', function () {
        var scope, location, ProjectService, windowMock;
        
        beforeEach(inject(function ($rootScope, $controller, $location, Project) {
            scope = $rootScope.$new();
            location = $location;
            ProjectService = Project;
            windowMock = {};
            windowMock.navigator = {};
            windowMock.navigator.geolocation = {};
            windowMock.navigator.geolocation.getCurrentPosition = function() {
                var position = {
                    coords: {
                        latitude: 20.0,
                        longitude: 10.0 
                    }
                };

                return position;
            }

            $controller('ProjectLocationController', {$scope: scope, $window: windowMock, $location: location, Project: ProjectService});
        }));

        it('should create the map', function () {
            expect(scope.map).toBeDefined();
            expect(scope.marker).toBeDefined();
        });

        it('should refresh the map to actual coordinates', function() {
    
            var searchBox = {
                getPlaces: function() {
                    var places = [{
                        geometry: {
                            location:{
                                lat:function(){return 20.0;},
                                lng:function(){return 40.0;} 
                            }
                        }
                    }]; return places;}}


            //create mocks
            scope.map.control.refresh = function(obj) {}
            spyOn(scope.map.control, 'refresh');

            scope.placeToMarker(searchBox);

            expect(scope.map.control.refresh).toHaveBeenCalledWith({latitude:20.0, longitude:40.0})

        })

        it('should return if searchBox getPlaces is undefined', function() {
            var searchBox = {getPlaces: function() {
                return 'undefined';
            }};

            scope.placeToMarker(searchBox);
        });


        it('should find near projects - check call', function() {
            var expectedParams = {
                latitude: 20,
                longitude: 40,
                radius: 100
            }

            //mock marker
            scope.marker = {
                coords : {
                    latitude: 20,
                    longitude: 40
                }
            }

            spyOn(ProjectService, 'getNearProjects');


            scope.findNearProjects();

            expect(ProjectService.getNearProjects).toHaveBeenCalledWith(expectedParams, jasmine.any(Function));
        });

        it('should find near projects - no projects found', function() {
            //mock marker
            scope.marker = {
                coords : {
                    latitude: 20,
                    longitude: 40
                }
            }

            spyOn(ProjectService, 'getNearProjects');

            scope.findNearProjects();
            ProjectService.getNearProjects.calls.mostRecent().args[1]([]);

            expect(scope.noProjectsFound).toBe(true);
        });

        it('should find near projects - projects found', function() {
            //mock marker
            scope.marker = {
                coords : {
                    latitude: 20,
                    longitude: 40
                }
            }

            spyOn(ProjectService, 'getNearProjects');

            scope.findNearProjects();
            ProjectService.getNearProjects.calls.mostRecent().args[1]([
                {project: {id:1}}, {project: {id:2}}
            ]);

            expect(scope.noProjectsFound).toBe(false);
            expect(scope.projects).toEqual([{id:1},{id:2}]);
        });

        it('should set alertMessage if marker coordinates are undefined', function() {
            scope.marker.coords = undefined;

            expect(scope.alertMessage).toBeUndefined();;
            scope.findNearProjects();
            expect(scope.alertMessage).toBeDefined();
        });

        it('should locate the user via geolocation', function() {
            spyOn(windowMock.navigator.geolocation,"getCurrentPosition");


            scope.position = {};
            //mock
            scope.showLocationOnMap = function(lat, lng) {};


            scope.geolocate();

            windowMock.navigator.geolocation.getCurrentPosition.calls.mostRecent().args[0]({
                coords: {
                    latitude: 20.0,
                    longitude: 10.0 
                }
            });

            expect(scope.showAccept).toBe(false);
            expect(scope.position).toEqual({
               coords: {
                    latitude: 20.0,
                    longitude: 10.0 
                } 
            });
            expect(scope.marker.coords).toEqual({latitude:20.0, longitude: 10.0});





        });

        it('should show permission denied error if user denies geolocation', function() {
            spyOn(windowMock.navigator.geolocation,"getCurrentPosition");

            scope.position = {};
            scope.geolocate();

            windowMock.navigator.geolocation.getCurrentPosition.calls.mostRecent().args[1]({
                code: 1,
                PERMISSION_DENIED: 1,
                POSITION_UNAVAILABLE: 2,
                TIMEOUT: 3
            });

            expect(scope.alertMessage).toEqual("nearProjects.error.PERMISSION_DENIED");
        });

        it('should show error if position not available', function() {
            spyOn(windowMock.navigator.geolocation,"getCurrentPosition");

            scope.position = {};
            scope.geolocate();

            windowMock.navigator.geolocation.getCurrentPosition.calls.mostRecent().args[1]({
                code: 2,
                PERMISSION_DENIED: 1,
                POSITION_UNAVAILABLE: 2,
                TIMEOUT: 3
            });

            expect(scope.alertMessage).toEqual("nearProjects.error.POSITION_UNAVAILABLE");
        });

        it('should show error geolocation timed out', function() {
            spyOn(windowMock.navigator.geolocation,"getCurrentPosition");

            scope.position = {};
            scope.geolocate();

            windowMock.navigator.geolocation.getCurrentPosition.calls.mostRecent().args[1]({
                code: 3,
                PERMISSION_DENIED: 1,
                POSITION_UNAVAILABLE: 2,
                TIMEOUT: 3
            });

            expect(scope.alertMessage).toEqual("nearProjects.error.TIMEOUT");
        });

        it('should show error if unknown geolocation error occurs', function() {
            spyOn(windowMock.navigator.geolocation,"getCurrentPosition");

            scope.position = {};
            scope.geolocate();

            windowMock.navigator.geolocation.getCurrentPosition.calls.mostRecent().args[1]({
                code: 4,
                PERMISSION_DENIED: 1,
                POSITION_UNAVAILABLE: 2,
                TIMEOUT: 3,
                UNKNOWN_ERROR: 4
            });

            expect(scope.alertMessage).toEqual("nearProjects.error.UNKNOWN_ERROR");
        });

        it('should redirect to project', function() {
            scope.redirectToProject({id:1});
            expect(location.path()).toContain('/projects/1');
        });

        it('should enable the searchBox', function() {
            expect(scope.showSearchBox).toBe(false);
            scope.enableAddress();
            expect(scope.showSearchBox).toBe(true);
        });

        it('should show location on map', function() {
            scope.map.control.refresh = function(){};

            var lat = 20.5;
            var lng = 40.5;
            scope.showLocationOnMap(lat,lng);
        });
    });
});