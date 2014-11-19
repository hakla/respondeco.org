'use strict';

respondecoApp.controller('OrganizationControllerEdit', function($scope, $location, $routeParams, resolvedOrganization, Organization, Account, User) {

    var id = $routeParams.id;
    var isNew = id === 'new';

    var organization = {};

    $scope.organization = {
        npo: false,
        owner: false
    };

    if (isNew) {
        // get the current logged in user and set the organization owner to it
        Account.get().$promise.then(function(account) {
            $scope.organization.owner = account.login;
        });
    }

    $scope.organizations = resolvedOrganization;

    $scope.isNew = function() {
        return isNew;
    };

    $scope.create = function() {
        organization.npo = $scope.organization.isNpo || false;
        organization.owner = $scope.organization.owner;
        organization.name = $scope.organization.name;
        organization.description = $scope.organization.description;
        organization.email = $scope.organization.email;

        Organization[isNew ? 'save' : 'update'](organization,
            function() {
                $scope.clear();
            },
            function(resp) {
                console.error(resp.data.message);
            });
    };

    $scope.update = function(name) {
        $scope.organization = Organization.get({
            id: name
        }, function(org) {
            User.getByOrgId({
                id: org.id
            });
        });

        $scope.orgJoinRequests = OrgJoinRequest.get({
            id: name
        });
    };

    $scope.delete = function(id) {
        Organization.delete({
                id: id
            },
            function() {
                $scope.organizations = Organization.query();
            });
    };

    $scope.clear = function() {
        if (isNew) {
            $location.path('organization');
        } else {
            $location.path('organization/' + $scope.organization.name);
        }
    };

    $scope.orgJoinRequest

    if (isNew == false) {
        $scope.update(id);
    }
});
