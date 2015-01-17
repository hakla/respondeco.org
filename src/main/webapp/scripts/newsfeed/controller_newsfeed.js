'use strict';

respondecoApp.controller('NewsfeedController', function($scope, $location, $routeParams, Account) {

    $scope.newsPage = -1;
    $scope.newsPageSize = 20;
    $scope.newsTotal = null;
    $scope.news = [];

    var refreshNews = function() {
        Account.getNewsfeed({
                page: $scope.newsPage,
                pageSize: $scope.newsPageSize },
            function(data) {
                $scope.newsTotal = data.totalElements;
                $scope.news = $scope.news.concat(data.postings);
            });
    };

    $scope.canShowMoreNews = function() {
        return $scope.news.length < $scope.newsTotal;
    };

    $scope.showMoreNews = function() {
        $scope.newsPage = $scope.newsPage + 1;
        refreshNews();
    };

    $scope.showMoreNews();

});
