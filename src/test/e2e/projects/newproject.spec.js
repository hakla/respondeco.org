/**
 * Created by Benjamin Fraller on 08.12.2014.
 */

describe('e2e: newproject', function() {
    var newProjectPage = require('./newproject.po.js');
    var login = require('../login.js');

    login.login();
    browser.waitForAngular();

    beforeEach(function() {
        newProjectPage.navigate();
        browser.waitForAngular();
    });

    /*it('should create a new project', function() {

        newProjectPage.name.sendKeys("Project");
        newProjectPage.purpose.sendKeys("Ich würde gerne Hosen sammeln");

        actions = protractor.getInstance().actions()
        actions.mouseMove(newProjectPage.tags)
        actions.click()
        actions.sendKeys("Hallo, Computer, Test ");
        actions.perform()

        newProjectPage.concreteButton.click();

        newProjectPage.startDate.sendKeys("20.04.2015");
        newProjectPage.endDate.sendKeys("21.04.2015");


        browser.waitForAngular();
    });*/

    it('should redirect to projects after clicking on cancel', function() {
        newProjectPage.cancelButton.click();
        browser.waitForAngular();
        expect(browser.getLocationAbsUrl()).toContain('projects');

    });

    it('should add resource requirements', function() {
        newProjectPage.addResourceButton.click();
        browser.waitForAngular();


        newProjectPage.resourceName.sendKeys("Ressource");
        newProjectPage.resourceDescription.sendKeys("Ich hätte gern die folgende Ressource");
        newProjectPage.resourceAmount.sendKeys("5");
        newProjectPage.resourceName.sendKeys("Name");

        actions = protractor.getInstance().actions();
        actions.mouseMove(newProjectPage.resourceTags);
        actions.click();
        actions.sendKeys("Hallo, Computer, Test ");
        actions.perform();

        newProjectPage.resourceEssential.click();

        newProjectPage.resourceSaveButton.click();

        browser.waitForAngular();


        browser.sleep(4000);

    });
 

});
