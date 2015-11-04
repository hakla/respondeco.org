ActivationController = ($scope, $routeParams, $location, Activate) ->
  Activate.get { key: $routeParams.key }, ((value, responseHeaders) ->
    $scope.error = null
    $scope.success = 'OK'

    $location.path "organization/#{value.id}"
  ), (httpResponse) ->
    $scope.success = null
    $scope.error = 'ERROR'

angular
  .module 'respondecoApp'
  .controller 'ActivationController', ActivationController
