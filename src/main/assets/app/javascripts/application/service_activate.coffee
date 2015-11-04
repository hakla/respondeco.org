ActivateService = ($resource) ->
  $resource 'app/rest/activate', {}, 'get':
    method: 'GET'
    params: {}
    isArray: false

angular
  .module 'respondecoApp'
  .factory 'Activate', ActivateService
