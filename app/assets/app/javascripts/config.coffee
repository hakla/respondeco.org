Config = ($stateProvider, $urlRouterProvider) ->
  $urlRouterProvider.otherwise '/'

  partial = (name) ->
    templateUrl: "assets/partials/#{name}.html"

  $stateProvider
    .state 'home',
      url: '/'
      views:
        "": partial 'home'

angular
.module 'respondeco'
.config Config
