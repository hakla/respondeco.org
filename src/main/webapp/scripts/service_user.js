/**
 * Created by Clemens Puehringer on 16/12/14.
 */
'use strict'

respondecoApp.factory('User', function($resource) {
    return $resource('app/rest/users/:id', {}, {
        'get': { method: 'GET'},
        'getByName': { method: 'GET', isArray: true, url: "app/rest/users" }
    });
});
