/**
 * Created by Benjamin Fraller on 08.12.2014.
 */

'use strict';

var NewProjectPage = function() {



    this.name = element(by.model("project.name"));
    this.purpose = element(by.model("project.purpose"));
    this.tags = element(by.model("project.propertyTags"));

    this.concreteButton = element(by.model("project.concrete"));

    this.startDate = element(by.model("project.startDate"));
    this.endDate = element(by.model("project.endDate"));

    this.saveButton = element(by.id("saveButton"));
    this.cancelButton = element(by.id("cancelButton"));
    this.addResourceButton = element(by.id("addResourceButton"));

    //modal bindings for resource requirement

    this.resourceName = element(by.model("resource.name"));
    this.resourceDescription = element(by.model("resource.description"));
    this.resourceAmount = element(by.model("resource.amount"));
    this.resourceTags = element(by.model("selectedResourceTags"));
    this.resourceEssential = element(by.model("resource.isEssential"));

    this.resourceSaveButton = element(by.id("resourceSaveButton"));
    this.resourceCancelButton = element(by.id("resourceCancelButton"));



    this.navigate = function() {
        browser.get("http://localhost:9000/#/projects/edit/undefined");
    }
};

module.exports = new NewProjectPage();
