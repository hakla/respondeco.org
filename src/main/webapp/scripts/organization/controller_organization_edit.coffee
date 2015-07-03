'use strict'
respondecoApp.controller 'OrganizationControllerEdit', ($scope, $location, $routeParams, Organization, Account, User, OrgJoinRequest, TextMessage, AuthenticationSharedService, $rootScope) ->
  id = $routeParams.id
  isNew = id == 'new'
  organization = {}
  $scope.logo = null
  $scope.organization =
    isNpo: false
    owner: false

  $scope.onUploadComplete = (fileItem, response) ->
    $scope.organization.logo = response

  $scope.alerts = []
  $scope.isCollapsed = false

  $scope.closeAlert = (index) ->
    $scope.alerts.splice index, 1

  updateOrgJoinRequests = ->
    $scope.orgJoinRequests = Organization.getOrgJoinRequests(id: $scope.organization.id)

  if isNew
    # get the current logged in user and set the organization owner to it
    Account.get null, (account) ->
      $scope.organization.owner = account.login
      $scope.organization.email = account.email

  $scope.isNew = ->
    isNew

  $scope.setRootScopeOrganization = (value) ->
    $rootScope._account.organization = value

  $scope.create = ->
    organization.isNpo = $scope.organization.isNpo or false
    organization.name = $scope.organization.name
    organization.description = $scope.organization.description
    organization.email = $scope.organization.email
    organization.logo = $scope.organization.logo
    Organization[if isNew then 'save' else 'update'] organization, (->
      $scope.clear()
      # Set global organization, otherwise on routeChange the "Create an organization" dialog would be shown
      $scope.setRootScopeOrganization {}
      # Refresh the account of the currently logged in user
      AuthenticationSharedService.refresh()
    ), (resp) ->
      console.error resp.data.message

  $scope.update = (id) ->
    Organization.get { id: id, fields: 'description,logo,members' }, (org) ->
      $scope.organization = org
      $scope.users = Organization.getInvitableUsers(id: $scope.organization.id)
      updateOrgJoinRequests()
      Organization.getMembers { id: $scope.organization.id }, (data) ->
        $scope.members = data

  deleteState = false
  $scope.deleteType = 'default'
  $scope.deleteMessage = 'organization.delete'

  $scope.delete = (id) ->
    if deleteState == false
      $scope.deleteMessage = 'organization.delete.sure'
      $scope.deleteType = 'danger'
      deleteState = true

    deleteState = true
    Organization.delete { id: id }, ->
      $location.path 'organization'
      # Set global organization, otherwise on routeChange the "Create an organization" dialog would be shown
      $scope.setRootScopeOrganization null
      # Refresh the account of the currently logged in user
      AuthenticationSharedService.refresh()

  $scope.clear = ->
    if isNew
      $location.path 'organization'
    else
      $location.path 'organization/' + $scope.organization.id

  $scope.sendInvite = ->
    # save if the user should be created and invited to join the organization
    $scope.invite = false
    if typeof $scope.selectedUser == 'string'
      $scope.users.forEach (user) ->
        if user.login == $scope.selectedUser or user.email == $scope.selectedUser
          $scope.selectedUser = user
      if typeof $scope.selectedUser == 'string'
        $scope.invite = confirm('Ein Benutzer mit dieser E-Mail Adresse existiert noch nicht, soll er eingeladen werden?')
        if $scope.invite == true
          $scope.selectedUser =
            login: 'sendInvitation'
            email: $scope.selectedUser
        else
          # user doesn't exist and no invite should be sent - there's nothing to do
    if $scope.invite == false
      OrgJoinRequest.save {
        organization: id: $scope.organization.id
        user: id: $scope.selectedUser.id
      }, ((data) ->
        updateOrgJoinRequests()
        TextMessage.save
          receiver: id: data.user.id
          content: 'You got invited to join the organization ' + $scope.organization.name + '!'
      ), (error) ->
        if error.status == 400
          $scope.alerts.push
            msg: 'A user can only be invited once'
            type: 'warning'
    else
      OrgJoinRequest.save {
        organization: id: $scope.organization.id
        user: $scope.selectedUser
      }, updateOrgJoinRequests
    $scope.selectedUser = null

  $scope.deleteInvitation = (id) ->
    OrgJoinRequest.delete { id: id }, ->
      updateOrgJoinRequests()

  if isNew == false
    $scope.update id
    # the name of an organization cannot be changed after creation --> show a tooltip
    $scope.tooltip_notChangeable = 'global.tooltip.not-changeable'
  else
    # the name of an organization cannot be changed after creation --> show a tooltip
    $scope.tooltip_notChangeable = 'global.tooltip.not-changeable-init'
  # the name of an organization cannot be changed after creation --> show a tooltip
  $scope.tooltip_notChangeable_email = 'global.tooltip.not-changeable-email'
