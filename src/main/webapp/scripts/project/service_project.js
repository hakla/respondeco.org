'use strict';

respondecoApp.factory('Project', function ($resource) {
    var project = $resource('app/rest/projects/:id', {}, {});
    project.currentProject = null;
    project.setProject = function (project) {
        this.currentProject = project;
    }
    return project;
    });
