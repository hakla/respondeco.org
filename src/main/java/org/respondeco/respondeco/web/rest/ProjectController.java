package org.respondeco.respondeco.web.rest;

import com.codahale.metrics.annotation.Timed;
import com.wordnik.swagger.annotations.ApiOperation;
import org.respondeco.respondeco.domain.Project;
import org.respondeco.respondeco.domain.ResourceMatch;
import org.respondeco.respondeco.security.AuthoritiesConstants;
import org.respondeco.respondeco.service.RatingService;
import org.respondeco.respondeco.service.ProjectService;
import org.respondeco.respondeco.service.exception.*;
import org.respondeco.respondeco.service.ResourceService;
import org.respondeco.respondeco.service.exception.OperationForbiddenException;
import org.respondeco.respondeco.web.rest.dto.*;
import org.respondeco.respondeco.web.rest.util.ErrorHelper;
import org.respondeco.respondeco.web.rest.util.RestParameters;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import javax.annotation.security.PermitAll;
import javax.annotation.security.RolesAllowed;
import javax.inject.Inject;
import javax.validation.Valid;
import java.util.List;

/**
 * REST controller for managing Project.
 */
@RestController
@Transactional
@RequestMapping("/app")
public class ProjectController {

    private final Logger log = LoggerFactory.getLogger(ProjectController.class);

    private ProjectService projectService;
    private ResourceService resourceService;
    private RatingService ratingService;

    @Inject
    public ProjectController(ProjectService projectService, ResourceService resourceService, RatingService ratingService) {
        this.projectService = projectService;
        this.resourceService = resourceService;
        this.ratingService = ratingService;
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
                project.getLogo() != null ? project.getLogo().getId() : null);
            responseEntity = new ResponseEntity<>(HttpStatus.OK);
        } catch(IllegalValueException e) {
            log.error("Could not save Project : {}", project, e);
            responseEntity = ErrorHelper.buildErrorResponse(e.getInternationalizationKey(), e.getMessage());
        } catch(OperationForbiddenException e) {
            log.error("Could not save Project : {}", project, e);
            responseEntity = new ResponseEntity<>(HttpStatus.FORBIDDEN);
        } catch (ResourceException e) {
            log.error("Could not save Project : {}", project, e);
            responseEntity = new ResponseEntity<>(HttpStatus.BAD_REQUEST);
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
        log.error("REST request to update Project : {}", project);
        ResponseEntity<?> responseEntity;
        try {
            projectService.update(
                    project.getId(),
                    project.getName(),
                    project.getPurpose(),
                    project.getConcrete(),
                    project.getStartDate(),
                    project.getEndDate(),
                    project.getLogo() != null ? project.getLogo().getId() : null,
                    project.getPropertyTags(),
                    project.getResourceRequirements());
            responseEntity = new ResponseEntity<>(HttpStatus.OK);
        } catch(IllegalValueException e) {
            log.error("Could not save Project : {}", project, e);
            responseEntity = ErrorHelper.buildErrorResponse(e.getInternationalizationKey(), e.getMessage());
        } catch(OperationForbiddenException e) {
            log.error("Could not save Project : {}", project, e);
            responseEntity = new ResponseEntity<>(HttpStatus.FORBIDDEN);
        } catch (ResourceException e) {
            log.error("Could not save Project : {}", project, e);
            responseEntity = new ResponseEntity<>(HttpStatus.BAD_REQUEST);
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
        } catch(IllegalValueException e) {
            log.error("Could not set manager of project {} to {}", id, newManager, e);
            responseEntity = ErrorHelper.buildErrorResponse(e.getInternationalizationKey(), e.getMessage());
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
    @PermitAll
    public List<ProjectResponseDTO> getByNameAndTags(
            @RequestParam(required = false) String filter,
            @RequestParam(required = false) String tags,
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer pageSize,
            @RequestParam(required = false) String fields,
            @RequestParam(required = false) String order) {
        log.debug("REST request to get projects");
        RestParameters restParameters = new RestParameters(page, pageSize, order, fields);
        List<Project> projects = projectService.findProjects(filter, tags, restParameters);
        return ProjectResponseDTO.fromEntities(projects, restParameters.getFields());
    }

    /**
     * GET  /rest/organizations/{id}/projects -> get all the projects for an organization.
     */
    @ApiOperation(value = "Get projects", notes = "Get projects by organization, name and tags")
    @RequestMapping(value = "/rest/organizations/{organizationId}/projects",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    @PermitAll
    public List<ProjectResponseDTO> getByOrganizationAndNameAndTags(
            @PathVariable Long organizationId,
            @RequestParam(required = false) String filter,
            @RequestParam(required = false) String tags,
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer pageSize,
            @RequestParam(required = false) String fields,
            @RequestParam(required = false) String order) {
        log.debug("REST request to get projects for organization {}", organizationId);
        RestParameters restParameters = new RestParameters(page, pageSize, order, fields);
        List<Project> projects =  projectService
                .findProjectsFromOrganization(organizationId, filter, tags, restParameters);
        return ProjectResponseDTO.fromEntities(projects, restParameters.getFields());
    }

    /**
     * GET  /rest/project/:id -> get the "id" project.
     */
    @ApiOperation(value = "Get project", notes = "Get a project by its id")
    @RequestMapping(value = "/rest/projects/{id}",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    @PermitAll
    public ResponseEntity<ProjectResponseDTO> get(
            @PathVariable Long id,
            @RequestParam(required = false) String fields) {
        log.debug("REST request to get Project : {}", id);
        Project project = projectService.findProjectById(id);
        ResponseEntity<ProjectResponseDTO> response;
        RestParameters restParameters = new RestParameters(null, null, null, fields);
        if(project != null) {
            ProjectResponseDTO responseDTO = ProjectResponseDTO
                    .fromEntity(project, restParameters.getFields());
            response = new ResponseEntity<>(responseDTO, HttpStatus.OK);
        } else {
            response = new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return response;
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
        } catch(IllegalValueException e) {
            log.error("Could not delete project {}", id, e);
            responseEntity = ErrorHelper.buildErrorResponse(e.getInternationalizationKey(), e.getMessage());
        } catch(OperationForbiddenException e) {
            log.error("Could not delete project {}", id, e);
            responseEntity = new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
        return responseEntity;
    }


    /**
     * Get all resource requirements for a specific project given by project id
     * @param id project id
     * @return list of ResourceRequirements wrapped into DTO
     */
    @RolesAllowed(AuthoritiesConstants.USER)
    @RequestMapping(value = "/rest/projects/{id}/resourceRequirements",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public List<ResourceRequirementRequestDTO> getAllResourceRequirement(@PathVariable Long id) {
        log.debug("REST request to get all resource requirements belongs to project id:{}", id);
        return this.resourceService.getAllRequirements(id);
    }


    /**
     * Get Resource Matches for a specific Project given by id
     * @param id Project id
     * @return List of ResourceMatches wrapped into DTO
     */
    @RolesAllowed(AuthoritiesConstants.USER)
    @RequestMapping(value = "/rest/projects/{id}/resourcematches",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<List<ResourceMatchResponseDTO>> getAllResourceMatchesForProject(@PathVariable Long id) {
        ResponseEntity<List<ResourceMatchResponseDTO>> responseEntity;

        List<ResourceMatch> resourceMatches = resourceService.getResourceMatchesForProject(id);
        if(resourceMatches.isEmpty() == false) {
            List<ResourceMatchResponseDTO> resourceMatchResponseDTO = ResourceMatchResponseDTO.fromEntities(resourceMatches, null);
            responseEntity = new ResponseEntity<>(resourceMatchResponseDTO, HttpStatus.OK);
        } else {
            responseEntity = new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        return responseEntity;
    }

    /**
     * POST  /rest/project/{id}/ratings -> Create a new projectrating.
     */
    /**
    @ApiOperation(value = "Create a projectrating", notes = "Create or update a projectrating")
    @RequestMapping(value = "/rest/projects/{id}/projectratings",
            method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    @RolesAllowed(AuthoritiesConstants.USER)
    public ResponseEntity<?> create(@RequestBody @Valid ProjectRatingRequestDTO projectRatingRequest) {
        log.debug("REST request to create ProjectRating : {}", projectRatingRequest);
        ResponseEntity<?> responseEntity;
        try {
            projectRatingService.createProjectRating(
                    projectRatingRequest.getRating(),
                    projectRatingRequest.getComment(),
                    projectRatingRequest.getProjectId());
            responseEntity = new ResponseEntity<>(HttpStatus.OK);
        } catch(NoSuchProjectException e) {
            log.error("Could not save ProjectRating : {}", projectRatingRequest, e);
            responseEntity = ErrorHelper.buildErrorResponse(e.getInternationalizationKey(),e.getMessage());
        }catch(ProjectRatingException e) {
            log.error("Could not save ProjectRating : {}", projectRatingRequest, e);
            responseEntity = ErrorHelper.buildErrorResponse(e.getInternationalizationKey(),e.getMessage());
        } catch(NotOwnerOfOrganizationException e) {
            log.error("Could not save ProjectRating : {}", projectRatingRequest, e);
            responseEntity = ErrorHelper.buildErrorResponse("rating.error.notowneroforg",e.getMessage());
        } catch(Exception e) {
            log.error("Could not save ProjectRating : {}", projectRatingRequest, e);
            responseEntity = new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return responseEntity;
    }

    /**
     * POST  /rest/project/{id}/projectratings -> Update a projectRating.
     */
    /**
    @ApiOperation(value = "Update a projectRating", notes = "Update a projectRating")
    @RequestMapping(value = "/rest/projects/{id}/projectratings",
            method = RequestMethod.PUT,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    @RolesAllowed(AuthoritiesConstants.USER)
    public ResponseEntity<?> update(@RequestBody @Valid ProjectRatingRequestDTO projectRatingRequest) {
        log.error("REST request to update ProjectRating : {}", projectRatingRequest);
        ResponseEntity<?> responseEntity;
        try {
            projectRatingService.updateProjectRating(
                    projectRatingRequest.getRating(),
                    projectRatingRequest.getComment(),
                    projectRatingRequest.getId());
            responseEntity = new ResponseEntity<>(HttpStatus.OK);
        } catch(NoSuchProjectRatingException e) {
            log.error("Could not update ProjectRating : {}", projectRatingRequest, e);
            responseEntity = new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return responseEntity;
    }

    /**
     * GET  /rest/project/:id -> get the "id" project.
     */
    /**
    @ApiOperation(value = "Get projectRating", notes = "Get a projectRating by its id")
    @RequestMapping(value = "/rest/projects/{id}/projectratings/{id}",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<?> get(
            @PathVariable Long id,
            @RequestParam(required = false) String fields,
            @RequestParam(required = false) Boolean aggregated) {
        log.debug("REST request to get ProjectRating : {}", id);
        ResponseEntity<?> response;
        RestParameters restParameters = new RestParameters(null, null, null, fields);
        if (aggregated == false || aggregated == null) {
            Rating projectRating = projectRatingService.getProjectRating(id);
            if(projectRating != null) {
                RatingResponseDTO responseDTO = RatingResponseDTO
                        .fromEntity(projectRating, restParameters.getFields());
                response = new ResponseEntity<>(responseDTO, HttpStatus.OK);
            } else {
                response = new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
        }
        else {
            AggregatedRating aggregatedRating = projectRatingService.getAggregatedRating(id);
            if(aggregatedRating != null) {
                AggregatedRatingResponseDTO responseDTO = AggregatedRatingResponseDTO
                        .fromEntity(aggregatedRating, restParameters.getFields());
                response = new ResponseEntity<>(responseDTO, HttpStatus.OK);
            } else {
                response = new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
        }
        return response;
    }**/
}
