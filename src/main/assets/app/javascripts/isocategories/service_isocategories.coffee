IsoCategories = ($resource) ->
  $resource 'app/rest/isocategories/:id', {},
    query:
      isArray: false

angular
  .module 'respondecoApp'
  .factory 'IsoCategories', IsoCategories
