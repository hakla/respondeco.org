(function() {
  'use strict';
  respondecoApp.factory('IsoCategories', function($resource) {
    return $resource('app/rest/isocategories/:id', {}, {
      query: {
        isArray: false
      }
    });
  });

}).call(this);

//# sourceMappingURL=service_isocategories.js.map
