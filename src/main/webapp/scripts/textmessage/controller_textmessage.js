'use strict';

respondecoApp.controller('TextMessageController', function ($scope, TextMessage, User, $rootScope, $translate) {

        $scope.textMessageToSend = {
            receiver: null,
            content: ""
        };
        $scope.replyContent = null;
        $scope.viewedTextMessage = null;
        $scope.textMessages = TextMessage.query();
        $scope.toDelete = null;

        $scope.sendsuccess = null;
        $scope.senderror = null;
        $scope.senderrorUserNotFound = null;
        $scope.senderrorReceiverLength = null;
        $scope.senderrorContentLength = null;
        $scope.sendErrorGeneral = null;
        $scope.senderrorMessage = null;

        $scope.deletesuccess = null;
        $scope.deleteerror = null;
        $scope.deleteerrorMessage = null;

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
                    $scope.senderrorMessage = null;
                    $scope.sendsuccess = "SUCCESS";
                },
                function (error) {
                    console.log("start");
                    if($scope.textMessageToSend.receiver != null && $scope.textMessageToSend.content.length > 0) {
                        console.log("404 check");
                        if(error.status == 404) {
                            $scope.senderrorUserNotFound = "ERROR";
                        } else if(error.status == 400) {
                            $scope.senderror = "ERROR";
                            $translate(error.data.key).then(function(translated) {
                                $scope.senderrorMessage = translated;
                            });
                        } else {
                            $scope.senderrorGeneral = "ERROR";
                        }
                    }
                    if($scope.textMessageToSend.receiver.length == 0) {
                        $scope.senderrorReceiverLength = "ERROR";
                    }
                    if($scope.textMessageToSend.content.length == 0) {
                        $scope.senderrorContentLength = "ERROR";
                    }
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
                    $scope.deleteerrorMessage = null;
                    $scope.deletesuccess = "SUCCESS";
                },
                function (error) {
                    $scope.clear();
                    $scope.deleteerror = "ERROR";
                    $scope.deletesuccess = null;
                    if(error.status == 400) {
                        $translate(error.data.key).then(function (translated) {
                            $scope.deleteerrorMessage = translated;
                        });
                    }
                });
        };

        $scope.reply = function (receiver) {
            $scope.textMessageToSend.content = $scope.replyContent;
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
            $scope.senderrorGeneral = null;
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
