Config = (stateHelperConfigProvider, $stateProvider, $urlRouterProvider) ->
  stateHelperConfigProvider.config.$stateProvider = $stateProvider
  stateHelperConfigProvider.config.$urlRouterProvider = $urlRouterProvider

angular
  .module 'respondeco.core'
  .config Config