/**
 * Created by Clemens Puehringer on 10/11/14.
 */
'use strict';

describe('TextMessage Controller Tests ', function () {
    beforeEach(module('respondecoApp'));

    describe('TextMessageController', function () {
        var $scope, TextMessageService;

        beforeEach(inject(function ($rootScope, $controller, TextMessage) {
            $scope = $rootScope.$new();
            TextMessageService = TextMessage;
            $controller('TextMessageController', {$scope: $scope, TextMessage: TextMessageService});
        }));

        it('should set senderror if message could not be sent', function () {
            $scope.textMessageToSend.receiver = "any";
            $scope.textMessageToSend.content = "any";

            spyOn(TextMessageService, "save");

            $scope.create();

            expect(TextMessageService.save).toHaveBeenCalled();
            expect(TextMessageService.save).toHaveBeenCalledWith(
                {receiver: "any", content: "any"},
                jasmine.any(Function),
                jasmine.any(Function));

            //Simulate call to error callback
            TextMessageService.save.calls.mostRecent().args[2]({
                data: {
                    error: "Some error message"
                }
            });
            expect($scope.senderror).toBe("ERROR");
            expect($scope.sendsuccess).toBeNull();
        });

        it('should set sendsuccess if message was sent successfully', function () {
            $scope.textMessageToSend.receiver = "any";
            $scope.textMessageToSend.content = "any";

            //disable call to dom
            $scope.hideNewMessageModal = function() {};

            spyOn(TextMessageService, "save");

            $scope.create();

            expect(TextMessageService.save).toHaveBeenCalled();
            expect(TextMessageService.save).toHaveBeenCalledWith(
                {receiver: "any", content: "any"},
                jasmine.any(Function),
                jasmine.any(Function));

            //Simulate call to success callback
            TextMessageService.save.calls.mostRecent().args[1]();
            expect($scope.senderror).toBeNull();
            expect($scope.sendsuccess).toBe("SUCCESS");
        });

        it('should set deleteerror if message could not be deleted', function () {

            spyOn(TextMessageService, "delete");

            $scope.delete({ id: 1 });

            expect(TextMessageService.delete).toHaveBeenCalled();
            expect(TextMessageService.delete).toHaveBeenCalledWith(
                { id: 1 },
                jasmine.any(Function),
                jasmine.any(Function));

            //Simulate call to error callback
            TextMessageService.delete.calls.mostRecent().args[2]({
                data: {
                    error: "Some error message"
                }
            });
            expect($scope.deleteerror).toBe("ERROR");
            expect($scope.deletesuccess).toBeNull();
        });

        it('should set deletesuccess if message was deleted successfully', function () {

            spyOn(TextMessageService, "delete");

            $scope.delete({ id: 1 });

            expect(TextMessageService.delete).toHaveBeenCalled();
            expect(TextMessageService.delete).toHaveBeenCalledWith(
                { id: 1 },
                jasmine.any(Function),
                jasmine.any(Function));

            //Simulate call to error callback
            TextMessageService.delete.calls.mostRecent().args[1]();
            expect($scope.deleteerror).toBeNull();
            expect($scope.deletesuccess).toBe("SUCCESS");
        });

        it('should reload messages if message was deleted successfully', function () {

            spyOn(TextMessageService, "delete");
            spyOn(TextMessageService, "query");

            $scope.delete(1);

            //Simulate call to error callback
            TextMessageService.delete.calls.mostRecent().args[1]();
            expect(TextMessageService.query).toHaveBeenCalled();
        });

    });

});