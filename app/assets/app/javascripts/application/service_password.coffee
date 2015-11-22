PasswordService = ($resource) ->
  $resource 'app/rest/account/change_password', {}, {}

angular
  .module 'respondecoApp'
  .factory 'Password', PasswordService
