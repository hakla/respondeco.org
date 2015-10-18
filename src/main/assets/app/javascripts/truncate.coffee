angular.module('truncate', []).filter('characters', ->
  (input, chars, breakOnWord) ->
    if isNaN(chars)
      input
    else if chars <= 0
      ''
    else if input and input.length > chars
      input = input.substring(0, chars)
      if !breakOnWord
        lastspace = input.lastIndexOf(' ')
        #get last space
        if lastspace != -1
          input = input.substr(0, lastspace)
      else
        while input.charAt(input.length - 1) == ' '
          input = input.substr(0, input.length - 1)
      input + '...'

    input
).filter 'words', ->
  (input, words) ->
    if isNaN(words)
      input
    else if words <= 0
      ''
    else if input
      inputWords = input.split(/\s+/)

      if inputWords.length > words
        input = inputWords.slice(0, words).join(' ') + '...'

    input
