'use strict';

respondecoApp.controller('OrganizationControllerEdit', function($scope, $location, $routeParams, resolvedOrganization, Organization, Account, User, OrgJoinRequest, TextMessage) {

    var id = $routeParams.id;
    var isNew = id === 'new';

    var organization = {};

    $scope.organization = {
        npo: false,
        owner: false
    };

    $scope.alerts = [];
    $scope.isCollapsed = false;

    $scope.closeAlert = function(index) {
        $scope.alerts.splice(index, 1);
    };

    var updateOrgJoinRequests = function() {
        $scope.orgJoinRequests = OrgJoinRequest.query({
            id: $scope.organization.name
        });
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
            $scope.users = User.getInvitableUsers({
                id: $scope.organization.id
            });

            User.getByOrgId({
                id: org.id
            }, null, null, function(error) {
                console.log(error);
            });

            $scope.orgJoinRequests = OrgJoinRequest.get({
                id: $scope.organization.name
            });

            Organization.getMembers({
                id: $scope.organization.id
            }).$promise.then(function(data) {
                $scope.members = data;
            });
        });
    };

    var deleteState = false;
    $scope.deleteMessage = "organization.delete";

    $scope.delete = function(id) {
        if (deleteState === false) {
            $scope.deleteMessage = "organization.delete.sure";
            deleteState = true;
            return;
        }

        deleteState = true;
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

    $scope.sendInvite = function() {
        OrgJoinRequest.save({
            orgName: $scope.organization.name,
            userLogin: $scope.selectedUser.login
        }, function(data) {
            updateOrgJoinRequests();
            TextMessage.save({
                receiver: data.userLogin,
                content: "You got invited to join the organization " + $scope.organization.name + "!"
            });
        }, function(error) {
            if (error.status === 400) {
                $scope.alerts.push({
                    msg: 'A user can only be invited once',
                    type: 'warning'
                });
            }
        });
        $scope.selectedUser = null;
    };

    $scope.deleteInvitation = function(id) {
        OrgJoinRequest.delete({
            id: id
        }, function() {
            updateOrgJoinRequests();
        });
    };

    if (isNew == false) {
        $scope.update(id);
    }
});
