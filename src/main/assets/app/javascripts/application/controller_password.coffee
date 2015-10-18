PasswordController = ($scope, Password) ->
  $scope.success = null
  $scope.error = null
  $scope.doNotMatch = null

  $scope.changePassword = ->
    if $scope.password != $scope.confirmPassword
      $scope.doNotMatch = 'ERROR'
    else
      $scope.doNotMatch = null
      Password.save $scope.password, ((value, responseHeaders) ->
        $scope.error = null
        $scope.success = 'OK'
      ), (httpResponse) ->
        $scope.success = null
        $scope.error = 'ERROR'

angular
  .module 'respondecoApp'
  .controller 'PasswordController', PasswordController
