/**
 * Created by clemens on 01/12/14.
 */
'use strict';

describe('Project Controller Tests ', function() {
    beforeEach(module('respondecoApp'));

    describe('ProjectController', function() {
        var scope, location, routeParams, ProjectNamesService, PropertyTagsNamesService, ProjectService, ResourceRequirementService;
        var fakeDeferred;
        var emptyProject;

        beforeEach(inject(function($rootScope, $controller, $location, $q, $sce, $routeParams, ProjectNames, PropertyTagNames, Project, ResourceRequirement) {
            scope = $rootScope.$new();
            location = $location;
            ProjectNamesService = ProjectNames;
            PropertyTagsNamesService = PropertyTagNames;
            ProjectService = Project;
            ResourceRequirementService = ResourceRequirement;
            routeParams = $routeParams;
            fakeDeferred = {
                $promise: {
                    then: function(object) {}
                }
            };
            emptyProject = {
                id: null,
                name: null,
                purpose: null,
                concrete: false,
                startDate: null,
                endDate: null,
                logo: null,
                propertyTags: [],
                resourceRequirements: []
            };

            $controller('ProjectController', {
                $scope: scope,
                $location: location,
                $routeParams: routeParams,
                Project: ProjectService,
                ProjectNames: ProjectNamesService,
                PropertyTagNames: PropertyTagsNamesService,
                ResourceRequirement: ResourceRequirementService
            });
        }));

        it('should set the logo after upload', function() {
            scope.onUploadComplete(null, {
                id: 1
            });

            expect(scope.project.logo.id).toEqual(1);
        });


        it('should call create', function() {
            spyOn(ProjectService, 'update');
            scope.project = emptyProject;
            scope.create();

            expect(ProjectService.update).toHaveBeenCalled();
            ProjectService.update.calls.mostRecent().args[1]();
        });

        it('should toggle calendars', function() {
            scope.openStart({
                stopPropagation: function() {}
            });
            scope.openEnd({
                stopPropagation: function() {}
            });
        });

        it('should call delete', function() {
            spyOn(ProjectService, 'delete');
            scope.delete(1);
            expect(ProjectService.delete).toHaveBeenCalledWith({
                id: 1
            }, jasmine.any(Function));
            ProjectService.delete.calls.mostRecent().args[1]();
        });

        it('should redirect', function() {
            scope.createProject();
            expect(location.path()).toEqual("/project/create");
        });

        it('should map resourceRequirements', function() {
            scope.project = emptyProject;
            scope.selectedResourceTags = [{
                name: "1"
            }, {
                name: "2"
            }];
            scope.createRequirement();

            expect(scope.resource.resourceTags).toEqual([{
                name: "1"
            }, {
                name: "2"
            }]);
        });

    });
});