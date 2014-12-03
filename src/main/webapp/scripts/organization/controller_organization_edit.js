'use strict';

respondecoApp.controller('OrganizationControllerEdit', function($scope, $location, $routeParams, resolvedOrganization, Organization, Account, User, OrgJoinRequest, TextMessage) {

    var id = $routeParams.id;
    var isNew = id === 'new';

    var organization = {};

    $scope.logo = null;
    $scope.organization = {
        npo: false,
        owner: false
    };

    $scope.onUploadComplete = function(fileItem, response) {
        $scope.organization.logo = response;
    };

    $scope.alerts = [];
    $scope.isCollapsed = false;

    $scope.closeAlert = function(index) {
        $scope.alerts.splice(index, 1);
    };

    var updateOrgJoinRequests = function() {
        $scope.orgJoinRequests = Organization.getOrgJoinRequests({
            id: $scope.organization.id
        });
    };

    if (isNew) {
        // get the current logged in user and set the organization owner to it
        Account.get(null, function(account) {
            $scope.organization.owner = account.login;
        });
    }

    $scope.organizations = resolvedOrganization;

    $scope.isNew = function() {
        return isNew;
    };

    $scope.create = function() {
        organization.npo = $scope.organization.npo || false;
        organization.name = $scope.organization.name;
        organization.description = $scope.organization.description;
        organization.email = $scope.organization.email;
        organization.logo = $scope.organization.logo;

        Organization[isNew ? 'save' : 'update'](organization,
            function() {
                $scope.clear();
            },
            function(resp) {
                console.error(resp.data.message);
            });
    };

    $scope.update = function(id) {
        Organization.get({
            id: id
        }, function(org) {
            $scope.organization = org;
            
            $scope.users = User.getInvitableUsers({
                id: $scope.organization.id
            });

            updateOrgJoinRequests();

            Organization.getMembers({
                id: $scope.organization.id
            }, function(data) {
                $scope.members = data;
            });
        });
    };

    var deleteState = false;
    $scope.deleteType = "default";
    $scope.deleteMessage = "organization.delete";

    $scope.delete = function(id) {
        if (deleteState === false) {
            $scope.deleteMessage = "organization.delete.sure";
            $scope.deleteType = "danger";
            deleteState = true;
            return;
        }

        deleteState = true;
        Organization.delete({
                id: id
            },
            function() {
                $scope.clear();
            });
    };

    $scope.clear = function() {
        if (isNew) {
            $location.path('organization');
        } else {
            $location.path('organization/' + $scope.organization.id);
        }
    };

    $scope.sendInvite = function() {
        OrgJoinRequest.save({
            organization: {
                name: $scope.organization.name
            },
            user: {
                login: $scope.selectedUser.login
            }
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
