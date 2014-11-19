/**
 * Created by Clemens Puehringer on 17/11/14.
 */

'use strict';

respondecoApp.factory('UserNames', function ($resource, $cacheFactory) {
    var UserNamesService = $resource('app/rest/users/names', { query: "" }, { });
    var cache = $cacheFactory("UserNames");

    return {
        getUsernames: function(partialName) {
            var names = cache.get(partialName);
            if(!names) {
                names = UserNamesService.query({ query: partialName });
                cache.put(partialName, names);
            }
            return names;
        }
    }
});