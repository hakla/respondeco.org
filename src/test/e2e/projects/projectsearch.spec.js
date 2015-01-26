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
   /* it('should display projects after clicking the search button', function() {
        browser.waitForAngular();
        searchProjectsPage.navigate();
        browser.waitForAngular();
        searchProjectsPage.nameTextField.sendKeys("admin\n\n");
        browser.waitForAngular();
        expect(element(by.css('.project-item')).isPresent()).toBe(true);
        expect(browser.getLocationAbsUrl()).toContain('projects');
    });*/

    it('should navigate to the project page after clicking a project', function() {
        browser.waitForAngular();
        searchProjectsPage.navigate();
        browser.waitForAngular();
        expect(element(by.css('.project-item')).isPresent()).toBe(true);
        searchProjectsPage.projectElement.click();
        browser.waitForAngular();
        expect(browser.getLocationAbsUrl()).toMatch('/projects/[0-9]+');
    });
});
