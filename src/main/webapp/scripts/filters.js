angular.module('respondecoAppFilters', []).filter('logo', function() {
  return function(input) {
    return input || 'http://lorempixel.com/200/200/city/';
  };
}).filter('round', function() {
  return function(input) {
    return (Math.round(input * 10) / 10).toString().replace(".", ",");
  }
});