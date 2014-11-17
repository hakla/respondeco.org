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

        $scope.delete = function (id) {
            TextMessage.delete({id: id},
                function () {
                    $scope.clear();
                    $scope.textMessages = TextMessage.query();
                    $scope.viewedTextMessage = null;
                    $scope.deleteerror = null;
                    $scope.deletesuccess = "SUCCESS";
                },
                function () {
                    $scope.clear();
                    $scope.deleteerror = "ERROR";
                    $scope.deletesuccess = null;
                });
        };

        $scope.reply = function (sender) {
            $scope.textMessageToSend.receiver = sender;
            $scope.showNewMessageModal();
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

        $scope.prepareDelete = function(message) {
            $scope.toDelete = message;
            $scope.showDeleteMessageModal();
        }

        $scope.confirmDelete = function() {
            if($scope.toDelete != null) {
                $scope.delete($scope.toDelete.id);
            }
            $scope.hideDeleteMessageModal();
        }

        $scope.showNewMessageModal = function() {
            $('#saveTextMessageModal').modal('show');
        }

        $scope.hideNewMessageModal = function() {
            $('#saveTextMessageModal').modal('hide');
        }

        $scope.showDeleteMessageModal = function() {
            $('#deleteTextMessageModal').modal('show');
        }

        $scope.hideDeleteMessageModal = function() {
            $('#deleteTextMessageModal').modal('hide');
        }
    });
