exports.config = {
  specs: ['*/*.spec.js'],
  baseUrl: 'http://localhost:9000',
  onPrepare: function() {
      browser.driver.manage().window().maximize;
    },
    suites: {
        textmessages: 'textmessages/*.spec.js',
        login: 'login/*.spec.js',
        register: 'register/*.spec.js',
        settings: 'settings/*.spec.js',
        newresourceoffers: 'resourceoffers/*.spec.js'
    },
    params: {
        login: {
            user: 'user',
            password: 'user'
        }
    }


};
