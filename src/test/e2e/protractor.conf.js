exports.config = {
  specs: ['*/*.spec.js'],
  baseUrl: 'http://localhost:9000',
  onPrepare: function() {
      browser.driver.manage().window().maximize;

      //browser.get("http://localhost:9000/#/login");
      //element(by.model('username')).sendKeys("user");
      //element(by.model('password')).sendKeys("user");
      //element(by.css('[ng-click="login()"]')).click();

      //browser.waitForAngular();
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
