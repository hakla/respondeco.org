'use strict';

respondecoApp.controller('TextMessageController', function ($scope, TextMessage, UserNames) {

        $scope.textMessageToSend = {
            receiver: null,
            content: null
        };
        $scope.viewedTextMessage = null;
        $scope.textMessages = TextMessage.query();
        $scope.toDelete = null;

        $scope.sendsuccess = null;
        $scope.senderror = null;

        $scope.deletesuccess = null;
        $scope.deleteerror = null;

        $scope.usernames = ["aaaaaa", "baaaa", "aabbbbb"];

        $scope.create = function () {
            TextMessage.save($scope.textMessageToSend,
                function () {
                    $scope.hideNewMessageModal();
                    $scope.clear();
                    $scope.senderror = null;
                    $scope.sendsuccess = "SUCCESS";
                },
                function () {
                    $scope.senderror = "ERROR";
                    $scope.sendsuccess = null;
                });
        };

        $scope.delete = function (message) {
            TextMessage.delete({id: message.id},
                function () {
                    $scope.clear();
                    $scope.textMessages = TextMessage.query();
                    $scope.viewedTextMessage = null;
                    $scope.deleteerror = null;
                    $scope.deletesuccess = "SUCCESS";
                },
                function (error) {
                    $scope.clear();
                    $scope.deleteerror = "ERROR";
                    $scope.deleteerrorMsg = error.data.error;
                    $scope.deletesuccess = null;
                });
        };

        $scope.reply = function (sender) {
            $scope.textMessageToSend.receiver = sender;
            $scope.create();
        }

        $scope.clear = function () {
            $scope.textMessageToSend = {receiver: null, content: null};
            $scope.toDelete = null;
            $scope.sendsuccess = null;
            $scope.senderror = null;
            $scope.deletesuccess = null;
            $scope.deleteerror = null;
        };

        $scope.viewMessage = function(message) {
            $scope.viewedTextMessage = message;
        }

        $scope.showNewMessageModal = function() {
            $('#saveTextMessageModal').modal('show');
        }

        $scope.hideNewMessageModal = function() {
            $('#saveTextMessageModal').modal('hide');
        }

    });
