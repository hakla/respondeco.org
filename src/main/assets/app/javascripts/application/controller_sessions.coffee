SessionsController = ($scope, resolvedSessions, Sessions) ->
  $scope.success = null
  $scope.error = null
  $scope.sessions = resolvedSessions

  $scope.invalidate = (series) ->
    Sessions.delete { series: encodeURIComponent(series) }, ((value, responseHeaders) ->
      $scope.error = null
      $scope.success = 'OK'
      $scope.sessions = Sessions.get()
    ), (httpResponse) ->
      $scope.success = null
      $scope.error = 'ERROR'

angular
  .module 'respondecoApp'
  .controller 'SessionsController', SessionsController
