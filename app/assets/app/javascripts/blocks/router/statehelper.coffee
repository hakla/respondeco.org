###
# The StateHelperConfig is used to save instances of the $urlRouterProvider and the $stateProvider so we can access them after the config phase
###
StateHelperConfig = () ->
  @config =
    $stateProvider: null
    $urlRouterProvider: null

  @$get = () ->
    config: @config

  @

StateHelper = (stateHelperConfig) ->
  # capture this
  service = {}

  $stateProvider = stateHelperConfig.config.$stateProvider
  $urlRouterProvider = stateHelperConfig.config.$urlRouterProvider

  ###
  # public functions
  ###

  service.controller = (state, name) -> _controller(state, name)
  service.raw = (state, o) -> _raw(state, o)
  service.view = (state) -> _view(state)

  ###
  # private functions
  ###

  _controller = (state, name) ->
    o =
      controller: _controllerName(name)
      controllerAs: 'vm'
      templateUrl: _templateUrl(name)

    $stateProvider.state(state, o)

    service

  _raw = (state, o) ->
    $stateProvider.state(state, o)
    
    service

  _view = (state, name) ->
    # create options
    options =
      templateUrl: _templateUrl(name)

    $stateProvider.state(state, options)

    service

  _controllerName = (name) -> "#{name.replace(/^(.)/, (a) => a.toUpperCase())}Controller"
  _templateUrl = (name) -> "assets/partials/#{name.toLowerCase()}.html"

  service

angular
  .module 'blocks.router'
  .provider 'stateHelperConfig', StateHelperConfig
  .factory 'stateHelper', StateHelper