LoginController = ($scope, $location, AuthenticationSharedService) ->
  $scope.rememberMe = true

  $scope.login = ->
    AuthenticationSharedService.login
      username: $scope.username
      password: $scope.password
      rememberMe: $scope.rememberMe

  $scope.$on ''

angular
  .module 'respondecoApp'
  .controller 'LoginController', LoginController

