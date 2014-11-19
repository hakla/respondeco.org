describe('end2end: textmessages', function() {
    var ptor;

    beforeEach(function() {
        browser.get('http://127.0.0.1:9000/#/textmessages');
        ptor = protractor.getInstance();
    });

    it('should load the home page', function() {
        element(by.model('username')).sendKeys('user');
        element(by.model('password')).sendKeys('user');

        element(by.css('[ng-click="login()"]')).click();

        element(by.buttonText('global.textmessages.createmessage')).click();
        browser.waitForAngular();

        element(by.model('textMessageToSend.receiver')).sendKeys('admin');
        element(by.model('textMessageToSend.content')).sendKeys('Guten Tag das ist ein Test, hiermit schreibe ich ihnen die nachricht');


    });
});
