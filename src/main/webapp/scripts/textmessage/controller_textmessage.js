'use strict';

respondecoApp.controller('TextMessageController', function ($scope, $location, resolvedTextMessage, TextMessage) {

        $scope.textMessageToSend = {
            receiver: null,
            content: null
        };
        $scope.viewedTextMessage = null;
        $scope.textMessages = resolvedTextMessage;
        $scope.toDelete = null;


        $scope.create = function () {
            TextMessage.save($scope.textMessageToSend,
                function () {
                    $scope.textmessages = TextMessage.query();
                    $('#saveTextMessageModal').modal('hide');
                    $scope.saveerror = null;
                    $scope.clear();
                },
                function () {
                    $scope.saveerror = "ERROR";
                });
        };

        $scope.delete = function (id) {
            TextMessage.delete({id: id},
                function () {
                    $scope.textMessages = TextMessage.query();
                    $scope.viewedTextMessage = null;
                });
        };

        $scope.reply = function (sender) {
            $scope.textMessageToSend.receiver = sender;
            $('#saveTextMessageModal').modal('show');
        }

        $scope.clear = function () {
            $scope.textMessageToSend = {receiver: null, content: null};
        };

        $scope.viewMessage = function(message) {
            $scope.viewedTextMessage = message;
        }

        $scope.prepareDelete = function(message) {
            $scope.toDelete = message;
            $('#deleteTextMessageModal').modal('show');
        }

        $scope.confirmDelete = function() {
            if($scope.toDelete != null) {
                $scope.delete($scope.toDelete.id);
            }
            $('#deleteTextMessageModal').modal('hide');
        }
    });
