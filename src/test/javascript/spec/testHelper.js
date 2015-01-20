var TestHelper = {

    /**
     * Sets expected requests that are expected after a test has run
     * @return {[type]} [description]
     */
    flushHttpBackend: function(httpBackend) {
        // app.js --> $rootScope.on("$routeChangeStart")
        httpBackend.expectGET('protected/authentication_check.gif').respond(200, '');

        // app.js --> otherwise rule in route initialization
        httpBackend.expectGET('views/main.html').respond(200, '');

        // AuthenticationSharedService --> verify function
        httpBackend.expectGET('app/rest/account').respond(200, '');

        //flush the backend to "execute" the request to do the expected POST assertion.
        httpBackend.flush();
    },

    /**
     * Sets initial expected requests (done e.g. by setting the default translation in app.js)
     */
    initHttpBackend: function(httpBackend) {
        // app.js --> translation initialization
        httpBackend.expectGET('i18n/de.json').respond(200, '');
    }

};
