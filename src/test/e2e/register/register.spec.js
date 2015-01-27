describe('e2e: registration', function() {
    var registerPage = require('./register.po.js');
    beforeEach(function() {
        registerPage.navigate();
        browser.waitForAngular();
    });
    it('should show an error if the confirmed password does not match', function() {
        registerPage.email.sendKeys("test@test.at");
        registerPage.password.sendKeys("password");
        registerPage.confirmedPassword.sendKeys("passw");
        registerPage.registerButton.click();
        browser.waitForAngular();
        expect(registerPage.doNotMatchMessage.isDisplayed()).toBe(true);
    });
    it('should display error messages if input fields are empty', function() {
        registerPage.email.sendKeys(" ");
        registerPage.password.sendKeys(" ");
        registerPage.confirmedPassword.sendKeys(" ");
        expect(registerPage.emailRequired.isDisplayed()).toBe(true);
        expect(registerPage.passwordRequired.isDisplayed()).toBe(true);
        expect(registerPage.confirmedPasswordRequired.isDisplayed()).toBe(true);
    })
});
