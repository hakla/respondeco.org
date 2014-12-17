/*
 Login Script
 */
'use strict';
var params = browser.params;
var Login = function() {
    this.login = function() {
        browser.driver.get('http://localhost:9000/#/login');
        element(by.model("username")).sendKeys(params.login.user);
        element(by.model("password")).sendKeys(params.login.password);
        element(by.css('[ng-click="login()"]')).click();
        browser.waitForAngular();
    };
};
module.exports = new Login();
