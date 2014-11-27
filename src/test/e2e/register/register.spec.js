describe('e2e: registration', function() {
    var registerPage = require('./register.po.js');

    beforeEach(function() {
        registerPage.navigate();
        browser.waitForAngular();
    });

    it('should register a new user', function() {
        expect(registerPage.successMessage.isDisplayed()).toBe(false);

        registerPage.username.sendKeys(registerPage.newUsername);
        registerPage.email.sendKeys("test@test.at");
        registerPage.password.sendKeys("password");
        registerPage.confirmedPassword.sendKeys("password");

        registerPage.registerButton.click();
        browser.waitForAngular();

        expect(registerPage.successMessage.isDisplayed()).toBe(true);
    });

    it('should decline a user which is already registered', function() {
        registerPage.username.sendKeys(registerPage.newUsername);
        registerPage.email.sendKeys("test@test.at");
        registerPage.password.sendKeys("password");
        registerPage.confirmedPassword.sendKeys("password");

        registerPage.registerButton.click();
        browser.waitForAngular();

        expect(registerPage.errorUserExistsMessage.isDisplayed()).toBe(true);
    });

    it('should show an error if the confirmed password does not match', function() {
        registerPage.username.sendKeys("testuser");
        registerPage.email.sendKeys("test@test.at");
        registerPage.password.sendKeys("password");
        registerPage.confirmedPassword.sendKeys("passw");

        registerPage.registerButton.click();
        browser.waitForAngular();

        expect(registerPage.doNotMatchMessage.isDisplayed()).toBe(true);
    });

    it('should display error messages if input fields are empty', function() {
        registerPage.username.sendKeys(" ");
        registerPage.email.sendKeys(" ");
        registerPage.password.sendKeys(" ");
        registerPage.confirmedPassword.sendKeys(" ");

        expect(registerPage.usernameRequired.isDisplayed()).toBe(true);
        expect(registerPage.emailRequired.isDisplayed()).toBe(true);
        expect(registerPage.passwordRequired.isDisplayed()).toBe(true);
        expect(registerPage.confirmedPasswordRequired.isDisplayed()).toBe(true);
    })


});
