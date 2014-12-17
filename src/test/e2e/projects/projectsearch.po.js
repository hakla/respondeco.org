/**
 * Created by Clemens Puehringer on 13/12/14.
 */

var ProjectSearchPage = function() {
    this.nameTextField = element(by.model("project.name"));
    this.tagsInput = element(by.model("newTag.text"));
    this.searchButton = element(by.id("searchButton"));
    this.projectElement = element.all(by.css(".project-item")).get(0);
    this.navigate = function() {
        browser.get("http://localhost:9000/#/projects");
        browser.waitForAngular();
    }
};
module.exports = new ProjectSearchPage();
