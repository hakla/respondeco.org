describe('e2e: settings', function() {
    var page = require('./settings.po.js');
    var login = require('../login.js');
    var loggedIn = false;
    beforeEach(function() {
        if(loggedIn === false) {
            login.login();
            loggedIn = true;
        }
    });
    it('should display the site', function() {
        page.navigate();
        browser.waitForAngular();
        expect(browser.getLocationAbsUrl()).toContain("/settings");
    });
});
