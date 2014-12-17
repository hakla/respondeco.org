'use strict';

respondecoApp.controller('TextMessageController', function ($scope, TextMessage, User, $rootScope) {

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
        $scope.senderrorReceiverCurrentUser = null;
        $scope.senderrorMsg = null;

        $scope.deletesuccess = null;
        $scope.deleteerror = null;
        $scope.deleteerrorMsg = null;

        $scope.getUsernames = function(partialName) {
            return User.getByName({filter: partialName, fields: "id,login", order: "+login"}).$promise.then(
                function(response) {
                    return response;
                }
            );
        }

        $scope.refreshTextMessages = function() {
            $scope.textMessages = TextMessage.query();
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
                        if(error.status == 404) {
                            $scope.senderrorUserNotFound = "ERROR";
                        } else if(error.status == 400) {
                            $scope.senderrorReceiverCurrentUser = "ERROR";
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

        $scope.reply = function (receiver) {
            $scope.textMessageToSend.receiver = receiver;
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
            $scope.senderrorReceiverCurrentUser = null;
            $scope.deletesuccess = null;
            $scope.deleteerror = null;
            $scope.sendform.$setPristine();
            $scope.replyform.$setPristine();
        };

        $scope.viewMessage = function(message) {
            // mark the selected message as read
            TextMessage.markRead({
                id: message.id
            }, function() {
                // reload the messages and the amount of new messages
                TextMessage.countNewMessages(function(amount) {
                    $rootScope.newMessages = amount[0];
                });

                $scope.refreshTextMessages();
            });

            $scope.replyform.$setPristine();
            $scope.viewedTextMessage = message;
        }

        $scope.showNewMessageModal = function() {
            $('#saveTextMessageModal').modal('show');
        }

        $scope.hideNewMessageModal = function() {
            $('#saveTextMessageModal').modal('hide');
        }

    });
