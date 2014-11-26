describe('e2e: settings', function() {
    var page = require('./settings.po.js');
    var login = require('../login.js');

    beforeEach(function() {
        login.login();
        page.navigate();
        browser.waitForAngular();
    });

    it('should display the site', function() {
        expect(browser.getLocationAbsUrl()).toContain("/settings");
    });

    it('should edit the user settings', function() {
        page.editButton.click();

        page.setTitle("Dr.");
        page.setFirstName("User");
        page.setLastName("User");
        page.setEmail("test@test.at");
        page.setDescription("Vorstand Greenpeace");

        page.saveButton.click();
    });
});