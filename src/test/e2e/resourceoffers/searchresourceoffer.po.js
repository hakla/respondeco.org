'use strict';

var ResourceOffers = function() {

    this.navigate = function() {
        browser.get("http://localhost:9000/#/resource");
    }
};

module.exports = new ResourceOffers();
