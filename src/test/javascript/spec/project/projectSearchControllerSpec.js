/**
 * Created by clemens on 01/12/14.
 */
'use strict';

describe('ProjectSearch Controller Tests ', function () {
    beforeEach(module('respondecoApp'));

    describe('ProjectSearchController', function () {
        var scope, location, ProjectNamesService, PropertyTagsNamesService, ProjectService;
        var fakeDeferred;

        beforeEach(inject(function ($rootScope, $controller, $location, $q, ProjectNames, PropertyTagNames, Project) {
            scope = $rootScope.$new();
            location = $location;
            ProjectNamesService = ProjectNames;
            PropertyTagsNamesService = PropertyTagNames;
            ProjectService = Project;
            fakeDeferred = {
                $promise: {
                    then: function(object) {}
                }
            }

            $controller('ProjectSearchController', {$scope: scope, $location: location, Project: ProjectService,
                resolvedProjects: [], ProjectNames: ProjectNamesService, PropertyTagNames: PropertyTagsNamesService});
        }));

        it('should query matching property tag names', function () {
            spyOn(PropertyTagsNamesService, "getPropertyTagNames").and.returnValue(fakeDeferred);
            scope.getPropertyTagNames("test");
            expect(PropertyTagsNamesService.getPropertyTagNames).toHaveBeenCalledWith("test");
        });

        it('should query matching project names', function () {
            spyOn(ProjectNamesService, "getProjectNames").and.returnValue(fakeDeferred);
            scope.getProjectNames("test");
            expect(ProjectNamesService.getProjectNames).toHaveBeenCalledWith("test");
        });

        it('should change the location to the project if redirectToProject is called', function () {
            spyOn(location, "path");
            scope.redirectToProject({id: 1, name: "test", purpose: "test"});
            expect(location.path).toHaveBeenCalledWith("/projects/1");
        });

        it('should query projects by name and set the found projects on success', function () {
            var projectList = [{id: 1, name: "test", purpose: "blub", tags: []}];
            spyOn(ProjectService, "query");
            scope.project = {name: "test"};
            scope.search();

            expect(ProjectService.query).toHaveBeenCalledWith(
                {filter: "test", tags: ""},
                jasmine.any(Function),
                jasmine.any(Function));

            //Simulate call to success callback
            ProjectService.query.calls.mostRecent().args[1](projectList);
            expect(scope.searchError).toBeNull();
            expect(scope.noProjects).toBeNull();
            expect(scope.projects).toBe(projectList);
        });

        it('should query projects by tags and set the found projects on success', function () {
            var projectList = [{id: 1, name: "test", purpose: "blub", tags: []}];
            spyOn(ProjectService, "query");
            scope.selectedTags = [{name: "test"}, {name: "foo"}, {name: "bar"}];
            scope.search();

            expect(ProjectService.query).toHaveBeenCalledWith(
                {filter: null, tags: "test,foo,bar"},
                jasmine.any(Function),
                jasmine.any(Function));

            //Simulate call to success callback
            ProjectService.query.calls.mostRecent().args[1](projectList);
            expect(scope.searchError).toBeNull();
            expect(scope.noProjects).toBeNull();
            expect(scope.noProjects).toBeNull();
            expect(scope.projects).toBe(projectList);
        });

        it('should query projects and set searchError on failure', function () {
            spyOn(ProjectService, "query");
            scope.project = {name: "test", tags: []};
            scope.selectedTags = [{name: "test"}];
            scope.search();

            expect(ProjectService.query).toHaveBeenCalledWith(
                {filter: "test", tags: "test"},
                jasmine.any(Function),
                jasmine.any(Function));

            //Simulate call to error callback
            ProjectService.query.calls.mostRecent().args[2]({});
            expect(scope.searchError).toBe("ERROR");
            expect(scope.noProjects).toBeNull();
        });

        it('should set noProjects if the query returned an empty result', function () {
            spyOn(ProjectService, "query");
            scope.selectedTags = [{name: "test"}, {name: "foo"}, {name: "bar"}];
            scope.search();

            expect(ProjectService.query).toHaveBeenCalledWith(
                {filter: null, tags: "test,foo,bar"},
                jasmine.any(Function),
                jasmine.any(Function));

            //Simulate call to success callback
            ProjectService.query.calls.mostRecent().args[1]([]);
            expect(scope.searchError).toBeNull();
            expect(scope.noProjects).toBe("NOPROJECTS");
        });
    });

});
