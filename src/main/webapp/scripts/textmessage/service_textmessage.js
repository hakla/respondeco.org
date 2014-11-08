'use strict';

respondecoApp.factory('TextMessage', function ($resource) {
        return $resource('app/rest/textmessages/:id', {}, {
                'delete': { method: 'DELETE'}
            });
    });
