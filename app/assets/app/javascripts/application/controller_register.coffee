RegisterController = ($scope, $translate, Register, $location, $routeParams) ->
  $scope.activation = false
  $scope.success = null
  $scope.error = null
  $scope.doNotMatch = null
  $scope.errorUserExists = null
  $scope.registerAccount =
    orgname: null
    npo: null
    email: null
    password: null
    langKey: 'de'
  if $location.path() == '/activateInvitation'
    $scope.activation = true
    $scope.registerAccount.email = $routeParams.email

  $scope.register = ->
    if $scope.registerAccount.password != $scope.confirmPassword
      $scope.doNotMatch = 'ERROR'
    else
      $scope.registerAccount.langKey = $translate.use()
      $scope.doNotMatch = null
      Register.save $scope.registerAccount, ((value, responseHeaders) ->
        $scope.error = null
        $scope.errorUserExists = null
        $scope.success = 'OK'
      ), (httpResponse) ->
        $scope.success = null
        if httpResponse.status == 304 and httpResponse.data.error and httpResponse.data.error == 'Not Modified'
          $scope.error = null
          $scope.errorUserExists = 'ERROR'
        else
          $scope.error = 'ERROR'
          $scope.errorUserExists = null

angular
  .module 'respondecoApp'
  .controller 'RegisterController', RegisterController
