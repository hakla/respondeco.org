angular.module('respondecoAppFilters', []).filter('logo', function() {
  return function(input) {
    return input || 'http://lorempixel.com/200/200/city/';
  };
}).filter('round', function() {
  return function(input) {
    return (Math.round(input * 10) / 10).toString().replace(".", ",");
  }
}).filter('formatUser', function() {
  return function(user) {
    var name = "";
 
    if (user != null) {
      if (user.lastName != undefined && user.firstName != undefined) {
        name = user.firstName + " " + user.lastName + " (" + user.login + ")";
      } else if (user.name == undefined || user.firstName == undefined) {
        name = user.login;
      }
    }

    return name;
  }
});