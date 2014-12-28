'use strict';

respondecoApp.controller('ProjectLocationController', function ($scope, $location, Project) {

   
    if (navigator.geolocation) {
    	navigator.geolocation.getCurrentPosition(function(position){
      		$scope.$apply(function(){
        		$scope.position = position;
        		console.log($scope.position);

        		$scope.staticMap = $scope.createStaticMapLink();    	
        	});
    });
  }




 	$scope.createStaticMapLink = function() {
    	var lat = $scope.position.coords.latitude;
    	var lng = $scope.position.coords.longitude;
    	var zoom = 14;

    	var link = "https://maps.google.com/maps/api/staticmap?center=" + lat + "%2C" +
    	+ lng +"&format=jpg&maptype=terrain&size=533x190&zoom=" + zoom + "&markers=" + lat + "%2C" +
    	lng;

    	return link;
    }

   /**
    * 
    */
    $scope.findProjectsNearMe = function() {

    	

    }
});
