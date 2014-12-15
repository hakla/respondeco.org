'use strict';

respondecoApp.factory('TextMessage', function ($resource) {
        return $resource('app/rest/textmessages/:id', {}, {
                'delete': { method: 'DELETE'},
                'countNewMessages': { method: 'GET', url: 'app/rest/messages/unread' },
                'markRead': { method: 'POST', url: 'app/rest/messages/:id/markread', params: {
                  id: '@id'
                } }
            });
    });
