'use strict';

respondecoApp.controller('ProfilePictureController', function ($scope, resolvedProfilePicture, ProfilePicture, resolvedUser) {

        $scope.profilepictures = resolvedProfilePicture;
        $scope.users = resolvedUser;

        $scope.create = function () {
            ProfilePicture.save($scope.profilepicture,
                function () {
                    $scope.profilepictures = ProfilePicture.query();
                    $('#saveProfilePictureModal').modal('hide');
                    $scope.clear();
                });
        };

        $scope.update = function (userlogin) {
            $scope.profilepicture = ProfilePicture.get({userlogin: userlogin});
            $('#saveProfilePictureModal').modal('show');
        };

        $scope.delete = function (userlogin) {
            ProfilePicture.delete({userlogin: userlogin},
                function () {
                    $scope.profilepictures = ProfilePicture.query();
                });
        };

        $scope.clear = function () {
            $scope.profilepicture = {userlogin: null, label: null, data: null};
        };
    });
