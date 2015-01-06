/**
 * Created by Clemens Puehringer on 13/12/14.
 */
'use strict'

describe('e2e: project search', function() {
    var login = require('../login.js');
    var searchProjectsPage = require('./projectsearch.po.js');
    var loggedIn = false;
    beforeEach(function() {
        if(loggedIn === false) {
            login.login();
            loggedIn = true;
        }
    });
    it('should display projects after clicking the search button', function() {
        browser.waitForAngular();
        searchProjectsPage.navigate();
        browser.waitForAngular();
        searchProjectsPage.tagsInput.sendKeys("Umwelt ");
        searchProjectsPage.nameTextField.sendKeys("admin\n\n");
        browser.waitForAngular();
        expect(element(by.css('.project-item')).isPresent()).toBe(true);
        expect(browser.getLocationAbsUrl()).toContain('projects');
    });
    it('should navigate to the project page after clicking a project', function() {
        browser.waitForAngular();
        searchProjectsPage.navigate();
        browser.waitForAngular();
        expect(element(by.css('.project-item')).isPresent()).toBe(true);
        searchProjectsPage.projectElement.click();
        browser.waitForAngular();
        expect(browser.getLocationAbsUrl()).toMatch('/projects/[0-9]+');
    });

    it('should click follow button', function(){
        expect(searchProjectsPage.follow.isDisplayed()).toBe(true);
        expect(searchProjectsPage.unfollow.isDisplayed()).toBe(false);
        browser.waitForAngular();

        var actions = protractor.getInstance().actions();
        actions.mouseMove(searchProjectsPage.follow);
        actions.click();
        browser.waitForAngular();
        excpect(searchProjectsPage.follow.isDisplayed()).toBe(false);
        excpect(searchProjectsPage.unfollow.isDisplayed()).toBe(true);
    });

    it('should click unfollow button', function(){
        expect(searchProjectsPage.follow.isDisplayed()).toBe(false);
        expect(searchProjectsPage.unfollow.isDisplayed()).toBe(true);
        browser.waitForAngular();

        var actions = protractor.getInstance().actions();
        actions.mouseMove(searchProjectsPage.unfollow);
        actions.click();
        browser.waitForAngular();
        excpect(searchProjectsPage.follow.isDisplayed()).toBe(true);
        excpect(searchProjectsPage.unfollow.isDisplayed()).toBe(false);
    });
});
