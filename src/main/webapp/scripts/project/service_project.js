'use strict';

respondecoApp.factory('Project', function ($resource, $http) {
    var project = $resource('app/rest/projects/:id', {}, {});
    project.currentProject = null;
    project.setProject = function (project) {
        this.currentProject = project;
    }

    project.getProjectNames = function(value) {
        return $http.get("app/rest/projects", { params: { filter: "value", fields: "name"} } )
    }

    return project;
    });
