'use strict'
respondecoApp.controller 'OrganizationController', ($scope, $location, $routeParams, Organization, Account, SocialMedia, AuthenticationSharedService, $rootScope, $filter) ->
  isOwner = false
  user = undefined
  $scope.twitterConnected = false
  $scope.facebookConnected = false
  $scope.xingConnected = false
  $rootScope.title = 'Organisation'
  $scope.shownRating = 0
  $scope.ratingCount = 0

  $scope.titlesForView =
    about_us: -> "- Über die Organisation"
    members: -> "- Mitarbeiter"
    news: -> "- Neuigkeiten"
    transactions: -> "- Transaktionsübersicht"
    settings: -> "- Einstellungen"
    prices: -> "- Auszeichnungen"
    projects: -> "- Projekte"

  $scope.view = 'about_us'

  $scope.categories =
    "main": [
        "Organisationsführung",
        "Faire Betriebs- und Geschäftspraktiken",
        "Menschenrechte",
        "Konsumentenanliegen",
        "Arbeitspraktiken",
        "Einbindung und Entwicklung der Gemeinschaft",
        "Umwelt"
    ],
    "Faire Betriebs- und Geschäftspraktiken": ["Korruptionsbekämpfung", "Verantwortungsbewusste politische Mitwirkung", "Fairer Wettbewerb", "Gesellschaftliche Verantwortung in der Wertschöpfungskette fördern", "Eigentumsrechte achten"],
    "Menschenrechte": ["Gebührende Sorgfalt", "Menschenrechte in kritischen Situationen", "Mittäterschaft vermeiden", "Missstände beseitigen", "Diskriminierung und schutzbedürftige Gruppen", "Bürgerliche und politische Rechte", "Wirtschaftliche, soziale und kulturelle Rechte", "Grundlegende Prinzipien und Rechte bei der Arbeit"],
    "Konsumentenanliegen": ["Faire Werbe-, Vertriebs- und Vertragspraktiken sowie sachliche und unverfälschte, nicht irreführende Informationen", "Schutz von Gesundheit und Sicherheit der Konsumenten", "Nachhaltiger Konsum", "Kundendienst, Beschwerdemanagement und Schlichtungsverfahren", "Schutz und Vertraulichkeit von Kundendaten", "Sicherung der Grundversorgung", "Verbraucherbildung und Sensibilisierung"],
    "Arbeitspraktiken": ["Beschäftigung und Beschäftigungsverhältnisse", "Arbeitsbedingungen und Sozialschutz", "Sozialer Dialog", "Gesundheit und Sicherheit am Arbeitsplatz", "Menschliche Entwicklung und Schulung am Arbeitsplatz "],
    "Einbindung und Entwicklung der Gemeinschaft": ["Einbindung der Gemeinschaft", "Bildung und Kultur", "Schaffen von Arbeitsplätzen und berufliche Qualifizierung", "Technologien entwickeln und Zugang dazu ermöglichen"],
    "Umwelt": ["Vermeidung der Umweltbelastung", "Nachhaltige Nutzung von Ressourcen", "Abschwächung des Klimawandels und Anpassung", "Umweltschutz, Artenvielfalt und Wiederherstellung natürli­cher Lebensräume"]

  $scope.chosenCategories = [
    main: 0,
    sub: "Menschenrechte in kritischen Situationen",
    description: "Lorem ipsum Ut irure aliquip cupidatat est sint sint irure nostrud laboris sint laborum."
  ]

  $scope.map =
    center:
      latitude: 48
      longitude: 16
    zoom: 8

  $scope.clearCategory = (category) ->
    $scope._subcategory = null
    category

  $scope.currentView = -> 
    $rootScope.title = "#{$scope.organization?.name}"
    "/template/organization/#{$scope.view}.html"

  $scope.update = (name) ->
    $scope.organization = Organization.get({ id: name, fields: 'logo,projects(name,projectLogo),email,description,members,postingFeed(postings(createdDate)),website' }, ->
      Organization.getMembers { id: $scope.organization.id }, (data) ->
        $scope.members = data
      Account.get null, (account) ->
        user = account
        isOwner = user != undefined and user.login == $scope.organization.owner?.login
      $scope.getConnections()
    )

  $logo = jQuery '.profile-logo'
  $container = $logo.next '.profile-container'
  $window = jQuery(window).on 'scroll', (event) ->
    # if $window.scrollTop() > 115 and $window.width() > 991
    #   # calculate new position
    #   $logo.css
    #     position: 'fixed'
    #     width: $logo.outerWidth()
    #     top: "53px"

    #   # set offset on container
    #   $container.addClass 'col-xs-offset-3'
    # else
    #   # reset fixed position
    #   $logo[0].style.position = null
    #   $logo[0].style.width = null
    #   $logo[0].style.top = null

    #   # reset offset
    #   $container.removeClass 'col-xs-offset-3'

  ###
  #  Gets all active connection for the currently logged in user
  #  and if the connection for the specific provider exists, it
  #  sets the connected variable for the appropriate provider to true.
  ###

  $scope.getConnections = ->
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

  #Posting
  #Posting for project
  $scope.postingPage = -1
  $scope.postingPageSize = 5
  $scope.postingInformation = null
  $scope.postingsTotal = null
  $scope.postings = []
  #function to refresh postings for the organization in the scope; get postings in the given pagesize

  refreshPostings = ->
    Organization.getPostingsByOrgId {
      id: $routeParams.id
      page: $scope.postingPage
      pageSize: $scope.postingPageSize
    }, (data) ->
      $scope.postingsTotal = data.totalElements
      $scope.postings = $scope.postings.concat(data.postings)

  $scope.canShowMorePostings = -> $scope.postings.length < $scope.postingsTotal

  #shows more postings by incrementing the postingCount (default 5 + 5)

  $scope.showMorePostings = ->
    $scope.postingPage = $scope.postingPage + 1
    refreshPostings()

  #show first page of postings
  $scope.showMorePostings()

  $scope.addPosting = ->
    if $scope.postingInformation.length < 5 or $scope.postingInformation.length > 2048
      return
    Organization.addPostingForOrganization { id: $routeParams.id }, $scope.postingInformation, (newPosting) ->
      #add new posting and cut array down to current number of shown postings
      $scope.postings.unshift newPosting
      $scope.postingsTotal = $scope.postingsTotal + 1
      $scope.postings = $scope.postings.slice(0, ($scope.postingPage + 1) * $scope.postingPageSize)
      $scope.postingInformation = null
      $scope.postingform.$setPristine()

    urlPath = $location.url()

    if $scope.postOnTwitter
      SocialMedia.createTwitterPost
        urlPath: urlPath
        post: $scope.postingInformation
    else if $scope.postOnFacebook
      SocialMedia.createFacebookPost
        urlPath: urlPath
        post: $scope.postingInformation
    else if $scope.postOnXing
      SocialMedia.createXingPost
        urlPath: urlPath
        post: $scope.postingInformation
    $scope.focused = false

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

  #allow execute follow state only if organization ID is set!
  if $routeParams.id != 'new' or $routeParams.id != 'null' or $routeParams.id != 'undefined'
    $scope.followingState()
    $scope.getDonatedResources()
