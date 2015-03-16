'use strict';

/* App Module */

var respondecoApp = angular.module('respondecoApp', ['http-auth-interceptor', 'tmh.dynamicLocale',
    'ngResource', 'ngRoute', 'ngCookies', 'pascalprecht.translate', 'truncate']);

respondecoApp
    .config(function ($routeProvider, $httpProvider, $translateProvider, tmhDynamicLocaleProvider) {
      $routeProvider.otherwise({
          controller: 'MainController'
      });
    });

respondecoApp.factory('Project', function($resource, $http) {
    return $resource('app/rest/projects/:id', {}, {
      query: {
        isArray: true,
        method: 'GET',
        transformResponse: function(data) {
          return JSON.parse(data).projects;
        }
      }
    });
});


respondecoApp.controller('MainController', function($scope, $location, $rootScope, Project) {
  $scope.projects = Project.query(function() {
    setTimeout(function() {
      App.cubeportfolio();
    });
  });
});