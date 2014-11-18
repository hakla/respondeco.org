'use strict';

respondecoApp.factory('ResourceRequirementJoinResourceTag', function ($resource) {
        return $resource('app/rest/resourceRequirementJoinResourceTags/:id', {}, {
            'query': { method: 'GET', isArray: true},
            'get': { method: 'GET'}
        });
    });
