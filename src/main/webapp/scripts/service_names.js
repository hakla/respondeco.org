/**
 * Created by Clemens Puehringer on 25/11/14.
 */

'use strict';

respondecoApp.factory('UserNames', function ($resource, $cacheFactory) {
    var UserNamesService = $resource('app/rest/names/users', { filter: "" }, { });
    var cache = $cacheFactory("UserNames");

    return {
        getUsernames: function(partialName) {
            var names = cache.get(partialName);
            if(!names) {
                names = UserNamesService.query({ filter: partialName });
                cache.put(partialName, names);
            }
            return names;
        }
    }
});

respondecoApp.factory('PropertyTagNames', function ($resource, $cacheFactory) {
    var PropertyTagNamesService = $resource('app/rest/names/propertytags', { filter: "" }, { });
    var cache = $cacheFactory("PropertyTagNames");

    return {
        getPropertyTagNames: function(partialName) {
            var names = cache.get(partialName);
            if(!names) {
                names = PropertyTagNamesService.query({ filter: partialName });
                cache.put(partialName, names);
            }
            return names;
        }
    }
});

respondecoApp.factory('ProjectNames', function ($resource, $cacheFactory) {
    var ProjectNamesService = $resource('app/rest/names/projects', { filter: "" }, { });
    var cache = $cacheFactory("ProjectNames");

    return {
        getProjectnames: function(partialName) {
            var names = cache.get(partialName);
            if(!names) {
                names = ProjectNamesService.query({ filter: partialName });
                cache.put(partialName, names);
            }
            return names;
        }
    }
});