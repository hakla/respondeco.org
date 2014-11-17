/**
 * Created by Clemens Puehringer on 17/11/14.
 */

'use strict';

respondecoApp.factory('UserNames', function ($resource) {
    return $resource('app/rest/users/names', { query: "" }, {
    });
});