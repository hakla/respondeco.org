AccountService = ($resource) ->
  $resource 'app/rest/account', {},
    leaveOrganization:
      method: 'POST'
      url: 'app/rest/account/leaveorganization'
    getNewsfeed:
      method: 'GET'
      url: 'app/rest/account/newsfeed'
    setProfilePicture:
      method: 'POST'
      url: 'app/rest/account/profilepicture'

angular
  .module 'respondecoApp'
  .factory 'Account', AccountService
