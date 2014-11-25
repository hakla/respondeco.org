'use strict';

var RegisterPage = function() {

    this.newUsername = "testuserabc";

    this.username = element(by.model("registerAccount.login"));
    this.email = element(by.model("registerAccount.email"));
    this.password = element(by.model("registerAccount.password"));
    this.confirmedPassword = element(by.model("confirmPassword"));

    this.registerButton = element(by.css('[translate="register.form.button"]'));

    this.successMessage = element(by.css('[translate="register.messages.success"]'));
    this.errorUserExistsMessage = element(by.css('[translate="register.messages.error.userexists"]'));
    this.errorMessage = element(by.css('[translate="register.messages.error.fail"]'));
    this.doNotMatchMessage = element(by.css('[translate="global.messages.error.dontmatch"]'));

    this.usernameRequired = element(by.css('[translate="register.messages.validate.login.required"]'));
    this.emailRequired = element(by.css('[translate="global.messages.validate.email.required"]'));
    this.passwordRequired = element(by.css('[translate="global.messages.validate.newpassword.required"]'));
    this.confirmedPasswordRequired = element(by.css('[translate="global.messages.validate.confirmpassword.required"]'));

    this.navigate = function() {
        browser.get("http://localhost:9000/#/register");
    }
};

module.exports = new RegisterPage();
