'use strict';

respondecoApp.controller('OrganizationController', function($scope, $location, $routeParams, resolvedOrganization, Organization, Account, User, OrgJoinRequest, TextMessage) {
    var redirectToOrganization = function(name) {
        $location.path('organization/' + name);
    };
    var isOwner = false;
    var user;

    var updateOrgJoinRequests = function() {
        $scope.orgJoinRequests = OrgJoinRequest.query({
            id: $scope.organization.name
        });
    }

    $scope.organizations = resolvedOrganization;

    // get the current logged in user and set the organization owner to it
    Account.get().$promise.then(function(account) {
        user = account;
    });

    $scope.update = function(name) {
        $scope.organization = Organization.get({
            id: name
        });

        $scope.organization.$promise.then(function() {
            $scope.users = User.getInvitableUsers({
                id: $scope.organization.id
            });
            $scope.orgJoinRequests = OrgJoinRequest.get({
                id: $scope.organization.name
            });

            isOwner = user.login === $scope.organization.owner;
        });
    };

    $scope.delete = function(id) {
        id = id || $scope.organization.id;

        if (confirm("Wirklich löschen?") === false) return;

        Organization.delete({
                id: id
            },
            function() {
                $scope.organizations = Organization.query();
            });
    };

    $scope.redirectToEdit = function() {
        $location.path('organization/edit/' + $scope.organization.name);
    }

    $scope.redirectToNew = function() {
        $location.path('organization/edit/new');
    };

    $scope.isOwner = function() {
        return isOwner;
    };

    $scope.invite = false;

    $scope.sendInvite = function() {
        OrgJoinRequest.save({
            orgId: $scope.organization.id,
            userlogin: $scope.selectedUser.login
        }, function(data) {
            updateOrgJoinRequests();
            TextMessage.save({
                receiver: data.userLogin,
                content: "You got invited to join the organization " + $scope.organization.name + "!"
            });
        });
    };

    $scope.updateUser = function($item, $model, $label) {
        $selectedUser = $item;
    };

    $scope.redirectToOverview = function() {
        $location.path('organization');
    };

    $scope.deleteInvitation = function(id) {
        OrgJoinRequest.delete({
            id: id
        }, function() {
            updateOrgJoinRequests();
        });
    };

    $scope.redirectToOrganization = redirectToOrganization;

    if ($routeParams.id !== undefined) {
        $scope.update($routeParams.id);
    }
});
