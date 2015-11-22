SocialMediaRouter = ($routeProvider, $httpProvider, $translateProvider, USER_ROLES) ->
  $routeProvider.when '/social-networks',
    templateUrl: '/views/social-networks.html'
    controller: 'SocialMediaController'
    access: authorizedRoles: [ USER_ROLES.all ]

angular
  .module 'respondecoApp'
  .config SocialMediaRouter
