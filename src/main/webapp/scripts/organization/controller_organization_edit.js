(function() {
  'use strict';
  respondecoApp.controller('OrganizationControllerEdit', function($scope, $location, $routeParams, Organization, Account, User, OrgJoinRequest, TextMessage, AuthenticationSharedService, $rootScope) {
    var deleteState, id, isNew, organization, updateOrgJoinRequests;
    id = $routeParams.id;
    isNew = id === 'new';
    organization = {};
    $scope.logo = null;
    $scope.organization = {
      npo: false,
      owner: false
    };
    $scope.onUploadComplete = function(fileItem, response) {
      return $scope.organization.logo = response;
    };
    $scope.alerts = [];
    $scope.isCollapsed = false;
    $scope.closeAlert = function(index) {
      return $scope.alerts.splice(index, 1);
    };
    updateOrgJoinRequests = function() {
      return $scope.orgJoinRequests = Organization.getOrgJoinRequests({
        id: $scope.organization.id
      });
    };
    if (isNew) {
      Account.get(null, function(account) {
        $scope.organization.owner = account.login;
        return $scope.organization.email = account.email;
      });
    }
    $scope.isNew = function() {
      return isNew;
    };
    $scope.setRootScopeOrganization = function(value) {
      return $rootScope._account.organization = value;
    };
    $scope.create = function() {
      organization.npo = $scope.organization.isNpo || false;
      organization.name = $scope.organization.name;
      organization.description = $scope.organization.description;
      organization.email = $scope.organization.email;
      organization.logo = $scope.organization.logo;
      return Organization[isNew ? 'save' : 'update'](organization, (function() {
        $scope.clear();
        $scope.setRootScopeOrganization({});
        return AuthenticationSharedService.refresh();
      }), function(resp) {
        return console.error(resp.data.message);
      });
    };
    $scope.update = function(id) {
      return Organization.get({
        id: id,
        fields: 'description,logo,members'
      }, function(org) {
        $scope.organization = org;
        $scope.users = Organization.getInvitableUsers({
          id: $scope.organization.id
        });
        updateOrgJoinRequests();
        return Organization.getMembers({
          id: $scope.organization.id
        }, function(data) {
          return $scope.members = data;
        });
      });
    };
    deleteState = false;
    $scope.deleteType = 'default';
    $scope.deleteMessage = 'organization.delete';
    $scope["delete"] = function(id) {
      if (deleteState === false) {
        $scope.deleteMessage = 'organization.delete.sure';
        $scope.deleteType = 'danger';
        deleteState = true;
      }
      deleteState = true;
      return Organization["delete"]({
        id: id
      }, function() {
        $location.path('organization');
        $scope.setRootScopeOrganization(null);
        return AuthenticationSharedService.refresh();
      });
    };
    $scope.clear = function() {
      if (isNew) {
        return $location.path('organization');
      } else {
        return $location.path('organization/' + $scope.organization.id);
      }
    };
    $scope.sendInvite = function() {
      $scope.invite = false;
      if (typeof $scope.selectedUser === 'string') {
        $scope.users.forEach(function(user) {
          if (user.login === $scope.selectedUser || user.email === $scope.selectedUser) {
            return $scope.selectedUser = user;
          }
        });
        if (typeof $scope.selectedUser === 'string') {
          $scope.invite = confirm('Ein Benutzer mit dieser E-Mail Adresse existiert noch nicht, soll er eingeladen werden?');
          if ($scope.invite === true) {
            $scope.selectedUser = {
              login: 'sendInvitation',
              email: $scope.selectedUser
            };
          } else {

          }
        }
      }
      if ($scope.invite === false) {
        OrgJoinRequest.save({
          organization: {
            id: $scope.organization.id
          },
          user: {
            id: $scope.selectedUser.id
          }
        }, (function(data) {
          updateOrgJoinRequests();
          return TextMessage.save({
            receiver: {
              id: data.user.id
            },
            content: 'You got invited to join the organization ' + $scope.organization.name + '!'
          });
        }), function(error) {
          if (error.status === 400) {
            return $scope.alerts.push({
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
      return $scope.selectedUser = null;
    };
    $scope.deleteInvitation = function(id) {
      return OrgJoinRequest["delete"]({
        id: id
      }, function() {
        return updateOrgJoinRequests();
      });
    };
    if (isNew === false) {
      $scope.update(id);
      $scope.tooltip_notChangeable = 'global.tooltip.not-changeable';
    } else {
      $scope.tooltip_notChangeable = 'global.tooltip.not-changeable-init';
    }
    return $scope.tooltip_notChangeable_email = 'global.tooltip.not-changeable-email';
  });

}).call(this);

//# sourceMappingURL=controller_organization_edit.js.map
