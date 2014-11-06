'use strict';

respondecoApp.controller('TextMessageController', function ($scope, $location, resolvedTextMessage, TextMessage) {

        $scope.textMessage = {
            receiver: null,
            content: null
        };
        $scope.textMessages = resolvedTextMessage;
        $scope.cachedTextMessage = TextMessage.retrieveTextMessage();

        $scope.create = function () {
            TextMessage.save($scope.textMessage,
                function () {
                    $scope.textmessages = TextMessage.query();
                    $('#saveTextMessageModal').modal('hide');
                    $scope.clear();
                },
                function () {
                    $scope.saveerror = "ERROR";
                });
        };

        $scope.delete = function (id) {
            TextMessage.delete({id: id},
                function () {
                    if($location.path() == "/textmessages") {
                        $scope.textMessages = TextMessage.query();
                    } else {
                        //path is /textmessage, switch back to /textmessages
                        $location.path("/textmessages");
                    }
                });
        };

        $scope.reply = function (sender) {
            $scope.textMessage.receiver = sender;
            $('#saveTextMessageModal').modal('show');
        }

        $scope.clear = function () {
            $scope.textMessage = {receiver: null, content: null};
        };

        $scope.viewMessage = function(message) {
            TextMessage.cacheTextMessage(message);
            $location.path("/textmessage");
        }
    });
