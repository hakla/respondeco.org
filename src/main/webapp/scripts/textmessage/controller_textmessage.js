'use strict';

respondecoApp.controller('TextMessageController', function ($scope, TextMessage, UserNames) {

        $scope.textMessageToSend = {
            receiver: "",
            content: ""
        };
        $scope.viewedTextMessage = null;
        $scope.textMessages = TextMessage.query();
        $scope.toDelete = null;

        $scope.sendsuccess = null;
        $scope.senderror = null;
        $scope.senderrorUserNotFound = null;
        $scope.senderrorReceiverLength = null;
        $scope.senderrorContentLength = null;
        $scope.senderrorMsg = null;

        $scope.deletesuccess = null;
        $scope.deleteerror = null;
        $scope.deleteerrorMsg = null;

        $scope.getUsernames = function(partialName) {
            return UserNames.getUsernames(partialName).$promise.then(
                function(response) {
                    return response;
                }
            );
        }

        $scope.create = function () {
            TextMessage.save($scope.textMessageToSend,
                function () {
                    $scope.hideNewMessageModal();
                    $scope.clear();
                    $scope.senderror = null;
                    $scope.sendsuccess = "SUCCESS";
                },
                function (error) {
                    if($scope.textMessageToSend.receiver.length > 0 && $scope.textMessageToSend.content.length > 0) {
                        if(error.status == 400) {
                            $scope.senderrorUserNotFound = "ERROR";
                        } else {
                            $scope.senderror = "ERROR";
                        }
                    }
                    if($scope.textMessageToSend.receiver.length == 0) {
                        $scope.senderrorReceiverLength = "ERROR";
                    }
                    if($scope.textMessageToSend.content.length == 0) {
                        $scope.senderrorContentLength = "ERROR";
                    }
                    $scope.senderrorMsg = error.data.error;
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
            $scope.senderrorUserNotFound = null;
            $scope.senderrorReceiverLength = null;
            $scope.senderrorContentLength = null;
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
