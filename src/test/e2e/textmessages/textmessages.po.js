'use strict';
var TextMessagePage = function() {
    this.newMessageButton = element(by.css('[data-target="#saveTextMessageModal"]'));
    this.cancelButton = element(by.css('[translate="global.form.cancel"]'));
    this.sendButton = element(by.css('[type="submit"'));
    this.messageReceiver = element(by.model('textMessageToSend.receiver'));
    this.messageContent = element(by.model('textMessageToSend.content'));
    this.navigate = function() {
        browser.get("http://localhost:9000/#/textmessages");
    }
};
module.exports = new TextMessagePage();
