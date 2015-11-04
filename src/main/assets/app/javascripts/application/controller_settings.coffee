SettingsController = ($scope, $location, Account, AuthenticationSharedService, OrgJoinRequest, Organization) ->

  $scope.onComplete = (fileItem, response) ->
    $scope.settingsAccount.profilePicture = response
    $scope.profilePicture = response

    Account.setProfilePicture parseInt(response)

  $scope.success = null
  $scope.error = null
  $scope.fullName = null
  $scope.settingsAccount = {}
  Account.get { fields: 'organization' }, (account) ->
#account is an organization account, redirect to organization page
    if account.id == account.organization?.ownerId
# $location.path("/organization/" + account.organization.id);
    else
      $scope.settingsAccount = account
      $scope.organization = account.organization
      if $scope.settingsAccount.organizationId != null
        reloadOrganization $scope.settingsAccount.organizationId
      if $scope.settingsAccount.firstName == null
        $scope.fullName = $scope.settingsAccount.lastName
      else if $scope.settingsAccount.lastName == null
        $scope.fullName = $scope.settingsAccount.firstName
      else
        $scope.fullName = $scope.settingsAccount.firstName + ' ' + $scope.settingsAccount.lastName
      if account.profilePicture != null
        $scope.profilePicture = account.profilePicture.id

  $scope.success = null
  $scope.error = null
  $scope.gender = [
    'UNSPECIFIED'
    'MALE'
    'FEMALE'
  ]

  getCurrentOrgJoinRequests = ->
    OrgJoinRequest.current (data) ->
      $scope.orgJoinRequests = data
      data.forEach (el) ->
        Organization.get { id: el.organization.id }, (data) ->
          el.organization = data

  reloadOrganization = (id) ->
    if id != null
      $scope.organization = Organization.get(id: id)
    else
      Account.get (account) ->
        if account.organizationId != null
          reloadOrganization account.organizationId
        else
          $scope.organization = null

  getCurrentOrgJoinRequests()

  $scope.save = ->
    Account.save $scope.settingsAccount, ((value, responseHeaders) ->
      $scope.error = null
      $scope.success = 'OK'
      # $scope.settingsAccount = Account.get();
      if $scope.settingsAccount.firstName == null
        $scope.fullName = $scope.settingsAccount.lastName
      else if $scope.settingsAccount.lastName == null
        $scope.fullName = $scope.settingsAccount.firstName
      else
        $scope.fullName = $scope.settingsAccount.firstName + ' ' + $scope.settingsAccount.lastName
    ), (httpResponse) ->
      $scope.success = null
      $scope.error = 'ERROR'

  $scope.delete = ->
    Account.delete ((value, responseHeaders) ->
      $scope.error = null
      $scope.accountdeleted = 'OK'
      AuthenticationSharedService.logout()
    ), (httpResponse) ->
      $scope.accountdeleted = null
      $scope.error = 'ERROR'

  $scope.acceptInvitation = (id) ->
    OrgJoinRequest.accept { id: id }, ->
      getCurrentOrgJoinRequests()
      reloadOrganization()

  $scope.declineInvitation = (id) -> OrgJoinRequest.decline { id: id }, getCurrentOrgJoinRequests

  $scope.leaveOrganization = ->
    Account.leaveOrganization ->
      reloadOrganization()

  $scope.edit =
    image: true
    account: false

angular
  .module 'respondecoApp'
  .controller 'SettingsController', SettingsController
