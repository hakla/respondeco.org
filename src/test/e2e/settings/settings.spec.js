describe('e2e: settings', function() {
    var settingsPage = require('./settings.po.js');
    var login = require('../login.js');

    login.login();

    beforeEach(function() {
        settingsPage.navigate();
        browser.waitForAngular();
    });

    it('should display the site', function() {
        expect(browser.getLocationAbsUrl()).toContain("/settings");
    });

    it('should edit the user settings', function() {
        settingsPage.editButton.click();

        settingsPage.setTitle("Dr");
    });

});
