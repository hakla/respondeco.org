/**
 * Created by clemens on 01/12/14.
 */
'use strict';

describe('ProjectSearch Controller Tests ', function () {
    beforeEach(module('respondecoApp'));

    describe('ProjectSearchController', function () {
        var scope, location, ProjectNamesService, PropertyTagsNamesService, ProjectService, OrganizationService,$routeService,$routeParamsService;
        var fakeDeferred;

        beforeEach(inject(function ($rootScope, $controller, $location, $q, ProjectNames, PropertyTagNames, Project, Organization, $route) {
            scope = $rootScope.$new();
            location = $location;
            ProjectNamesService = ProjectNames;
            PropertyTagsNamesService = PropertyTagNames;
            ProjectService = Project;
            OrganizationService = Organization;
            $routeService = $route;

            var routeParamsMock = {id: 1};

            $routeParamsService = routeParamsMock;
            fakeDeferred = {
                $promise: {
                    then: function(object) {}
                }
            }

            // mock $route.current.$$route
            $routeService.current = {
                $$route: ""
            };

            $controller('ProjectSearchController', {$scope: scope, $location: location, Project: ProjectService,
                resolvedProjects: [], ProjectNames: ProjectNamesService, PropertyTagNames: PropertyTagsNamesService,
                Organization: OrganizationService, $route: $routeService, $routeParams: $routeParamsService});
        }));

        it('should change the location to the project if redirectToProject is called', function () {
            spyOn(location, "path");
            scope.redirectToProject({id: 1, name: "test", purpose: "test"});
            expect(location.path).toHaveBeenCalledWith("/projects/1");
        });

        it('should query projects by name and set the found projects on success', function () {
            var response = {projects: [{id: 1, name: "test", purpose: "blub", tags: []}], totalItems:3};
            spyOn(ProjectService, "query");
            scope.project = {name: "test"};
            scope.search();

            expect(ProjectService.query).toHaveBeenCalledWith(
                {page:0, pageSize:20, filter: null},
                jasmine.any(Function),
                jasmine.any(Function));

            //Simulate call to success callback
            ProjectService.query.calls.mostRecent().args[1](response);
            expect(scope.searchError).toBeNull();
            expect(scope.noProjects).toBeNull();
            expect(scope.projects).toBe(response.projects);
        });

        it('should query projects and set searchError on failure', function () {
            spyOn(ProjectService, "query");
            scope.project = {name: "test", tags: []};
            scope.search();

            expect(ProjectService.query).toHaveBeenCalledWith(
                {page:0, pageSize:20, filter: null},
                jasmine.any(Function),
                jasmine.any(Function));

            //Simulate call to error callback
            ProjectService.query.calls.mostRecent().args[2]({});
            expect(scope.searchError).toBe("ERROR");
            expect(scope.noProjects).toBeNull();
        });

        it('should set noProjects if the query returned an empty result', function () {
            spyOn(ProjectService, "query");
            scope.search();

            expect(ProjectService.query).toHaveBeenCalledWith(
                {page:0, pageSize:20, filter: null},
                jasmine.any(Function),
                jasmine.any(Function));

            //Simulate call to success callback
            ProjectService.query.calls.mostRecent().args[1]({projects:[], totalItems:0});
            expect(scope.searchError).toBeNull();
            expect(scope.noProjects).toBe("NOPROJECTS");
        });

        it('should search after pressing search button', function() {
            spyOn(scope, 'search');
            scope.searchButton();

            expect(scope.currentPage).toEqual(1);
            expect(scope.search).toHaveBeenCalled();
        });

        it('should search after pagechange', function() {
            spyOn(scope, 'search');

            scope.onPageChange();

            expect(scope.search).toHaveBeenCalled();
        });

        it('should check the account', function(){
            spyOn(OrganizationService, 'get');

            var account =  {
                organization: {
                    id:1
                }
            };

            scope.checkAccount(account);

            expect(scope.ownOrganization).toBe(true);
            expect(OrganizationService.get).toHaveBeenCalled();
            OrganizationService.get.calls.mostRecent().args[1]('test');

            expect(scope.organization).toEqual('test');
        });
    });

});
