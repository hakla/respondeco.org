'use strict';

respondecoApp
    .config(function ($routeProvider, $httpProvider, $translateProvider, USER_ROLES) {
        $routeProvider
            .when('/textmessages', {
                templateUrl: 'views/textmessages.html',
                controller: 'TextMessageController',
                resolve:{
                    resolvedTextMessage: ['TextMessage', function (TextMessage) {
                        return TextMessage.query();
                    }]
                },
                access: {
                    authorizedRoles: [USER_ROLES.user, USER_ROLES.admin]
                }
            })
        });

respondecoApp
    .config(function ($routeProvider, $httpProvider, $translateProvider, USER_ROLES) {
        $routeProvider
            .when('/textmessage', {
                templateUrl: 'views/textmessage.html',
                controller: 'TextMessageController',
                resolve:{
                    resolvedTextMessage: ['TextMessage', function (TextMessage) {
                        return TextMessage.query();
                    }]
                },
                access: {
                    authorizedRoles: [USER_ROLES.user, USER_ROLES.admin]
                }
            })
    });
