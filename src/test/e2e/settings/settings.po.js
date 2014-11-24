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

    this.setTitle = function(title) {
        this.title.clear().sendKeys(title);
    };

    this.setFirstName = function(firstName) {
        this.firstName.clear().sendKeys(firstName);
    };

    this.setLastName = function(lastName) {
        this.lastName.clear().sendKeys(lastName);
    };

    this.setEmail = function(email) {
        this.email.clear().sendKeys(email);
    };

    this.setDescription = function(description) {
        this.description.clear().sendKeys(description);
    };

    this.navigate = function() {
        browser.get("http://localhost:9000/#/settings");
    }
};

module.exports = new SettingsPage();
