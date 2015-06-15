(function() {
  angular.module('respondecoAppFilters', []).filter('logo', function() {
    return function(input) {
      return input || 'http://lorempixel.com/200/200/city/';
    };
  }).filter('round', function() {
    return function(input) {
      return (Math.round(input * 10) / 10).toString().replace('.', ',');
    };
  }).filter('formatUser', function() {
    return function(user) {
      var name;
      name = '';
      if (user !== null) {
        if (user.lastName !== void 0 && user.firstName !== void 0) {
          name = user.firstName + ' ' + user.lastName + ' (' + user.login + ')';
        } else if (user.name === void 0 || user.firstName === void 0) {
          name = user.login;
        }
      }
      return name;
    };
  }).filter('linkify', function() {
    return function(link) {
      return "<a href='" + link + "'>" + link + "</a>";
    };
  });

}).call(this);

//# sourceMappingURL=filters.js.map
