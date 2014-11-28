package org.respondeco.respondeco.web.rest;

import com.codahale.metrics.annotation.Timed;
import com.wordnik.swagger.annotations.ApiOperation;
import org.respondeco.respondeco.domain.Project;
import org.respondeco.respondeco.domain.ResourceRequirement;
import org.respondeco.respondeco.repository.ProjectRepository;
import org.respondeco.respondeco.security.AuthoritiesConstants;
import org.respondeco.respondeco.service.ProjectService;
import org.respondeco.respondeco.service.exception.NoSuchProjectException;
import org.respondeco.respondeco.service.ResourcesService;
import org.respondeco.respondeco.service.exception.GeneralResourceException;
import org.respondeco.respondeco.service.exception.NoSuchUserException;
import org.respondeco.respondeco.service.exception.OperationForbiddenException;
import org.respondeco.respondeco.web.rest.dto.ProjectRequestDTO;
import org.respondeco.respondeco.web.rest.dto.ProjectResponseDTO;
import org.respondeco.respondeco.web.rest.util.ErrorConstants;
import org.respondeco.respondeco.web.rest.util.ErrorHelper;
import org.respondeco.respondeco.web.rest.dto.ResourceRequirementDTO;
import org.respondeco.respondeco.web.rest.util.RestParameters;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.annotation.security.RolesAllowed;
import javax.inject.Inject;
import javax.validation.Valid;
import java.util.List;
import java.util.Optional;

/**
 * REST controller for managing Project.
 */
@RestController
@RequestMapping("/app")
public class ProjectController {

    private final Logger log = LoggerFactory.getLogger(ProjectController.class);

    private ProjectService projectService;
    private ResourcesService resourcesService;

    @Inject
    public ProjectController(ProjectService projectService, ResourcesService resourcesService) {
        this.projectService = projectService;
        this.resourcesService = resourcesService;
    }

    /**
     * POST  /rest/project -> Create a new project.
     */
    @ApiOperation(value = "Create a project", notes = "Create or update a project")
    @RequestMapping(value = "/rest/projects",
            method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    @RolesAllowed(AuthoritiesConstants.USER)
    public ResponseEntity<?> create(@RequestBody @Valid ProjectRequestDTO project) {
        log.debug("REST request to create Project : {}", project);
        ResponseEntity<?> responseEntity;
        try {
            projectService.create(
                    project.getName(),
                    project.getPurpose(),
                    project.getConcrete(),
                    project.getStartDate(),
                    project.getEndDate(),
                    project.getPropertyTags(),
                    project.getResourceRequirements(),
                    project.getImageId());
            responseEntity = new ResponseEntity<>(HttpStatus.OK);
        } catch(IllegalArgumentException e) {
            log.error("Could not save Project : {}", project, e);
            responseEntity = new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        } catch(OperationForbiddenException e) {
            log.error("Could not save Project : {}", project, e);
            responseEntity = new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
        return responseEntity;
    }

    /**
     * POST  /rest/project -> Create a new project.
     */
    @ApiOperation(value = "Update a project", notes = "Create or update a project")
    @RequestMapping(value = "/rest/projects",
            method = RequestMethod.PUT,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    @RolesAllowed(AuthoritiesConstants.USER)
    public ResponseEntity<?> update(@RequestBody @Valid ProjectRequestDTO project) {
        log.debug("REST request to update Project : {}", project);
        ResponseEntity<?> responseEntity;
        try {
            projectService.update(
                    project.getId(),
                    project.getName(),
                    project.getPurpose(),
                    project.getConcrete(),
                    project.getStartDate(),
                    project.getEndDate(),
                    project.getImageId(),
                    project.getPropertyTags(),
                    project.getResourceRequirements());
            responseEntity = new ResponseEntity<>(HttpStatus.OK);
        } catch(IllegalArgumentException e) {
            log.error("Could not save Project : {}", project, e);
            responseEntity = new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        } catch(OperationForbiddenException e) {
            log.error("Could not save Project : {}", project, e);
            responseEntity = new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
        return responseEntity;
    }

    /**
     * POST  /rest/project/manager -> Change project manager of a project
     */
    @ApiOperation(value = "Change manager", notes = "Change the manager of a project")
    @RequestMapping(value = "/rest/projects/{id}/manager",
            method = RequestMethod.PUT,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    @RolesAllowed(AuthoritiesConstants.USER)
    public ResponseEntity<?> changeManager(@PathVariable Long id, @RequestBody String newManager) {
        log.debug("REST request to change project manager of project {} to {}", id, newManager);
        ResponseEntity<?> responseEntity;
        try {
            projectService.setManager(id, newManager);
            responseEntity = new ResponseEntity<>(HttpStatus.OK);
        } catch(IllegalArgumentException e) {
            log.error("Could not set manager of project {} to {}", id, newManager, e);
            responseEntity = ErrorHelper.buildErrorResponse(ErrorConstants.PROJECTS_NOT_A_VALID_MANAGER);
        } catch(NoSuchUserException e) {
            log.error("Could not set manager of project {} to {}", id, newManager, e);
            responseEntity = ErrorHelper.buildErrorResponse(ErrorConstants.PROJECTS_NO_SUCH_USER);
        } catch (NoSuchProjectException e) {
            log.error("Could not set manager of project {} to {}", id, newManager, e);
            responseEntity = ErrorHelper.buildErrorResponse(ErrorConstants.PROJECTS_NO_SUCH_PROJECT);
        } catch(OperationForbiddenException e) {
            log.error("Could not set manager of project {} to {}", id, newManager, e);
            responseEntity = new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
        return responseEntity;
    }

    /**
     * GET  /rest/project -> get all the projects.
     */
    @ApiOperation(value = "Get projects", notes = "Get projects by name and tags")
    @RequestMapping(value = "/rest/projects",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public List<ProjectResponseDTO> getByNameAndTags(
            @RequestParam(required = false) String filter,
            @RequestParam(required = false) String tags,
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer pageSize,
            @RequestParam(required = false) String fields,
            @RequestParam(required = false) String order) {
        log.debug("REST request to get projects");
        return projectService.findProjects(filter, tags, new RestParameters(page, pageSize, order, fields));
    }

    /**
     * GET  /rest/organizations/{id}/projects -> get all the projects for an organization.
     */
    @ApiOperation(value = "Get projects", notes = "Get projects by organization, name and tags")
    @RequestMapping(value = "/rest/organizations/{organizationId}/projects",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public List<ProjectResponseDTO> getByOrganizationAndNameAndTags(
            @PathVariable Long organizationId,
            @RequestParam(required = false) String filter,
            @RequestParam(required = false) String tags,
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer pageSize,
            @RequestParam(required = false) String fields,
            @RequestParam(required = false) String order) {
        log.debug("REST request to get projects for organization {}", organizationId);
        return projectService.findProjectsFromOrganization(organizationId, filter, tags,
                new RestParameters(page, pageSize, order, fields));
    }

    /**
     * GET  /rest/project/:id -> get the "id" project.
     */
    @ApiOperation(value = "Get project", notes = "Get a project by its id")
    @RequestMapping(value = "/rest/projects/{id}",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<ProjectResponseDTO> get(
            @PathVariable Long id,
            @RequestParam(required = false) String fields) {
        log.debug("REST request to get Project : {}", id);
        return Optional.ofNullable(projectService.findProjectById(id, fields))
            .map(project -> new ResponseEntity<>(
                project,
                HttpStatus.OK))
            .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    /**
     * DELETE  /rest/project/:id -> delete the "id" project.
     */
    @ApiOperation(value = "Delete project", notes = "Delete a project by its id")
    @RequestMapping(value = "/rest/projects/{id}",
            method = RequestMethod.DELETE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @RolesAllowed(AuthoritiesConstants.USER)
    @Timed
    public ResponseEntity<?> delete(@PathVariable Long id) {
        log.debug("REST request to delete Project : {}", id);
        ResponseEntity<?> responseEntity = null;
        try {
            projectService.delete(id);
        } catch(IllegalArgumentException e) {
            log.error("Could not delete project {}", id, e);
            responseEntity = ErrorHelper.buildErrorResponse(ErrorConstants.PROJECTS_NO_SUCH_PROJECT);
        } catch(OperationForbiddenException e) {
            log.error("Could not delete project {}", id, e);
            responseEntity = new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
        return responseEntity;
    }


    @RolesAllowed(AuthoritiesConstants.USER)
    @RequestMapping(value = "/rest/projects/{id}/resourceRequirements",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public List<ResourceRequirementDTO> getAllResourceRequirement(@PathVariable Long id) {
        log.debug("REST request to get all resource requirements belongs to project id:{}", id);
        return this.resourcesService.getAllRequirements(id);
    }

}
