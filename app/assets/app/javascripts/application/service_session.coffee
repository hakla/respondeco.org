SessionService = ->
# TODO remove everything except account
  @create = (account) ->
    @login = account.login
    @firstName = account.firstName
    @lastName = account.lastName
    @email = account.email
    @userRoles = account.roles
    @profilePicture = account.profilePicture

  @invalidate = ->
    @login = null
    @firstName = null
    @lastName = null
    @email = null
    @userRoles = null

  @valid = -> @login isnt null and @login isnt undefined

  @

angular
  .module 'respondecoApp'
  .factory 'Session', SessionService
