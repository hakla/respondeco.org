package org.respondeco.respondeco.web.rest;

import com.codahale.metrics.annotation.Timed;
import com.wordnik.swagger.annotations.ApiOperation;
import org.respondeco.respondeco.domain.*;
import org.respondeco.respondeco.security.AuthoritiesConstants;
import org.respondeco.respondeco.service.*;
import org.respondeco.respondeco.service.exception.*;
import org.respondeco.respondeco.service.exception.OperationForbiddenException;
import org.respondeco.respondeco.web.rest.dto.*;
import org.respondeco.respondeco.web.rest.util.ErrorHelper;
import org.respondeco.respondeco.web.rest.util.RestParameters;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import javax.annotation.security.PermitAll;
import javax.annotation.security.RolesAllowed;
import javax.inject.Inject;
import javax.validation.Valid;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * REST controller for managing Project.
 *
 * This REST-Controller handles all requests for /rest/projects
 */
@RestController
@Transactional
@RequestMapping("/app")
public class ProjectController {

    private final Logger log = LoggerFactory.getLogger(ProjectController.class);

    private ProjectService projectService;
    private ResourceService resourceService;
    private RatingService ratingService;
    private UserService userService;
    private ProjectLocationService projectLocationService;
    private PostingFeedService postingFeedService;

    @Inject
    public ProjectController(ProjectService projectService,
                             ResourceService resourceService,
                             RatingService ratingService,
                             UserService userService,
                             PostingFeedService postingFeedService,
                            ProjectLocationService projectLocationService) {

        this.projectService = projectService;
        this.resourceService = resourceService;
        this.ratingService = ratingService;
        this.userService = userService;
        this.projectLocationService = projectLocationService;
        this.postingFeedService = postingFeedService;
    }

    /**
     * Organization that apply new resource to a project
     * @param projectApplyDTO data to apply
     * @return HTPP Status OK: no errors accure, BAD REQUEST: error accures
     */
    @ApiOperation(value = "project apply", notes = "Create a project apply (org donate project)")
    @RolesAllowed(AuthoritiesConstants.USER)
    @RequestMapping(value = "/rest/projects/apply",
        method = RequestMethod.POST,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<?> projectApplyOffer(@RequestBody ProjectApplyDTO projectApplyDTO) {
        log.debug("REST request to projectApplyOffer with dto: {}", projectApplyDTO);
        ResponseEntity<?> responseEntity;
        try {
            ResourceMatch resourceMatch = resourceService.createProjectApplyOffer(
                projectApplyDTO.getResourceOfferId(),
                projectApplyDTO.getResourceRequirementId(),
                projectApplyDTO.getOrganizationId(),
                projectApplyDTO.getProjectId()
            );

            log.debug("Resource Match: {}", resourceMatch);

            responseEntity = new ResponseEntity<>(HttpStatus.CREATED);
        } catch (ResourceNotFoundException e) {
            log.error("Could not save Project apply: {}", projectApplyDTO, e);
            responseEntity = new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }catch (IllegalValueException e){
            log.error("Could not save Project apply: {}", projectApplyDTO, e);
            responseEntity = ErrorHelper.buildErrorResponse(e.getInternationalizationKey(), e.getMessage());
        }

        return responseEntity;
    }

     /**
     * POST  /rest/projects -> Creates a new project from the values sent in the request body.
     *
     * @param project the ProjectRequestDTO containing the values to create a new project
     * @return status CREATED with the newly created project as ProjectResponseDTO, or if the request was not successful,
     * an error response status and a potential error message
     */
    @ApiOperation(value = "Create a project", notes = "Create a new project")
    @RequestMapping(value = "/rest/projects",
            method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    @RolesAllowed(AuthoritiesConstants.USER)
    public ResponseEntity<?> create(@RequestBody @Valid ProjectRequestDTO project) {
        log.debug("REST request to create Project : {}", project);
        ResponseEntity<?> responseEntity;
        try {
            Project newProject = projectService.create(
                project.getName(),
                project.getPurpose(),
                project.getConcrete(),
                project.getStartDate(),
                project.getPropertyTags(),
                project.getResourceRequirements(),
                project.getLogo() != null ? project.getLogo().getId() : null);

            ProjectLocationDTO projectLocationDTO = project.getProjectLocation();

            if(projectLocationDTO != null) {
                projectLocationService.createProjectLocation(newProject.getId(), projectLocationDTO.getAddress(),
                    projectLocationDTO.getLatitude(), projectLocationDTO.getLongitude());
            }

            ProjectResponseDTO responseDTO = ProjectResponseDTO.fromEntity(newProject, null);
            responseEntity = new ResponseEntity<>(responseDTO, HttpStatus.CREATED);
        } catch(IllegalValueException e) {
            log.error("Could not save Project : {}", project, e);
            responseEntity = ErrorHelper.buildErrorResponse(e);
        }
        return responseEntity;
    }

    /**
     * PUT  /rest/projects -> Updates an existing project with new information
     * @param project the new information to save, the project id must be present in order to update the
     *                existing project
     * @return status OK with the updated project as ProjectResponseDTO, or if the request was not successful,
     * an error response status and a potential error message
     */
    @ApiOperation(value = "Update a project", notes = "Update an existing project")
    @RequestMapping(value = "/rest/projects",
            method = RequestMethod.PUT,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    @RolesAllowed(AuthoritiesConstants.USER)
    public ResponseEntity<?> update(@RequestBody @Valid ProjectRequestDTO project) {
        log.error("REST request to update Project : {}", project);
        ResponseEntity<?> responseEntity;
        try {
            Project updatedProject = projectService.update(
                    project.getId(),
                    project.getName(),
                    project.getPurpose(),
                    project.getConcrete(),
                    project.getStartDate(),
                    project.getLogo() != null ? project.getLogo().getId() : null,
                    project.getPropertyTags(),
                    project.getResourceRequirements());

            ProjectLocationDTO projectLocationDTO = project.getProjectLocation();

            if(projectLocationDTO != null) {
                ProjectLocation location = projectLocationService.updateProjectLocation(projectLocationDTO.getProjectId(),
                    projectLocationDTO.getAddress(), projectLocationDTO.getLatitude(), projectLocationDTO.getLongitude());
            }

            ProjectResponseDTO responseDTO = ProjectResponseDTO.fromEntity(updatedProject, null);
            responseEntity = new ResponseEntity<>(responseDTO, HttpStatus.OK);
        } catch(IllegalValueException e) {
            log.error("Could not save Project : {}", project, e);
            responseEntity = ErrorHelper.buildErrorResponse(e);
        }
        return responseEntity;
    }

    /**
     * PUT  /rest/projects/{id}/manager -> Change project manager of a project
     * @param id the id of the project of which to change the manager, given by the REST path
     * @param newManagerId The id of the new project manager
     * @return status OK with the updated project as ProjectResponseDTO, or if the request was not successful,
     * an error response status and a potential error message
     */
    @ApiOperation(value = "Change manager", notes = "Change the manager of a project")
    @RequestMapping(value = "/rest/projects/{id}/manager",
            method = RequestMethod.PUT,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    @RolesAllowed(AuthoritiesConstants.USER)
    public ResponseEntity<?> changeManager(@PathVariable Long id, @RequestBody Long newManagerId) {
        log.debug("REST request to change project manager of project {} to {}", id, newManagerId);
        ResponseEntity<?> responseEntity;
        try {
            Project project = projectService.setManager(id, newManagerId);
            ProjectResponseDTO responseDTO = ProjectResponseDTO.fromEntity(project, null);
            responseEntity = new ResponseEntity<>(responseDTO, HttpStatus.OK);
        } catch(IllegalValueException e) {
            log.error("Could not set manager of project {} to {}", id, newManagerId, e);
            responseEntity = ErrorHelper.buildErrorResponse(e.getInternationalizationKey(), e.getMessage());
        }
        return responseEntity;
    }

    /**
     * GET  /rest/projects -> if not specified otherwise, returns all projects.
     *
     * optional parameters filter and tags are combined via OR, which means that if both parameters are
     * given, projects matching the name OR are associated with one or more tags are returned.
     *
     * page and pageSize work as follows, supposed that there are 50 projects in the database, if page = 2 and
     * pageSize = 15, database entries 16-30 will be returned, the offset and limit can be computed as follows:
     * offset = (page - 1) * pageSize
     * limit = pageSize
     *
     * @param filter optional parameter, if not null or empty, projects containing the filter string in their name
     *               will be returned
     * @param tags optional parameter, if not null or empty, projects which have one or more of these tags
     *             associated with them will be returned
     * @param page optional parameter indicating the page of projects to be returned, works in conjunction with
     *             pageSize, dafault is 1 (first page)
     * @param pageSize optional parameter indicating the size of the pages of projects to be returned
     * @param fields optional parameter indicating the fields of the responses to be returned, if specified, only the
     *               corresponding fields in the response DTO will be set.
     *               example: fields=id,name
     *               response: [{id: 0, name: "example1"}, {id: 1, name: "ex2"}, ...]
     * @param order optional parameter indicating the order of the returned values, orders can be specified as follows:
     *              fieldname: orders the responses by the fieldname ascending,
     *              +fieldname: same as fieldname,
     *              -fieldname: orders the responses by the fieldname descending
     *              example: order=-id,+name orders by id descending and name ascending
     * @return a ProjectPaginationResponseDTO
     */
    @ApiOperation(value = "Get projects", notes = "Get projects by name and tags, " +
        "or get all projects if the two paramters are not given")
    @RequestMapping(value = "/rest/projects",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    @PermitAll
    public ResponseEntity<ProjectPaginationResponseDTO> getByNameAndTags(
            @RequestParam(required = false) String filter,
            @RequestParam(required = false) String tags,
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer pageSize,
            @RequestParam(required = false) String fields,
            @RequestParam(required = false) String order) {
        log.debug("REST request to get projects");
        RestParameters restParameters = new RestParameters(page, pageSize, order, fields);

        Page<Project> resultPage = projectService.findProjects(filter, tags, restParameters);

        ProjectPaginationResponseDTO paginationResponseDTO = ProjectPaginationResponseDTO.createFromPage(resultPage, restParameters.getFields());

        ResponseEntity<ProjectPaginationResponseDTO> responseEntity = new ResponseEntity(paginationResponseDTO, HttpStatus.OK);

        return responseEntity;
    }

    /**
     * GET  /rest/organizations/{id}/projects -> if not specified otherwise, returns all projects from an organization.
     *
     * optional parameters filter and tags are combined via OR, which means that if both parameters are
     * given, projects matching the name OR are associated with one or more tags are returned.
     *
     * page and pageSize work as follows, supposed that there are 50 projects in the database, if page = 2 and
     * pageSize = 15, database entries 16-30 will be returned, the offset and limit can be computed as follows:
     * offset = (page - 1) * pageSize
     * limit = pageSize
     *
     * @param organizationId REST path variable indicating the organization of which to query the projects
     * @param filter optional parameter, if not null or empty, projects containing the filter string in their name
     *               will be returned
     * @param tags optional parameter, if not null or empty, projects which have one or more of these tags
     *             associated with them will be returned
     * @param page optional parameter indicating the page of projects to be returned, works in conjunction with
     *             pageSize, dafault is 1 (first page)
     * @param pageSize optional parameter indicating the size of the pages of projects to be returned
     * @param fields optional parameter indicating the fields of the responses to be returned, if specified, only the
     *               corresponding fields in the response DTO will be set.
     *               example: fields=id,name
     *               response: [{id: 0, name: "example1"}, {id: 1, name: "ex2"}, ...]
     * @param order optional parameter indicating the order of the returned values, orders can be specified as follows:
     *              fieldname: orders the responses by the fieldname ascending,
     *              +fieldname: same as fieldname,
     *              -fieldname: orders the responses by the fieldname descending
     *              example: order=-id,+name orders by id descending and name ascending
     * @return a list of response DTOs matching the given criteria
     */
    @ApiOperation(value = "Get projects", notes = "Get projects by organization, name and tags")
    @RequestMapping(value = "/rest/organizations/{organizationId}/projects",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    @PermitAll
    public ResponseEntity<ProjectPaginationResponseDTO> getByOrganizationAndNameAndTags(
            @PathVariable Long organizationId,
            @RequestParam(required = false) String filter,
            @RequestParam(required = false) String tags,
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer pageSize,
            @RequestParam(required = false) String fields,
            @RequestParam(required = false) String order) {
        log.debug("REST request to get projects for organization {}", organizationId);
        RestParameters restParameters = new RestParameters(page, pageSize, order, fields);
        Page<Project> resultPage =  projectService
                .findProjectsFromOrganization(organizationId, filter, tags, restParameters);

        ProjectPaginationResponseDTO projectPaginationResponseDTO = ProjectPaginationResponseDTO.createFromPage(resultPage, restParameters.getFields());

        ResponseEntity<ProjectPaginationResponseDTO> responseEntity = new ResponseEntity<>(projectPaginationResponseDTO, HttpStatus.OK);

        return responseEntity;
    }

    /**
     * GET  /rest/project/{id} -> get the project with the given id
     * @param id the id of the project ot get
     * @param fields optional parameter indicating the fields of the responses to be returned, if specified, only the
     *               corresponding fields in the response DTO will be set.
     *               example: fields=id,name
     *               response: [{id: 0, name: "example1"}, {id: 1, name: "ex2"}, ...]
     * @return status OK with the specified project as ProjectResponseDTO, or if the project was not found, a
     * NOT_FOUND status with an empty response body
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

            //project location
            ProjectLocation projectLocation = projectLocationService.getProjectLocation(project.getId());
            if(projectLocation != null) {
                ProjectLocationResponseDTO dto = ProjectLocationResponseDTO.fromEntity(projectLocation, null);
                responseDTO.setProjectLocation(dto);
            }

            response = new ResponseEntity<>(responseDTO, HttpStatus.OK);
        } else {
            response = new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return response;
    }

    /**
     * DELETE  /rest/project/{id} -> delete the project with the given id.
     * @param id the id of the project to delete
     * @return status OK if the request was successful, or if the request was not successful,
     * an error response status and a potential error message
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
            responseEntity = new ResponseEntity<>(HttpStatus.OK);
        } catch(IllegalValueException e) {
            log.error("Could not delete project {}", id, e);
            responseEntity = ErrorHelper.buildErrorResponse(e.getInternationalizationKey(), e.getMessage());
        }
        return responseEntity;
    }

    /**
     * Get all resource requirements for a specific project given by project id
     * @param id project id
     * @return list of ResourceRequirements wrapped into DTO
     */
    @RolesAllowed(AuthoritiesConstants.USER)
    @RequestMapping(value = "/rest/projects/{id}/resourcerequirements",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<List<ResourceRequirementResponseDTO>> getAllResourceRequirement(@PathVariable Long id) {
        log.debug("REST request to get all resource requirements belongs to project id:{}", id);
        ResponseEntity<List<ResourceRequirementResponseDTO>> responseEntity;

        List<ResourceRequirement> resourceRequirements = resourceService.getAllRequirements(id);

        List<ResourceRequirementResponseDTO> resourceRequirementResponseDTOs = ResourceRequirementResponseDTO.fromEntities(resourceRequirements, null);

        responseEntity = new ResponseEntity<>(resourceRequirementResponseDTOs, HttpStatus.OK);
        return responseEntity;
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
     * POST  /rest/project/{id}/ratings -> Create a new projectrating for the project with the given id.
     *
     * @param ratingRequestDTO DTO containing the rating
     * @param id REST path variable indicating the project for which the rating is meant
     * @return status OK if the request was successful, or if the request was not successful,
     * an error response status and a potential error message
     */
    @RequestMapping(value = "/rest/projects/{id}/ratings",
            method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    @RolesAllowed(AuthoritiesConstants.USER)
    public ResponseEntity<?> rateProject(
            @RequestBody @Valid RatingRequestDTO ratingRequestDTO,
            @PathVariable Long id) {
        ResponseEntity<?> responseEntity;
        try {
            ratingService.rateProject(id,ratingRequestDTO.getMatchid(),
                ratingRequestDTO.getRating(),ratingRequestDTO.getComment());
            responseEntity = new ResponseEntity<>(HttpStatus.OK);
        } catch (NoSuchProjectException e ) {
            log.error("Could not grate project {}", id, e);
            responseEntity = new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } catch (ProjectRatingException  | NoSuchResourceMatchException | NoSuchOrganizationException e) {
            responseEntity = ErrorHelper.buildErrorResponse(e);
        }
        return responseEntity;
    }

    /**
     * get an aggregated rating for a project, or get a rating permission indicator indicating if the project can be
     * rated by the current user
     * @param id the id of the project in question
     * @param permission flag, if present, check if the current user is allowed to rate the project and return a
     *                   RatingPermissionResponseDTO
     * @return a ResponseEntity containing either and AggregatedRatingResponseDTO or
     * a RatingPermissionResponseDTO object
     */
    @RequestMapping(value = "/rest/projects/{id}/ratings",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    @RolesAllowed(AuthoritiesConstants.USER)
    public ResponseEntity<?> getAggregatedRating(@PathVariable Long id,
                                                 @RequestParam(required = false) String permission,
                                                 @RequestParam(required = false) List<Long> matches) {
        ResponseEntity<?> responseEntity;
        if(permission != null) {
            if("matches".equals(permission)) {
                List<RatingPermission> permissions = null;
                try {
                    permissions = ratingService.checkPermissionsForMatches(matches);
                    List<RatingPermissionResponseDTO> responseDTOs = RatingPermissionResponseDTO.fromEntities(permissions);
                    responseEntity = new ResponseEntity<>(responseDTOs, HttpStatus.OK);
                } catch (NoSuchResourceMatchException e) {
                    responseEntity = ErrorHelper.buildErrorResponse(e);
                }
            } else {
                try {
                    RatingPermission ratingPermission = ratingService.checkPermissionForProject(id);
                    RatingPermissionResponseDTO responseDTO = RatingPermissionResponseDTO.fromEntity(ratingPermission);
                    responseEntity = new ResponseEntity<>(Arrays.asList(responseDTO), HttpStatus.OK);
                } catch (NoSuchProjectException e) {
                    responseEntity = new ResponseEntity<>(HttpStatus.NOT_FOUND);
                }
            }
        } else {
            AggregatedRating aggregatedRating = ratingService.getAggregatedRatingByProject(id);
            AggregatedRatingResponseDTO aggregatedRatingResponseDTO = AggregatedRatingResponseDTO
                .fromEntity(aggregatedRating, null);
            responseEntity = new ResponseEntity<>(aggregatedRatingResponseDTO, HttpStatus.OK);
        }
        return responseEntity;
    }

    /**
     * Checks if the currently authenticated user is allowed to edit a project
     * @return
     */
    @RequestMapping(value = "/rest/projects/{id}/editable",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @RolesAllowed(AuthoritiesConstants.USER)
    public ResponseEntity isEditable(@PathVariable Long id) {
        // Forbidden if the user is not allowed to edit --> return 403
        ResponseEntity responseEntity;

        try {
            // check if the project is editable by the authenticated user
            if (projectService.isEditable(id)) {
                // and return a 200 if the user is allowed to edit
                responseEntity = new ResponseEntity(HttpStatus.OK);
            } else {
                // and return a 403 code if the user is not allowed to edit
                responseEntity = new ResponseEntity(HttpStatus.FORBIDDEN);
            }
        } catch (NullPointerException e) {
            // No project found for the given id
            responseEntity = new ResponseEntity(HttpStatus.NOT_FOUND);
        }

        return responseEntity;
    }

    @RequestMapping(value = "/rest/projects/{id}/started",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @RolesAllowed(AuthoritiesConstants.USER)
    public void isStarted(@PathVariable Long id) {
        try {
            projectService.checkProjectsToStart();
        } catch (Exception e) {

        }
    }

    /**
     * Get the Follow State for the current Project given by ID
     * @return Error or OK Response Entity {true/false} as result
     */
    @RequestMapping(value = "/rest/projects/{id}/followingstate",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @RolesAllowed(AuthoritiesConstants.USER)
    public ResponseEntity followingState(@PathVariable Long id) {
        FollowStateDTO result = new FollowStateDTO();
        result.setState(projectService.followingState(id));
        return new ResponseEntity(result, HttpStatus.OK);
    }

    /**
     * Allow user to subscribe a specific project news
     * @return Error or OK Response Entity
     */
    @RequestMapping(value = "/rest/projects/{id}/follow",
        method = RequestMethod.POST,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @RolesAllowed(AuthoritiesConstants.USER)
    public ResponseEntity follow(@PathVariable Long id) {
        ResponseEntity responseEntity;

        try {
            projectService.follow(id);
            responseEntity = new ResponseEntity(HttpStatus.CREATED);
        }
        catch (IllegalValueException e){
            responseEntity = ErrorHelper.buildErrorResponse(e);
        }

        return responseEntity;
    }

    /**
     * Allow user to un-subscribe a specific project news
     * @return Error or OK Response Entity
     */
    @RequestMapping(value = "/rest/projects/{id}/unfollow",
        method = RequestMethod.DELETE,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @RolesAllowed(AuthoritiesConstants.USER)
    public ResponseEntity unfollow(@PathVariable Long id) {
        ResponseEntity responseEntity;

        try {
            projectService.unfollow(id);
            responseEntity = new ResponseEntity(HttpStatus.OK);
        }
        catch (IllegalValueException e){
            responseEntity = ErrorHelper.buildErrorResponse(e);
        }

        return responseEntity;
    }
    /**
     * Returns all Project Locations
     * @return ResponseEntity containing a List of ProjectLocationResponse DTOs
     */
    @RequestMapping(value = "/rest/locations",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @RolesAllowed(AuthoritiesConstants.USER)
    public ResponseEntity<List<ProjectLocationResponseDTO>> getAllProjectLocations() {

        List<ProjectLocation> projectLocations = projectLocationService.getAllLocations();

        List<ProjectLocationResponseDTO> projectLocationResponseDTOs = ProjectLocationResponseDTO.fromEntities(projectLocations, null);

        return new ResponseEntity<>(projectLocationResponseDTOs, HttpStatus.OK);
    }


    /**
     * Return projects which are in a specific radius from the given coordinates
     * @return ResponseEntity which contains a list of ProjectLocationResponseDTOs
     */
    @RequestMapping(value = "/rest/nearprojects",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @RolesAllowed(AuthoritiesConstants.USER)
    public ResponseEntity<?> findProjectsNearMe(
        @RequestParam(required = true) Double latitude,
        @RequestParam(required = true) Double longitude,
        @RequestParam(required = true) Double radius) {
        log.debug("REST Request to get near projects: (latitude: " + latitude +" , longitude: " + longitude + "), radius: " + radius );

        ResponseEntity<?> responseEntity = null;

        try{
            List<ProjectLocation> projects = projectLocationService.getNearProjects(latitude, longitude, radius);
            responseEntity = new ResponseEntity<List<ProjectLocationResponseDTO>>(ProjectLocationResponseDTO.fromEntities(projects, null), HttpStatus.OK);
        } catch (NoSuchProjectException e) {
            log.debug("Can not find project for projectLocation");
            responseEntity = new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } catch (IllegalValueException e) {
            responseEntity = ErrorHelper.buildErrorResponse(e.getInternationalizationKey(), e.getMessage());
        }

        return responseEntity;
    }


    /**
     * gents the list of postings ordered by creation date for the specified project
     *
     * @param id the id of the project for which to get the postings
     * @return response status OK and the Postings for the project
     */
    @RequestMapping(value = "/rest/projects/{id}/postings",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    @RolesAllowed(AuthoritiesConstants.USER)
    public ResponseEntity<?> getPostings(@PathVariable Long id,
                                                        @RequestParam(required = false) Integer page,
                                                        @RequestParam(required = false) Integer pageSize) {
        RestParameters restParameters = new RestParameters(page, pageSize);
        ResponseEntity<PostingPaginationResponseDTO> responseEntity;
        PostingPaginationResponseDTO responseDTO = new PostingPaginationResponseDTO();
        List<PostingDTO> postings = new ArrayList<>();
        try {
            Page<Posting> currentPage = postingFeedService.getPostingsForProject(id, restParameters);
            for(Posting posting : currentPage.getContent()){
                postings.add(new PostingDTO(posting));
            }
            responseDTO.setTotalElements(currentPage.getTotalElements());
            responseDTO.setPostings(postings);
            responseEntity = new ResponseEntity<>(responseDTO, HttpStatus.OK);
        } catch (NoSuchProjectException e) {
            log.error("Could not get postings for project {}", id, e);
            responseEntity = new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return responseEntity;
    }

    /**
     * creates a post for the organization in the postingfeed
     * @param information the string which contains the informaiton of the posting
     * @param id the id of the organization for which to create the posting
     * @return response status ok if posting has
     */
    @RequestMapping(value = "/rest/projects/{id}/postings",
            method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    @RolesAllowed(AuthoritiesConstants.USER)
    public ResponseEntity<?> postingForProject(
            @RequestBody String information,
            @PathVariable Long id) {
        ResponseEntity<?> responseEntity;
        try {
            postingFeedService.addPostingForProjects(id, information);

            responseEntity = new ResponseEntity<>(HttpStatus.OK);
        } catch (NoSuchProjectException e) {
            log.error("Could not post for project {}", id, e);
            responseEntity = new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } catch (PostingFeedException e) {
            responseEntity = ErrorHelper.buildErrorResponse(e);
        }
        return responseEntity;
    }

    /**
     * deletes posting by setting active flag false
     * @param pid posting id to find in repository
     * @param id project id for path completeness
     * @return ok if posting has been deleted; bad request if not
     */
    @RequestMapping(value = "/rest/projects/{id}/postings/{pid}",
            method = RequestMethod.DELETE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    @RolesAllowed(AuthoritiesConstants.USER)
    public ResponseEntity<?> deletePostingForProject(
            @PathVariable Long id,
            @PathVariable Long pid) {
        ResponseEntity<?> responseEntity;
        try {
            postingFeedService.deletePosting(pid);

            responseEntity = new ResponseEntity<>(HttpStatus.OK);
        } catch (PostingException e) {
            responseEntity = ErrorHelper.buildErrorResponse(e);
        }
        return responseEntity;
    }
}
