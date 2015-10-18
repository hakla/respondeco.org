SessionsService = ($resource) ->
  $resource 'app/rest/account/sessions/:series', {}, 'get':
    method: 'GET'
    isArray: true

angular
  .module 'respondecoApp'
  .factory 'Sessions', SessionsService
