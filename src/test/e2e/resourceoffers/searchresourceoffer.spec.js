describe('e2e: searchresourceoffer', function() {
    var offer = require('./newresourceoffer.po.js');
    var login = require('../login.js');

    beforeEach(function() {
        login.login();
        offer.navigate();
        browser.waitForAngular();
    });

    it('should navigate to create new resourceoffer', function() {
        expect(browser.getLocationAbsUrl()).toEqual("/resource/");
    });

});
