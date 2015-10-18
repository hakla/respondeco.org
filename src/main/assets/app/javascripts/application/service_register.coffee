RegisterService = ($resource) ->
  $resource 'app/rest/register', {}, {}

angular
  .module 'respondecoApp'
  .factory 'Register', RegisterService
