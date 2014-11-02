'use strict';

respondecoApp.controller('TextMessageController', function ($scope, resolvedTextMessage, TextMessage) {

        $scope.textmessages = resolvedTextMessage;

        $scope.create = function () {
            TextMessage.save($scope.textmessage,
                function () {
                    $scope.textmessages = TextMessage.query();
                    $('#saveTextMessageModal').modal('hide');
                    $scope.clear();
                });
        };

        $scope.update = function (id) {
            $scope.textmessage = TextMessage.get({id: id});
            $('#saveTextMessageModal').modal('show');
        };

        $scope.delete = function (id) {
            TextMessage.delete({id: id},
                function () {
                    $scope.textmessages = TextMessage.query();
                });
        };

        $scope.clear = function () {
            $scope.textmessage = {sender: null, receiver: null, timestamp: null, content: null, id: null};
        };
    });
