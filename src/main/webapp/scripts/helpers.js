Respondeco = {};
Respondeco.Helpers = {};
Respondeco.Helpers.Url = (function() {
	// Contains all query parameters
	var params = {};

	// Split search parameters by & and iterate over all query parts (x=y)
	$.each(document.location.search.substring(1).split(/[&]/), function() {
		// Split query part by =
		var param = this.split("=");

		// query part 0 == key
		// query part 1 == value
		params[param[0]] = param[1];
	});

	return {
		param: function(name) {
			// if no parameter was defined return the params object
			if (name === undefined) {
				return params;
			}

			// if the parameter *name* isset then return it
			if (params[name] !== undefined) {
				return params[name];
			}

			// otherwise return undefined
			return undefined;
		}
	}
})()