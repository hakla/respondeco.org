/**
 * Created by Clemens Puehringer on 13/12/14.
 */

var ProjectSearchPage = function() {
    this.nameTextField = element(by.model("project.name"));
    this.searchButton = element(by.id("searchButton"));
    this.projectElement = element.all(by.css(".project-item")).get(0);
    this.navigate = function() {
        browser.get("http://localhost:9000/#/projects");
        browser.waitForAngular();
    };
    this.navigateToProject1 = function(){
        browser.get("http://localhost:9000/#/projects/1");
        browser.waitForAngular();
    };

    this.follow = element(by.name("follow_button"));
    this.unfollow = element(by.name("unfollow_button"));
};
module.exports = new ProjectSearchPage();
