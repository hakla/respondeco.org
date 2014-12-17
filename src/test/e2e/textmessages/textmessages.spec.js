describe('e2e: textmessages', function() {
    var messagePage = require('./textmessages.po.js');
    var loggedIn = false;
    beforeEach(function() {
        if(loggedIn === false) {
            login.login();
            loggedIn = true;
        }
    });

});
