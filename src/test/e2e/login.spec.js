
describe('E2E: mainpage', function() {
    var ptor;

    beforeEach(function() {
        browser.get('http://127.0.0.1:9000/#login');
        ptor = protractor.getInstance();
    });

    it('should load the home page', function() {
        element(by.model('username')).sendKeys('user');
        element(by.model('password')).sendKeys('user');

        element(by.css('[ng-click="login()"]')).click();
    });
});



