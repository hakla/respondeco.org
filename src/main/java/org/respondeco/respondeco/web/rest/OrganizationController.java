package org.respondeco.respondeco.web.rest;

import com.codahale.metrics.annotation.Timed;
import com.wordnik.swagger.annotations.ApiOperation;
import org.respondeco.respondeco.domain.*;
import org.respondeco.respondeco.security.AuthoritiesConstants;
import org.respondeco.respondeco.service.*;
import org.respondeco.respondeco.service.exception.*;
import org.respondeco.respondeco.web.rest.dto.*;
import org.respondeco.respondeco.web.rest.util.ErrorHelper;
import org.respondeco.respondeco.web.rest.util.RestParameters;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.annotation.security.RolesAllowed;
import javax.inject.Inject;
import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * REST controller for managing Organization.
 */
@RestController
@RequestMapping("/app")
public class OrganizationController {

    private final Logger log = LoggerFactory.getLogger(OrganizationController.class);

    private ResourceService resourceService;
    private OrganizationService organizationService;
    private UserService userService;
    private OrgJoinRequestService orgJoinRequestService;
    private SupporterRatingService supporterRatingService;


    @Inject
    public OrganizationController (OrganizationService organizationService, UserService userService, ResourceService resourceService, OrgJoinRequestService orgJoinRequestService, SupporterRatingService supporterRatingService) {
        this.organizationService = organizationService;
        this.userService = userService;
        this.resourceService = resourceService;
        this.orgJoinRequestService = orgJoinRequestService;
        this.supporterRatingService = supporterRatingService;
    }

    /**
     * POST  /rest/organizations -> Create a new organization.
     */
    @RolesAllowed(AuthoritiesConstants.USER)
    @RequestMapping(value = "/rest/organizations",
            method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<?> create(@RequestBody @Valid OrganizationRequestDTO newOrganization){
        log.debug("REST request to save Organization : {}", newOrganization);
        ResponseEntity<?> responseEntity;
        try {
            Organization organization = organizationService.createOrganizationInformation(
                newOrganization.getName(),
                newOrganization.getDescription(),
                newOrganization.getEmail(),
                newOrganization.isNpo(),
                newOrganization.getLogo() != null ? newOrganization.getLogo().getId() : null);

            responseEntity = new ResponseEntity<>(HttpStatus.OK);
        } catch (AlreadyInOrganizationException e) {
            log.error("Could not save Organization : {}", newOrganization, e);
            responseEntity = new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        } catch (OrganizationAlreadyExistsException e) {
            log.error("Could not save Organization : {}", newOrganization, e);
            responseEntity = new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        return  responseEntity;
    }

    /**
     * GET  /rest/project -> get all the projects.
     */
    @ApiOperation(value = "Get organizations", notes = "Get organizations")
    @RequestMapping(value = "/rest/organizations",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public List<OrganizationResponseDTO> getAll(
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer pageSize,
            @RequestParam(required = false) String fields,
            @RequestParam(required = false) String order) {
        log.debug("REST request to get organizations");
        RestParameters restParameters = new RestParameters(page, pageSize, order, fields);
        List<Organization> organizations = organizationService.getOrganizations();
        return OrganizationResponseDTO.fromEntities(organizations, restParameters.getFields());
    }

    /**
     * GET  /rest/organization/:id -> get the "id" organization.
     */
    @ApiOperation(value = "Get organization", notes = "Get a organization by its id")
    @RequestMapping(value = "/rest/organizations/{id}",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<OrganizationResponseDTO> get(
            @PathVariable Long id,
            @RequestParam(required = false) String fields) {
        log.debug("REST request to get Organization : {}", id);
        Organization organization = organizationService.getOrganization(id);
        ResponseEntity<OrganizationResponseDTO> response;
        RestParameters restParameters = new RestParameters(null, null, null, fields);
        if(organization != null) {
            OrganizationResponseDTO responseDTO = OrganizationResponseDTO
                    .fromEntity(organization, restParameters.getFields());
            response = new ResponseEntity<>(responseDTO, HttpStatus.OK);
        } else {
            response = new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return response;
    }

    /**
     * GET  /rest/organizations/:owner -> get the organization of current owner.
     */
    @RolesAllowed(AuthoritiesConstants.USER)
    @RequestMapping(value = "/rest/organizations/myorganization",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<OrganizationResponseDTO> getByOwner(
            @RequestParam(required = false) String fields) {
        log.debug("REST request to get owner's Organization : {}");
        Organization organization = organizationService.getOrganizationByOwner();
        ResponseEntity<OrganizationResponseDTO> response;
        RestParameters restParameters = new RestParameters(null, null, null, fields);
        if(organization != null) {
            OrganizationResponseDTO responseDTO = OrganizationResponseDTO
                    .fromEntity(organization, restParameters.getFields());
            response = new ResponseEntity<>(responseDTO, HttpStatus.OK);
        } else {
            response = new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return response;
    }

    /**
     * POST  /rest/organizations -> Update an organization.
     */
    @RolesAllowed(AuthoritiesConstants.USER)
    @RequestMapping(value = "/rest/organizations/updateOrganization",
            method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<?> update(@RequestBody @Valid OrganizationRequestDTO organization){
        log.debug("REST request to update Organization : {}", organization);
        ResponseEntity<?> responseEntity;
        try {
            organizationService.updaterOrganizationInformation(
                    organization.getName(),
                    organization.getDescription(),
                    organization.getEmail(),
                    organization.isNpo(),
                    organization.getLogo().getId());
            responseEntity = new ResponseEntity<>(HttpStatus.OK);
        } catch (NoSuchOrganizationException e) {
            log.error("Could not update Organization : {}", organization, e);
            responseEntity = new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return responseEntity;
    }

    /**
     * DELETE  /rest/organizations/:organization -> delete the "organization" of which the currently logged in user is the owner
     */
    @RolesAllowed(AuthoritiesConstants.USER)
    @RequestMapping(value = "/rest/organizations",
            method = RequestMethod.DELETE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<?> delete(){
        log.debug("REST request to delete Organization : {}");
        ResponseEntity<?> responseEntity;
        try {
            organizationService.deleteOrganizationInformation();
            responseEntity = new ResponseEntity<>(HttpStatus.OK);
        } catch (NoSuchOrganizationException e) {
            log.error("Could not delete Organization : {}", e);
            responseEntity = new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return responseEntity;
    }

    /*
     * GET /rest/organizations/:id/resourceOffers -> get the resourceOffers for the organization :id
     */

    @RolesAllowed(AuthoritiesConstants.USER)
    @RequestMapping(value = "/rest/organizations/{id}/resourceOffers",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public List<ResourceOfferDTO> getAllResourceOffer(@PathVariable Long id) {
        ResponseEntity<Organization> responseEntity;
        OrganizationRequestDTO organizationRequestDTO;

        log.debug("REST request to get all resource offer belongs to Organization id: {}", id);

        List<ResourceOfferDTO> resourceOfferDTOs = this.resourceService.getAllOffers(id);

        return  resourceOfferDTOs;
    }

    /**
     * GET  /rest/organizations/:id/orgJoinRequests -> get the orgjoinrequests for organization :id
     */
    @RolesAllowed(AuthoritiesConstants.ADMIN)
    @RequestMapping(value = "/rest/organizations/{id}/orgJoinRequests",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed

    public ResponseEntity<List<OrgJoinRequestDTO>> getByOrgId(@PathVariable Long id){
        log.debug("REST request to get OrgJoinRequest : {}", id);
        ResponseEntity<List<OrgJoinRequestDTO>> responseEntity = new ResponseEntity<>(new ArrayList<>(), HttpStatus.OK);
        try {
            List<OrgJoinRequest> orgJoinRequests = orgJoinRequestService.getOrgJoinRequestByOrganization(id);

            if (orgJoinRequests == null) {
                responseEntity = new ResponseEntity<>(HttpStatus.NOT_FOUND);
            } else {
                for (OrgJoinRequest orgJoinRequest: orgJoinRequests) {
                    responseEntity.getBody().add(new OrgJoinRequestDTO(orgJoinRequest));
                }
            }
        } catch (NoSuchOrganizationException e) {
            log.error("Could not find OrgJoinRequest : {}", e);
            responseEntity = new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return responseEntity;
    }

    /**
     * GET  /rest/organizations/:id/members -> get the members for the organization :id
     */
    @RequestMapping(value = "/rest/organizations/{id}/members",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    @RolesAllowed(AuthoritiesConstants.USER)
    ResponseEntity<List<UserDTO>> getUserByOrgId(@PathVariable Long id) {
        log.debug("REST request to get Users by OrgId : {}", id);
        ResponseEntity<List<UserDTO>> responseEntity;

        try {
            // get all users for organization with id :id
            List<User> users = userService.getUserByOrgId(id);
            List<UserDTO> userDTOs = new ArrayList<>();

            if (users.isEmpty()) {
                // if there are no members then return 404
                responseEntity = new ResponseEntity<>(HttpStatus.NOT_FOUND);
            } else {
                // map found users and http status 200
                users.forEach(p -> userDTOs.add(new UserDTO(p)));
                responseEntity = new ResponseEntity<>(userDTOs, HttpStatus.OK);
            }
        } catch (NoSuchOrganizationException e) {
            // if the organization with :id couldn't be found
            log.error("Could not get User by Organization : {}", id, e);
            responseEntity = new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        return responseEntity;
    }

    /**
     * POST  /rest/project/{id}/ratings -> Create a new projectrating.
     */
    @ApiOperation(value = "Create a supporterrating", notes = "Create or update a supporterrating")
    @RequestMapping(value = "/rest/organizations/{id}/supporterratings",
            method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    @RolesAllowed(AuthoritiesConstants.USER)
    public ResponseEntity<?> create(@RequestBody @Valid SupporterRatingRequestDTO supporterRatingRequest) {
        log.debug("REST request to create SupporterRating : {}", supporterRatingRequest);
        ResponseEntity<?> responseEntity;
        try {
            supporterRatingService.createSupporterRating(
                    supporterRatingRequest.getRating(),
                    supporterRatingRequest.getComment(),
                    supporterRatingRequest.getProjectId(),
                    supporterRatingRequest.getOrganizationId());
            responseEntity = new ResponseEntity<>(HttpStatus.OK);
        } catch(SupporterRatingException e) {
            log.error("Could not save ProjectRating : {}", supporterRatingRequest, e);
            responseEntity = ErrorHelper.buildErrorResponse(e.getInternationalizationKey(), e.getMessage());
        } catch(NoSuchOrganizationException e) {
            log.error("Could not save ProjectRating : {}", supporterRatingRequest, e);
            responseEntity = new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        } catch(NoSuchProjectException e) {
            log.error("Could not save ProjectRating : {}", supporterRatingRequest, e);
            responseEntity = new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        } catch(Exception e) {
            log.error("Could not save ProjectRating : {}", supporterRatingRequest, e);
            responseEntity = new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return responseEntity;
    }

    /**
     * POST  /rest/organizations/{id}/supporterratings -> Update a supporterRating.
     */
    @ApiOperation(value = "Update a supporterRating", notes = "Update a suppoerterRating")
    @RequestMapping(value = "/rest/organizations/{id}/supporterratings",
            method = RequestMethod.PUT,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    @RolesAllowed(AuthoritiesConstants.USER)
    public ResponseEntity<?> update(@RequestBody @Valid SupporterRatingRequestDTO supporterRatingRequest) {
        log.error("REST request to update SupporterRating : {}", supporterRatingRequest);
        ResponseEntity<?> responseEntity;
        try {
            supporterRatingService.updateSupporterRating(
                    supporterRatingRequest.getRating(),
                    supporterRatingRequest.getComment(),
                    supporterRatingRequest.getId());
            responseEntity = new ResponseEntity<>(HttpStatus.OK);
        } catch(NoSuchSupporterRatingException e) {
            log.error("Could not update ProjectRating : {}", supporterRatingRequest, e);
            responseEntity = new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return responseEntity;
    }

    /**
     * GET  /rest/organizations/{id}/supporterratings-> get the "id" supporterRating.
     */
    @ApiOperation(value = "Get supporterRating", notes = "Get a supporterRating by its id")
    @RequestMapping(value = "/rest/organizations/{id}/supporterratings",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<?> get(
            @PathVariable Long id,
            @RequestParam(required = true) Long projectId,
            @RequestParam(required = false) String fields,
            @RequestParam(required = false) Boolean aggregated) {
        log.debug("REST request to get ProjectRating : {}", id);
        ResponseEntity<?> response;
        RestParameters restParameters = new RestParameters(null, null, null, fields);
        if (aggregated == false || aggregated == null) {
            SupporterRating supporterRating = supporterRatingService.getSupporterRating(id,projectId);
            if(supporterRating != null) {
                SupporterRatingResponseDTO responseDTO = SupporterRatingResponseDTO
                        .fromEntity(supporterRating, restParameters.getFields());
                response = new ResponseEntity<>(responseDTO, HttpStatus.OK);
            } else {
                response = new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
        }
        else {
            AggregatedRating aggregatedRating = supporterRatingService.getAggregatedRating(id);
            if(aggregatedRating != null) {
                AggregatedRatingResponseDTO responseDTO = AggregatedRatingResponseDTO
                        .fromEntity(aggregatedRating, restParameters.getFields());
                response = new ResponseEntity<>(responseDTO, HttpStatus.OK);
            } else {
                response = new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
        }
        return response;
    }
}
