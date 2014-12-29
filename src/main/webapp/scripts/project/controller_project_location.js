'use strict';

respondecoApp.controller('ProjectLocationController', function ($scope, $location, Project) {

  	$scope.projects = [];

  	$scope.showMap = false;
  	$scope.noProjectsFound = false;
  	$scope.searchStarted = false;
  	
  	$scope.createMap = function() {
	  	//google maps
	    $scope.location = {searchbox: null};

	    $scope.map = { control: {}, center: { latitude: null, longitude: null }, zoom: 12 };
	    $scope.map.options = {scrollable:false};

	     $scope.marker = {
	      id: 0,
	      coords: {
	        latitude: 47.453368,
	        longitude: 16.415000
	      },
	      options: { draggable: true },
	      events: {
	        dragend: function (marker, eventName, args) {
	            console.log(marker);
	          var lat = marker.getPosition().lat();
	          var lng = marker.getPosition().lng();

	          $scope.marker.options = {
	            draggable: true,
	            labelContent: "lat: " + $scope.marker.coords.latitude + ' ' + 'lng: ' + $scope.marker.coords.longitude,
	            labelAnchor: "100 0",
	            labelClass: "marker-labels"
	          };
	        }
	      }
    };

    $scope.placeToMarker = function(searchBox, id) {
        var place = searchBox.getPlaces();
        console.log(place);
        if(!place || place == 'undefined' || place.length == 0) {
            return;
        }

        $scope.map.control.refresh({latitude: place[0].geometry.location.lat(), longitude: place[0].geometry.location.lng()});

        $scope.marker.coords = {latitude: place[0].geometry.location.lat(), longitude: place[0].geometry.location.lng()};
        $scope.marker.address = place[0].formatted_address;

        console.log($scope.marker);
    }
   
   
  	var events = {
        places_changed: function (searchBox) {
            var id = 0;
            console.log("places changed");
            $scope.placeToMarker(searchBox, id);
        }
    }

    $scope.searchBox = { template:'searchBox.template.html', events:events, parentdiv: "searchBoxParent"};
  	}
  	
  	
   /**
    * 
    */
    $scope.findNearProjects = function() {
    	var params = {
    		latitude: $scope.marker.coords.latitude,
    		longitude: $scope.marker.coords.longitude,
    		radius: 200
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

    $scope.geolocate = function() {
    	$scope.showAccept = true;

    	if (navigator.geolocation) {
	    	navigator.geolocation.getCurrentPosition(function(position){
	      		$scope.$apply(function(){
	      			$scope.showAccept = false;
	        		$scope.position = position;

	        		var lat = $scope.position.coords.latitude;
	        		var lng = $scope.position.coords.longitude;

					$scope.showLocationOnMap(lat, $scope.position.coords.longitude);
					$scope.marker.coords = {latitude: lat, longitude: lng }
	        		$scope.showMap = true;
	        	});
	    	}, function(error) {
	    		switch(error.code) {
			        case error.PERMISSION_DENIED:
			            $scope.alertMessage = "User denied the request for Geolocation."
			            break;
			        case error.POSITION_UNAVAILABLE:
			            $scope.alertMessage = "Location information is unavailable."
			            break;
			        case error.TIMEOUT:
			            $scope.alertMessage = "The request to get user location timed out."
			            break;
			        case error.UNKNOWN_ERROR:
			            $scope.alertMessage = "An unknown error occurred."
			            break;
			    }
	    	});
    	} else {
    		//browser does not support geolocate
    	}
    }

    $scope.showLocationOnMap = function(lat, lng) {
    	console.log(lat);
    	console.log(lng);
    	$scope.map.control.refresh({latitude: lat, longitude: lng});
    	$scope.map.zoom = 15;
    }

    $scope.createMap();

    $scope.redirectToProject = function(project) {
    	$location.path('/projects/'+project.id);
    }

});
