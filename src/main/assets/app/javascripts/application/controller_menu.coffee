MenuController = ($rootScope, TextMessage, Session) ->

  refresh = ->
    if Session.valid()
      TextMessage.countNewMessages (amount) ->
        $rootScope.newMessages = amount[0]

  refresh()
  # refresh the messages on each route change
  $rootScope.$on '$routeChangeStart', (event, next) ->
    refresh()

angular
  .module 'respondecoApp'
  .controller 'MenuController', MenuController

