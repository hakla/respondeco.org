'use strict';

/* App Module */

var respondecoApp = angular.module('respondecoApp', ['http-auth-interceptor', 'tmh.dynamicLocale',
    'ngResource', 'ngRoute', 'ngCookies', 'pascalprecht.translate', 'truncate', 'ui.bootstrap']);

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
          return JSON.parse(data).projects.map(function(item, index) {
            item.className = (index + 1) % 3 === 0 ? "resource" : "project";

            return item;
          });
        }
      }
    });
});

respondecoApp.factory('Organization', function($resource, $http) {
    return $resource('app/rest/organizations/:id', {}, {
      query: {
        isArray: true,
        method: 'GET',
        transformResponse: function(data) {
          return JSON.parse(data).organizations.map(function(item, index) {
            item.className = (index + 1) % 3 === 0 ? "resource" : "project";

            return item;
          });
        }
      }
    });
});


respondecoApp.controller('MainController', function($scope, $location, $rootScope, Project, Organization) {
  $scope.projects = Project.query({ pageSize: 6, fields: [ "name", "progress", "projectLogo", "propertyTags", "organization", "purpose", "resourceRequirements" ].join(",") }, function() {
    setTimeout(function() {
      App.cubeportfolio();
    });
  });
  $scope.organizations = Organization.query({
    pageSize: 6,
    fields: "logo"
  });

  $scope.goToOrganization = function(id) {
    document.location = '/#/organization/' + id;
  }
});
