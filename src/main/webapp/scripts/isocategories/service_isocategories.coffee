'use strict'
respondecoApp.factory 'IsoCategories', ($resource) ->
  $resource 'app/rest/isocategories/:id', {},
    query:
      isArray: false