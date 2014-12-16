describe('e2e: searchresourceoffer', function() {
    var search = require('./searchresourceoffer.po.js');
    var login = require('../login.js');
    var loggedIn = false;

    beforeEach(function() {
        if(loggedIn == false) {
            login.login();
            loggedIn = true;
        }

        search.navigate();
        browser.waitForAngular();
    });

    it('should navigate to create new resourceoffer', function() {
        expect(browser.getLocationAbsUrl()).toContain("/resource");
    });

    it('should search for a resource', function() {
        search.searchField.sendKeys("test");

        search.commercialButton.click();
        search.searchButton.click();
    });

    it('should only display commercial resources', function() {
        search.searchField.sendKeys("");
        search.commercialButton.click();
        search.searchButton.click();
    });

     it('should only display commercial resources', function() {
        search.searchField.sendKeys("");
        search.nonCommercialButton.click();
        search.searchButton.click();
    });

});
