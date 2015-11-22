LogoutController = ($location, AuthenticationSharedService) ->
  AuthenticationSharedService.logout()

angular
  .module 'respondecoApp'
  .controller 'LogoutController', LogoutController
