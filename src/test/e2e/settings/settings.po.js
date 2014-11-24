'use strict';

var SettingsPage = function() {
    this.title = element(by.model("settingsAccount.title"));
    this.firstName = element(by.model("settingsAccount.firstName"));
    this.lastName = element(by.model("settingsAccount.lastName"));
    this.gender = element(by.model("settingsAccount.gender"));
    this.email = element(by.model("settingsAccount.email"));
    this.description = element(by.model("settingsAccount.description"));

    this.editButton = element(by.id('editButton'));
    this.saveButton = element(by.id('saveButton'));
    this.cancelButton = element(by.id('cancelButton'));

    this.deleteButton = element(by.id('deleteButton'));
    this.confirmDeleteButon = element(by.id('confirmDeleteButon'));
    this.abortDeleteButton = element(by.id('abortDeleteButton'));

    this.newPicture = element(by.id('newPicture'));

    this.setTitle = function(asdfasdf) {
        this.title.clear().then(function(asdfasdf) {
            this.title.sendKeys(asdfasdf);
        });
    }

    this.navigate = function() {
        browser.get("http://localhost:9000/#/settings");
    }
};

module.exports = new SettingsPage();
