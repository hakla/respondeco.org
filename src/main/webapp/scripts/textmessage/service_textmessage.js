'use strict';

respondecoApp.factory('TextMessage', function ($resource) {
        var textMessageResource = $resource('app/rest/textmessages/:id', {}, {
                'delete': { method: 'DELETE'}
            });

        textMessageResource.cacheTextMessage = function(textMessage) {
            this.cachedTextMessage = textMessage;
        }

        textMessageResource.retrieveTextMessage = function() {
            var textMessage = this.cachedTextMessage;
            this.cachedTextMessage = null;
            return textMessage;
        }

        return textMessageResource;
    });
