OrganizationController = ($scope, $location, $routeParams, Organization, Account, SocialMedia, AuthenticationSharedService, $rootScope, $filter, Session, IsoCategories) ->
  isOwner = false
  user = undefined
  $scope.twitterConnected = false
  $scope.facebookConnected = false
  $scope.xingConnected = false
  $scope.shownRating = 0
  $scope.ratingCount = 0
  $scope.posting = {}

  $scope.user = -> user

  $scope.map =
    center:
      latitude: 48
      longitude: 16
    zoom: 8

  $scope.clearCategory = (category) ->
    $scope._subcategory = null
    category

  $scope.createGrid = ->
    gridContainer = $('#grid-container')
    filtersContainer = $('#filters-container')
    wrap = undefined
    filtersCallback = undefined

    ###******************************
        init cubeportfolio
    #*****************************
    ###

    gridContainer.cubeportfolio
      layoutMode: 'grid'
      rewindNav: true
      scrollByPage: false
      mediaQueries: [
        {
          width: 1100
          cols: 4
        }
        {
          width: 800
          cols: 4
        }
        {
          width: 500
          cols: 4
        }
        {
          width: 320
          cols: 1
        }
      ]
      defaultFilter: '*'
      animationType: 'rotateSides'
      gapHorizontal: 10
      gapVertical: 10
      gridAdjustment: 'responsive'
      caption: 'overlayBottomPush'
      displayType: 'sequentially'
      displayTypeSpeed: 100
      singlePageInlineDelegate: '.cbp-singlePageInline'
      singlePageInlinePosition: 'below'
      singlePageInlineInFocus: true
      singlePageInlineCallback: (url, element) ->
        # to update singlePageInline content use the following method: this.updateSinglePageInline(yourContent)
        @updateSinglePageInline $(element).parents('.cbp-item-wrapper').find('.inline-content').html()

  $scope.isoCategories = IsoCategories.query ->
    $scope.isoCategories = $scope.isoCategories.isocategorys

  $scope.update = (name) ->
    $scope.organization = Organization.get({ id: name, fields: 'logo,projects(name,projectLogo,resourceRequirements(amount,originalAmount)),email,description,members,postingFeed(postings(createdDate)),verified,website' }, ->
      Organization.getMembers { id: $scope.organization.id }, (data) ->
        $scope.members = data
      Account.get null, (account) ->
        user = account
        isOwner = user != undefined and user.login == $scope.organization.owner?.login

      $scope.organization.description = $scope.organization.description || ''

      setTimeout ->
        $scope.createGrid()

      $scope.getConnections()
    )

  ###
  #  Gets all active connection for the currently logged in user
  #  and if the connection for the specific provider exists, it
  #  sets the connected variable for the appropriate provider to true.
  ###
  $scope.getConnections = ->
    if Session.valid()
      SocialMedia.getConnections (response) ->
        response.forEach (connection) ->
          $scope.twitterConnected = connection.provider is 'twitter'
          $scope.facebookConnected = connection.provider is 'facebook'
          $scope.xingConnected = connection.provider is 'xing'

  $scope.refreshOrganizationRating = ->
    #get new aggregated rating
    Organization.getAggregatedRating { id: $routeParams.id }, (rating) ->
      $scope.shownRating = rating.rating
      $scope.ratingCount = rating.count

  $scope.refreshOrganizationRating()

  $scope.delete = (id) ->
    id = id or $scope.organization.id
    unless confirm('Wirklich löschen?') is false
      Organization.delete { id: id }, ->
        $scope.organizations = Organization.query()

  $scope.redirectToEdit = ->
    $location.path 'organization/edit/' + $scope.organization.id

  $scope.isOwner = ->
    isOwner

  $scope.updateUser = ($item, $model, $label) ->
    $scope.selectedUser = $item

  $scope.redirectToOverview = ->
    $location.path 'organization'

  $scope.redirectToOwnResources = ->
    $location.path 'ownresource'

  $scope.redirectToNewProject = ->
    $location.path 'projects/edit/new'

  $scope.redirectToProjects = ->
    $location.path 'organization/' + $scope.organization.id + '/projects'

  $scope.redirectToProject = (projectId) ->
    $location.path 'project/' + projectId

  if $routeParams.id != undefined
    $scope.update $routeParams.id

  ###
  # Button Event. Try to follow the current Organization Newsfeed
  ###
  $scope.follow = ->
    Organization.follow { id: $scope.organization.id }, null, (result) ->
      $scope.following = true

  ###
  # Button Event. Try to un-follow the current Organization Newsfeed
  ###
  $scope.unfollow = ->
    Organization.unfollow { id: $scope.organization.id }, (result) ->
      $scope.following = false

  ###
  # show or hide the Un-Follow Button. Show only if the current organization is being followed by user
  # @returns {boolean} true => show, else hide
  ###
  $scope.showUnfollow = ->
    $scope.following == true
    # && $scope.isOwner() == false;

  ###
  # show or hide the Follow Button. Show only if the current organization is not being followed by user
  # @returns {boolean} true => show, else hide
  ###
  $scope.showFollow = ->
    $scope.following == false
    # && $scope.isOwner() == false;

  # Posting
  # Posting for project
  $scope.postingPage = -1
  $scope.postingPageSize = 5
  $scope.postingInformation = null
  $scope.postingsTotal = null
  $scope.postings = []

  # function to refresh postings for the organization in the scope; get postings in the given pagesize
  refreshPostings = ->
    Organization.getPostingsByOrgId {
      id: $routeParams.id
      page: $scope.postingPage
      pageSize: $scope.postingPageSize
    }, (data) ->
      $scope.postingsTotal = data.totalElements
      $scope.postings = $scope.postings.concat(data.postings)

  $scope.canShowMorePostings = -> $scope.postings.length < $scope.postingsTotal

  # shows more postings by incrementing the postingCount (default 5 + 5)
  $scope.showMorePostings = ->
    $scope.postingPage = $scope.postingPage + 1
    refreshPostings()

  # show first page of postings
  $scope.showMorePostings()

  $scope.post = (posting) ->
    if posting.information?.length < 5 or posting.title?.length < 5
      return

    Organization.addPostingForOrganization { id: $routeParams.id }, posting, (newPosting) ->
      # add new posting and cut array down to current number of shown postings
      $scope.postings.unshift newPosting
      $scope.postingsTotal = $scope.postingsTotal + 1
      $scope.postings = $scope.postings.slice(0, ($scope.postingPage + 1) * $scope.postingPageSize)
      $scope.posting = {}
      $scope.postingform.$setPristine()

    # urlPath = $location.url()

    # if $scope.postOnTwitter
    #   SocialMedia.createTwitterPost
    #     urlPath: urlPath
    #     post: $scope.postingInformation
    # else if $scope.postOnFacebook
    #   SocialMedia.createFacebookPost
    #     urlPath: urlPath
    #     post: $scope.postingInformation
    # else if $scope.postOnXing
    #   SocialMedia.createXingPost
    #     urlPath: urlPath
    #     post: $scope.postingInformation
    # $scope.focused = false

  $scope.deletePosting = (id) ->
    Organization.deletePosting {
      id: $scope.organization.id
      pid: id
    }, ->
      for i of $scope.postings
        if $scope.postings[i].id == id
          $scope.postings.splice i, 1
          break
      return
    return

  ###*
  # Resolve the follow state for the current organization of logged in user
  ###
  $scope.followingState = ->
    if Session.valid()
      Organization.followingState { id: $routeParams.id }, (follow) ->
        $scope.following = follow.state

  $scope.getDonatedResources = ->
    Organization.getDonatedResources { id: $routeParams.id }, {
      pageSize: 20
      page: 0
    }, (response) ->
      projects = {}
      $scope.donatedResources = response
      response.resourceMatches?.map((x) ->
        x.project.match = [x]
        x.project
      ).forEach (project) ->
        if projects[project.id] == undefined
          projects[project.id] = project
        else
          projects[project.id].match = projects[project.id].match.concat(project.match)

      $scope.projects = projects

  mapToDTO = (model) ->
    name: model.name
    id: model.id
    description: model.description
    email: model.email
    website: model.website
    logo: model.logo

  $scope.save = ->
    Organization.update mapToDTO $scope.organization

  # allow execute follow state only if organization ID is set!
  if $routeParams.id != 'new' or $routeParams.id != 'null' or $routeParams.id != 'undefined'
    $scope.followingState()
    $scope.getDonatedResources()

angular
  .module 'respondecoApp'
  .controller 'OrganizationController', OrganizationController
