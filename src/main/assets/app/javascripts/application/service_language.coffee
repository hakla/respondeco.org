LanguageService = ($http, $translate, LANGUAGES) ->
  {
    getBy: (language) ->
      if language == undefined
        language = $translate.storage().get('NG_TRANSLATE_LANG_KEY')
      if language == undefined
        language = 'en'
      promise = $http.get('/i18n/' + language + '.json').then((response) ->
        LANGUAGES
      )
      promise
  }

angular
  .module 'respondecoApp'
  .factory 'LanguageService', LanguageService
