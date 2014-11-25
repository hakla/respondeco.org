angular.module('respondecoAppFilters', []).filter('logo', function() {
  return function(input) {
    return input || 'http://lorempixel.com/200/200/city/';
  };
});