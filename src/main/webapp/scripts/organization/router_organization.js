'use strict';

respondecoApp
    .config(function ($routeProvider, $httpProvider, $translateProvider, USER_ROLES) {
            $routeProvider
                .when('/organization', {
                    templateUrl: 'views/organizations.html',
                    controller: 'OrganizationsController',
                    resolve:{
                        resolvedOrganization: ['Organization', function (Organization) {
                            return Organization.query();
                        }]
                    },
                    access: {
                        authorizedRoles: [USER_ROLES.all]
                    }
                }).when('/organization/:id', {
                    templateUrl: 'views/organization.html',
                    controller: 'OrganizationController',
                    resolve: {
                        resolvedOrganization: ['Organization', function(Organization) {
                            return Organization.query();
                        }]
                    },
                    access: {
                        authorizedRoles: [USER_ROLES.all]
                    }
                }).when('/organization/edit/:id', {
                    templateUrl: 'views/organization-edit.html',
                    controller: 'OrganizationControllerEdit',
                    resolve: {
                        resolvedOrganization: ['Organization', function(Organization) {
                            return Organization.query();
                        }]
                    },
                    access: {
                        authorizedRoles: [USER_ROLES.user]
                    }
                }).when('/admin/organization/verify', {
                    templateUrl: 'views/organizations_verify.html',
                    controller: 'OrganizationControllerVerify',
                    access: {
                        authorizedRoles: [USER_ROLES.admin]
                    }
                })
        });
