Run = (stateHelper) ->
  stateHelper
    .controller '/organizations', 'OrganizationsController'
    .controller '/organizations/:id', 'OrganizationController'

angular
  .module 'respondeco.organization'
  .run Run