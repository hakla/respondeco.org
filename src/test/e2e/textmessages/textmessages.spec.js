describe('e2e: textmessages', function() {
    var messagePage = require('./textmessages.po.js');
    var loggedIn = false;

    beforeEach(function() {
        if(loggedIn = false) {
            login.login();
            loggedIn = true;
        }
        messagePage.navigate();
        browser.waitForAngular();
    });

    it('should send a message', function() {
        messagePage.newMessageButton.click();
        browser.waitForAngular();

        messagePage.messageReceiver.sendKeys("admin");
        browser.waitForAngular();
        messagePage.messageContent.sendKeys("hallo");
        browser.waitForAngular();

        messagePage.sendButton.click();

    });
});
