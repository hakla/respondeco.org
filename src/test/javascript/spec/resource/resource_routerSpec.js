'use strict';

describe('Resource Router Test', function() {

    beforeEach(module('respondecoApp'));

    it('should map routes to controller', function() {

        inject(function($route) {
            expect($route.routes['/resource'].controller).toBe('ResourceController');
            expect($route.routes['/resource'].templateUrl).toEqual('/views/resources.html');

            expect($route.routes['/resource/:id'].controller).toBe('ResourceController');
            expect($route.routes['/resource/:id'].templateUrl).toEqual('/views/resource-new.html');

        	expect($route.routes['/ownresource'].controller).toBe('ResourceController');
        	expect($route.routes['/ownresource'].templateUrl).toEqual('/views/resources-own.html');
    	});

    });
});
