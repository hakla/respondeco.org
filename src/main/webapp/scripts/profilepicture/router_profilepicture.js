'use strict';

respondecoApp
    .config(function ($routeProvider, $httpProvider, $translateProvider, USER_ROLES) {
            $routeProvider
                .when('/profilepicture', {
                    templateUrl: 'views/profilepictures.html',
                    controller: 'ProfilePictureController',
                    resolve:{
                        resolvedProfilePicture: ['ProfilePicture', function (ProfilePicture) {
                            return ProfilePicture.query();
                        }],
                        resolvedUser: ['User', function (User) {
                            return User.query();
                        }]
                    },
                    access: {
                        authorizedRoles: [USER_ROLES.all]
                    }
                })
        });
