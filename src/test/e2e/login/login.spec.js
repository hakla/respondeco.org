'use strict';

describe('e2e: login', function() {
    var loginPage = require("./login.po.js");
    var ptor = protractor.getInstance();

    beforeEach(function() {
        loginPage.navigate();
        browser.waitForAngular();
    });

    it('should login', function() {
        expect(browser.getLocationAbsUrl()).toMatch("/login");
        loginPage.username.sendKeys("user");
        loginPage.password.sendKeys("user");
        loginPage.loginButton.click();
        browser.waitForAngular();

        expect(browser.getLocationAbsUrl()).not.toMatch("/login");
    });

    it('should fail if username and password are empty', function() {
    	loginPage.loginButton.click();
        expect(loginPage.errorMessage.isDisplayed()).toBe(true);
    });
});
