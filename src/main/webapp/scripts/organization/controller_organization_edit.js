'use strict';

respondecoApp.controller('OrganizationControllerEdit', function($scope, $location, $routeParams, resolvedOrganization, Organization, Account, User, OrgJoinRequest, TextMessage, AuthenticationSharedService, $rootScope) {

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
            $scope.organization.email = account.email;
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
        organization.logo = $scope.organization.logo;

        Organization[isNew ? 'save' : 'update'](organization,
            function() {
                $scope.clear();

                // Set global organization, otherwise on routeChange the "Create an organization" dialog would be shown
                $rootScope._account.organization = {};

                // Refresh the account of the currently logged in user
                AuthenticationSharedService.refresh();
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

            $scope.users = Organization.getInvitableUsers({
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
                $location.path('organization');

                // Set global organization, otherwise on routeChange the "Create an organization" dialog would be shown
                $rootScope._account.organization = null;

                // Refresh the account of the currently logged in user
                AuthenticationSharedService.refresh();
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
        // save if the user should be created and invited to join the organization
        var invite = false;

        if (typeof $scope.selectedUser === 'string') {
            $scope.users.forEach(function(user) {
                if (user.login == $scope.selectedUser || user.email == $scope.selectedUser) {
                    $scope.selectedUser = user;
                }
            });

            if (typeof $scope.selectedUser === 'string') {
                invite = confirm("Ein Benutzer mit dieser E-Mail Adresse existiert noch nicht, soll er eingeladen werden?");

                if (invite === true) {
                    $scope.selectedUser = {
                        login: "sendInvitation",
                        email: $scope.selectedUser
                    };
                } else {
                    // user doesn't exist and no invite should be sent - there's nothing to do
                    return;
                }
            }
        }

        if (invite === false) {
            OrgJoinRequest.save({
                organization: {
                    id: $scope.organization.id
                },
                user: {
                    id: $scope.selectedUser.id
                }
            }, function(data) {
                updateOrgJoinRequests();
                TextMessage.save({
                    receiver: {
                        id: data.user.id
                    },
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
        } else {
            OrgJoinRequest.save({
                organization: {
                    id: $scope.organization.id
                },
                user: $scope.selectedUser
            }, updateOrgJoinRequests);
        }

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

        // the name of an organization cannot be changed after creation --> show a tooltip
        $scope.tooltip_notChangeable = "global.tooltip.not-changeable";
    } else {
        // the name of an organization cannot be changed after creation --> show a tooltip
        $scope.tooltip_notChangeable = "global.tooltip.not-changeable-init";
    }

    // the name of an organization cannot be changed after creation --> show a tooltip
    $scope.tooltip_notChangeable_email = "global.tooltip.not-changeable-email";
});