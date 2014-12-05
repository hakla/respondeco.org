'use strict';

var NewResourceOffers = function() {
    this.name = element(by.model("resource.name"));
    this.description = element(by.model("resource.description"));
    this.amount = element(by.model("resource.amount"));
    this.tags = element(by.model("resource.resourceTags"));

    this.isCommercial = element(by.model("resource.isCommercial"));
    this.isRecurrent = element(by.model("resource.isRecurrent"));
    this.startDate = element(by.model("resource.startDate"));
    this.endDate = element(by.model("resource.endDate"));
    
    this.cancel = element(by.id("cancelButton"));
    this.saveButton = element(by.id("saveButton"));

    this.errorMessage = element(by.css('[translate="global.resource.new.error"]'));

    this.navigate = function() {
        browser.get("http://localhost:9000/#/resource/new");
    }
};

module.exports = new NewResourceOffers();
