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
    return function(user, length) {
      var name;
      length = length || 'long';
      name = '';
      if (user !== null && user !== void 0) {
        if (length === 'long') {
          if (user.lastName !== void 0 && user.firstName !== void 0) {
            name = user.firstName + ' ' + user.lastName + ' (' + user.login + ')';
          }
        } else if (length === 'login') {
          name = user.login;
        } else if (length === 'short') {
          name = user.firstName + " " + user.lastName;
        } else if (length === 'first') {
          name = user.firstName;
        } else if (length === 'last') {
          name = user.lastName;
        }
      }
      return name;
    };
  }).filter('userImage', function() {
    return function(user) {
      if ((user != null ? user.profilePicture : void 0) !== null && (user != null ? user.profilePicture : void 0) !== void 0) {
        return "/app/rest/images/file/" + user.profilePicture.id;
      } else {
        return '/images/profile_empty.png';
      }
    };
  }).filter('linkify', function() {
    return function(link) {
      return "<a href='" + link + "'>" + link + "</a>";
    };
  }).filter('default', function() {
    return function(input, __default__) {
      if (input === null || input === void 0) {
        return __default__;
      } else {
        return input;
      }
    };
  });

}).call(this);

//# sourceMappingURL=filters.js.map
