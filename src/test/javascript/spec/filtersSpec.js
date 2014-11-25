describe('filter', function() {

  beforeEach(module('respondecoAppFilters'));

  describe('logo', function() {

    it('should return a template if empty',
        inject(function(logoFilter) {
      expect(logoFilter(null)).toMatch(/^http:\/\/lorempixel.com\/200\/200\/city.*/);
    }));
  });
});
