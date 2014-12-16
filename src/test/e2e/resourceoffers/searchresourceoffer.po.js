'use strict';

var ResourceSearch = function() {

	this.searchField = element(by.model("resourceSearch.name"));
	this.searchButton = element(by.id("searchButton"));
	this.commercialButton = element(by.id("commercialButton"));
	this.nonCommercialButton = element(by.id("nonCommercialButton"));

    this.navigate = function() {
        browser.get("http://localhost:9000/#/resource");
    }
};

module.exports = new ResourceSearch();
