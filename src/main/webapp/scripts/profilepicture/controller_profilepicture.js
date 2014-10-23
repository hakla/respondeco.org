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

        $scope.update = function (id) {
            $scope.profilepicture = ProfilePicture.get({id: id});
            $('#saveProfilePictureModal').modal('show');
        };

        $scope.delete = function (id) {
            ProfilePicture.delete({id: id},
                function () {
                    $scope.profilepictures = ProfilePicture.query();
                });
        };

        $scope.clear = function () {
            $scope.profilepicture = {userlogin: null, label: null, data: null, id: null};
        };
    });
