'use strict';

describe('Newsfeed Controller Tests ', function () {
    beforeEach(module('respondecoApp'));

    describe('NewsfeedController', function () {
        var $scope, AccountService, location;

        beforeEach(inject(function($rootScope, $controller, $location, $routeParams, Account) {
            $scope = $rootScope.$new();
            location = $location;
            AccountService = Account;

            $controller('NewsfeedController', {$scope: $scope, $location: location, $routeParams: $routeParams,
                Account: AccountService});
        }));

        it('should show more news', function() {
            spyOn(AccountService, 'getNewsfeed');

            $scope.newsPage = 0;

            $scope.showMoreNews();

            expect($scope.newsPage).toEqual(1);

            expect(AccountService.getNewsfeed).toHaveBeenCalledWith({page: 1, pageSize: 20}, jasmine.any(Function));
            AccountService.getNewsfeed.calls.mostRecent().args[1]({totalElements: 15, postings: [{msg: 'hallo'}, {msg: 'test'}]});

            expect($scope.newsTotal).toEqual(15);
            expect($scope.news).toEqual([{msg: 'hallo'}, {msg: 'test'}]);
        });

        it('should say if it can show more news or not', function() {
            $scope.news.length = 10;
            $scope.newsTotal = 20;

            var canShowMore = $scope.canShowMoreNews();

            expect(canShowMore).toBe(true);
        });
    });
});
