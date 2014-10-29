'use strict';

respondecoApp.factory('ProfilePicture', function ($resource) {
        return $resource('app/rest/profilepictures/:userlogin', {}, {
            'query': { method: 'GET', isArray: true},
            'get': { method: 'GET'}
        });
    });
