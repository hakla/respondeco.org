(function() {
  'use strict';
  respondecoApp.config(function($routeProvider, $httpProvider, $translateProvider, USER_ROLES) {
    $routeProvider.when('/organization', {
      templateUrl: 'views/organizations.html',
      controller: 'OrganizationsController',
      access: {
        authorizedRoles: [USER_ROLES.all]
      }
    }).when('/organization/:id', {
      templateUrl: 'views/organization.html',
      controller: 'OrganizationController',
      access: {
        authorizedRoles: [USER_ROLES.all]
      }
    }).when('/organization/edit/:id', {
      templateUrl: 'views/organization-edit.html',
      controller: 'OrganizationControllerEdit',
      access: {
        authorizedRoles: [USER_ROLES.user]
      }
    }).when('/admin/organization/verify', {
      templateUrl: 'views/organizations_verify.html',
      controller: 'OrganizationControllerVerify',
      access: {
        authorizedRoles: [USER_ROLES.admin]
      }
    }).when('/organization/:id/projects', {
      templateUrl: 'views/projects_own.html',
      controller: 'ProjectSearchController',
      access: {
        authorizedRoles: [USER_ROLES.all]
      }
    });
  });

}).call(this);

//# sourceMappingURL=router_organization.js.map
