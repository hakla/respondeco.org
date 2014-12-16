describe('e2e: newresourceoffer', function() {
    var offer = require('./newresourceoffer.po.js');
    var login = require('../login.js');

    login.login();
    browser.waitForAngular();

    beforeEach(function() {
        offer.navigate();
        browser.waitForAngular();
    });

    it('should navigate to create new resourceoffer', function() {
        expect(browser.getLocationAbsUrl()).toContain("/resource/new");
    });

    it('should create a new resourceoffer', function() {
        offer.name.sendKeys("Ressource");
        offer.description.sendKeys("Das ist eine tolle Ressource");
        offer.amount.sendKeys("5");
        offer.tags.sendKeys("Computer, Technik, Cool");

        offer.startDate.sendKeys("10.10.2015");
        offer.endDate.sendKeys("11.10.2015");

        offer.isCommercial.click();
        offer.saveButton.click();

        browser.waitForAngular();

        expect(browser.getLocationAbsUrl()).toEqual("/ownresource");
    });
});
