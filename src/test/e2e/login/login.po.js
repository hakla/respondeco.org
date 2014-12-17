'use strict';
var LoginPage = function() {
    this.username = element(by.model('username'));
    this.password = element(by.model('password'));
    this.loginButton = element(by.css('[ng-click="login()"]'));
    this.errorMessage = element(by.css('[ng-show="authenticationError"]'));
    this.navigate = function() {
        browser.get('http://localhost:9000/#/login');
    }
};
module.exports = new LoginPage();
