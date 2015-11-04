respondecoApp.factory 'TextMessage', ($resource) ->
  $resource 'app/rest/textmessages/:id', {},
    'delete':
      method: 'DELETE'
    'countNewMessages':
      method: 'GET'
      url: 'app/rest/messages/unread'
      ignoreAuthModule: true
    'markRead':
      method: 'POST'
      url: 'app/rest/messages/:id/markread'
      params: id: '@id'