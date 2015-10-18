LanguageController = ($scope, $translate, LanguageService, $window, $rootScope) ->
  $scope.changeLanguage = (languageKey) ->
    changeLocale languageKey
    LanguageService.getBy(languageKey).then (languages) ->
      $scope.languages = languages

  changeLocale = (languageKey) ->
    $translate.use languageKey
    $window.moment.locale languageKey
    $rootScope.$broadcast 'amMoment:localeChanged'

  LanguageService.getBy().then (languages) ->
    $scope.languages = languages
    changeLocale $translate.use()

angular
  .module 'respondecoApp'
  .controller 'LanguageController', LanguageController

