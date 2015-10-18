###global angular:true, browser:true ###

###
# @license HTTP Auth Interceptor Module for AngularJS
# (c) 2012 Witold Szczerba
# License: MIT
###

do ->
  'use strict'
  angular.module('http-auth-interceptor', [ 'http-auth-interceptor-buffer' ]).factory('authService', ($rootScope, httpBuffer) ->
    {
    loginConfirmed: (data, configUpdater) ->
      updater = configUpdater or (config) ->
        config
      $rootScope.$broadcast 'event:auth-loginConfirmed', data
      httpBuffer.retryAll updater

    loginCancelled: (data, reason) ->
      httpBuffer.rejectAll reason
      $rootScope.$broadcast 'event:auth-loginCancelled', data
    }
  ).config ($httpProvider) ->
    interceptor = [
      '$rootScope'
      '$q'
      'httpBuffer'
      '$translate'
      ($rootScope, $q, httpBuffer, $translate) ->

        success = (response) ->
          response

        error = (response) ->
          if response.status == 401 and !response.config.ignoreAuthModule
            deferred = $q.defer()
            httpBuffer.append response.config, deferred
            $rootScope.$broadcast 'event:auth-loginRequired', response
            return deferred.promise
          else if response.status == 403 and !response.config.ignoreAuthModule
            $rootScope.$broadcast 'event:auth-notAuthorized', response
          else if response.status == 400 and response.data.key != undefined and response.data.message != undefined
            message = $translate.instant(response.data.key)
            console.log response.data.message
            $rootScope.globalAlerts.push
              type: 'danger'
              msg: message
          # otherwise, default behaviour
          $q.reject response

        (promise) ->
          promise.then success, error
    ]

    $httpProvider.responseInterceptors.push interceptor

  ###*
  # Private module, a utility, required internally by 'http-auth-interceptor'.
  ###

  angular.module('http-auth-interceptor-buffer', []).factory 'httpBuffer', ($injector) ->

    ###* Holds all the requests, so they can be re-requested in future. ###

    buffer = []

    ###* Service initialized later because of circular dependency problem. ###

    $http = undefined

    retryHttpRequest = (config, deferred) ->

      successCallback = (response) ->
        deferred.resolve response
        return

      errorCallback = (response) ->
        deferred.reject response
        return

      $http = $http or $injector.get('$http')
      $http(config).then successCallback, errorCallback
      return

    {
      append: (config, deferred) ->
        buffer.push
          config: config
          deferred: deferred
        return
      rejectAll: (reason) ->
        if reason
          i = 0
          while i < buffer.length
            buffer[i].deferred.reject reason
            ++i
        buffer = []
        return
      retryAll: (updater) ->
        i = 0
        while i < buffer.length
          retryHttpRequest updater(buffer[i].config), buffer[i].deferred
          ++i

        buffer = []
    }
