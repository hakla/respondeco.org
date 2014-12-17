describe('e2e: searchresourceoffer', function() {
    var search = require('./searchresourceoffer.po.js');
    var login = require('../login.js');
    var loggedIn = false;
    beforeEach(function() {
        if(loggedIn === false) {
            login.login();
            loggedIn = true;
        }
    });
    it('should search for a resource', function() {
        search.navigate();
        browser.waitForAngular();
        search.searchField.sendKeys("test");
        search.searchButton.click();
    });

});
