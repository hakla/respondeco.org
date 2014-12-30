'use strict';

/**
 * ProjectLocationController
 */
respondecoApp.controller('ProjectLocationController', function ($scope, $location, Project) {

  	$scope.projects = [];

  	$scope.showMap = false;
  	$scope.noProjectsFound = false;
  	$scope.searchStarted = false;
    $scope.showSearchBox = false;
  	
    //the places_changed function is called whenever a new place is entered
    var searchBoxEvents = {
        places_changed: function (searchBox) {
            var id = 0;
            $scope.placeToMarker(searchBox);
        }
    }

    $scope.searchBox = { template:'searchBox.template.html', events:searchBoxEvents, parentdiv: "searchBoxParent"};

    /**
     * $scope.createMap
     *
     * Initialize the map and the marker used for actual position
     */
  	$scope.createMap = function() {
        //initial latlng coordinates belong to Austria (via googleplaces)
	    $scope.map = { control: {}, center: { latitude: 47.516231, longitude: 14.550072 }, zoom: 7 };
	    $scope.map.options = {scrollable:false};

        $scope.marker = {
            id: 0,
            options: {
                draggable: true
            }
        };
  	}

    /**
     * $scope.placeToMarker
     *
     * @description This function is called whenever a new place is entered in the searchbox.
     * Therefor the map position is set to the found place and the coordinates of the marker
     * are also set.
     * @param searchBox input field for search
     */
    $scope.placeToMarker = function(searchBox) {
        var place = searchBox.getPlaces();

        if(!place || place == 'undefined' || place.length == 0) {
            return;
        }

        $scope.map.control.refresh({latitude: place[0].geometry.location.lat(), longitude: place[0].geometry.location.lng()});

        $scope.marker.coords = {latitude: place[0].geometry.location.lat(), longitude: place[0].geometry.location.lng()};
        $scope.marker.address = place[0].formatted_address;

        console.log($scope.marker.coords);

        $scope.map.zoom = 14;
    }
  	
  	
    /**
     * $scope.findNearProjects
     *
     * Sets the search parameters (latitude, longitude and radius) and sends a request to the
     * Project Service to get near projects. Takes the response and creates a project array.
     */
    $scope.findNearProjects = function() {
        //search near projects in radius RADIUS km
        var RADIUS = 100;

        if($scope.marker.coords === undefined) {
            $scope.alertMessage = "Bitte geben Sie zuerst ihren Standort ein!";
        } else {
            $scope.alertMessage = null;

            var params = {
                latitude: $scope.marker.coords.latitude,
                longitude: $scope.marker.coords.longitude,
                radius: RADIUS
            }

            Project.getNearProjects(params, function(projectLocations) {
                $scope.projects = [];

                if(projectLocations.length == 0) {
                    $scope.noProjectsFound = true;
                }

                projectLocations.forEach(function(element) {
                    $scope.projects.push(element.project);
                })

                $scope.searchStarted = true;
            });
        }

        
    }

    /**
     * $scope.geolocate
     *
     * Uses HTML5 to geolocate the user. If successful, sets the markers
     * position to ther right latitude and longitude. Otherwise displays
     * an error message to the user.
     */
    $scope.geolocate = function() {
    	$scope.showAccept = true;

    	if (navigator.geolocation) {
	    	navigator.geolocation.getCurrentPosition(function(position){
	      			$scope.showAccept = false;
	        		$scope.position = position;

	        		var lat = $scope.position.coords.latitude;
	        		var lng = $scope.position.coords.longitude;

					$scope.showLocationOnMap(lat, lng);
					$scope.marker.coords = {latitude: lat, longitude: lng }
	    	}, function(error) {
	    		switch(error.code) {
			        case error.PERMISSION_DENIED:
			            $scope.alertMessage = "nearProjects.error.PERMISSION_DENIED";
			            break;
			        case error.POSITION_UNAVAILABLE:
			            $scope.alertMessage = "nearProjects.error.POSITION_UNAVAILABLE";
			            break;
			        case error.TIMEOUT:
			            $scope.alertMessage = "nearProjects.error.TIMEOUT";
			            break;
			        case error.UNKNOWN_ERROR:
			            $scope.alertMessage = "nearProjects.error.UNKNOWN_ERROR";
			            break;
			    }
	    	});
    	} else {
    		//browser does not support geolocate
    	}
    }

    /**
     * $scope.showLocationOnMap
     * 
     * Centers the map to the specific coordinates
     * @param lat latitude
     * @param lng longitude
     */
    $scope.showLocationOnMap = function(lat, lng) {
    	$scope.map.control.refresh({latitude: lat, longitude: lng});
    	$scope.map.zoom = 15;
    }

    
    /**
     * $scope.redirctToProject
     *
     * Redirect to the project details for the given project
     * @param project the project to be displayed
     */
    $scope.redirectToProject = function(project) {
    	$location.path('/projects/'+project.id);
    }

    /**
     * $scope.enableAddress
     *
     * Sets showSearchBox to true
     */
    $scope.enableAddress = function() {
        $scope.showSearchBox = true;
    }

    //init map and marker
    $scope.createMap();
});
