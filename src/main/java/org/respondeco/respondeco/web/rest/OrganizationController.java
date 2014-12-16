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
import org.respondeco.respondeco.web.rest.util.RestUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.annotation.security.PermitAll;
import javax.annotation.security.RolesAllowed;
import javax.inject.Inject;
import javax.transaction.Transactional;
import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * REST controller for managing Organization.
 */
@RestController
@RequestMapping("/app")
@Transactional
public class OrganizationController {

    private final Logger log = LoggerFactory.getLogger(OrganizationController.class);

    private ResourceService resourceService;
    private OrganizationService organizationService;
    private UserService userService;
    private OrgJoinRequestService orgJoinRequestService;
    private RatingService ratingService;

    @Inject
    public OrganizationController (OrganizationService organizationService,
                                   UserService userService,
                                   ResourceService resourceService,
                                   OrgJoinRequestService orgJoinRequestService,
                                   RatingService ratingService) {
        this.organizationService = organizationService;
        this.userService = userService;
        this.resourceService = resourceService;
        this.orgJoinRequestService = orgJoinRequestService;
        this.ratingService = ratingService;
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
                newOrganization.getLogo());

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
     * GET  /rest/organizations -> get all the organizations.
     */
    @PermitAll
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
    @PermitAll
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
     * POST  /rest/organizations -> Update an organization.
     */
    @RolesAllowed(AuthoritiesConstants.USER)
    @RequestMapping(value = "/rest/organizations",
            method = RequestMethod.PUT,
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
                    organization.getLogo());
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

    @PermitAll
    @RequestMapping(value = "/rest/organizations/{id}/resourceoffers",
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


    @RolesAllowed(AuthoritiesConstants.USER)
    @RequestMapping(value = "/rest/organizations/{id}/resourcerequests",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<List<ResourceMatchResponseDTO>> getAllResourceRequests(
        @PathVariable Long id,
        @RequestParam(required = false) Integer page,
        @RequestParam(required = false) Integer pageSize,
        @RequestParam(required = false) String fields,
        @RequestParam(required = false) String order) {

        log.debug("REST request to get all resource claim requests for organization with id " + id);
        ResponseEntity<List<ResourceMatchResponseDTO>> responseEntity = new ResponseEntity<>(new ArrayList<>(), HttpStatus.OK);

        RestParameters restParameters = new RestParameters(page, pageSize, order, fields);
        List<ResourceMatch> resourceClaims = resourceService.getResourceRequestsForOrganization(id, restParameters);

        for(ResourceMatch match : resourceClaims) {
            ResourceMatchResponseDTO resourceDTO = new ResourceMatchResponseDTO();

            OrganizationResponseDTO organizationDTO = OrganizationResponseDTO.fromEntity(match.getOrganization(), null);
            ProjectResponseDTO projectDTO = ProjectResponseDTO.fromEntity(match.getProject(), null);
            ResourceOfferDTO resourceOfferDTO = new ResourceOfferDTO(match.getResourceOffer());
            ResourceRequirementResponseDTO resourceRequirementResponseDTO = ResourceRequirementResponseDTO.fromEntity(match.getResourceRequirement(), null);

            resourceDTO.setProject(projectDTO);
            resourceDTO.setResourceOffer(resourceOfferDTO);
            resourceDTO.setResourceRequirement(resourceRequirementResponseDTO);
            resourceDTO.setMatchId(match.getId());

            responseEntity.getBody().add(resourceDTO);
        }

        return responseEntity;
    }




    /**
     * GET  /rest/organizations/:id/orgJoinRequests -> get the orgjoinrequests for organization :id
     */
    @RolesAllowed(AuthoritiesConstants.USER)
    @RequestMapping(value = "/rest/organizations/{id}/orgjoinrequests",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed

    public ResponseEntity<?> getByOrgId(@PathVariable Long id){
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
    ResponseEntity<List<UserDTO>> getMembers(@PathVariable Long id) {
        log.debug("REST request to get Users by OrgId : {}", id);
        ResponseEntity<List<UserDTO>> responseEntity;

        try {
            // get all users for organization with id :id
            List<User> users = organizationService.getUserByOrgId(id);
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
     * POST  /rest/deleteMember-> delete Member by userlogin
     */
    @RequestMapping(value = "/rest/organization/{id}/members/{userId}",
            method = RequestMethod.DELETE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    @RolesAllowed(AuthoritiesConstants.USER)
    public ResponseEntity<?> deleteMember(@PathVariable Long id, @PathVariable Long userId) {
        log.debug("REST request to delete Member : {}", userId);
        ResponseEntity<?> responseEntity;
        try {
            organizationService.deleteMember(userId);
            responseEntity = new ResponseEntity<>(HttpStatus.OK);
        } catch (NoSuchUserException e) {
            log.error("Could not delete Member : {}", userId, e);
            responseEntity = new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } catch (NoSuchOrganizationException e) {
            log.error("Could not delete Member : {}", userId, e);
            responseEntity = new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } catch (NotOwnerOfOrganizationException e) {
            log.error("Could not delete Member : {}", userId, e);
            responseEntity = new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return responseEntity;
    }

    /**
     * GET  /rest/users -> get all users
     */
    @RequestMapping(value = "/rest/organizations/{id}/invitableusers",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    @RolesAllowed(AuthoritiesConstants.USER)
    public ResponseEntity<List<UserDTO>> getInvitableUsers(@PathVariable Long id) {
        List<UserDTO> users = new ArrayList<>();

        for (User user: organizationService.findInvitableUsersByOrgId(id)) {
            users.add(new UserDTO(user));
        }

        return new ResponseEntity<List<UserDTO>>(users, HttpStatus.OK);
    }

    @RequestMapping(value = "/rest/organizations/{id}/ratings",
            method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    @RolesAllowed(AuthoritiesConstants.USER)
    public ResponseEntity<?> rateOrganization(
            @RequestBody @Valid RatingRequestDTO ratingRequestDTO,
            @PathVariable Long id) {
        ResponseEntity<?> responseEntity;
        try {
            ratingService.rateOrganization(id, ratingRequestDTO.getMatchid(),
                    ratingRequestDTO.getRating(),
                    ratingRequestDTO.getComment());
            responseEntity = new ResponseEntity<>(HttpStatus.OK);
        } catch (NoSuchOrganizationException e) {
            log.error("Could not grate organization {}", id, e);
            responseEntity = new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } catch (NoSuchProjectException | NoSuchResourceMatchException | SupporterRatingException e) {
            responseEntity = ErrorHelper.buildErrorResponse(e);
        }
        return responseEntity;
    }

    @RequestMapping(value = "/rest/organizations/{id}/ratings",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    @RolesAllowed(AuthoritiesConstants.USER)
    public ResponseEntity<?> getAggregatedRating(@PathVariable Long id,
                                                 @RequestParam(required = false) String permission) {
        ResponseEntity<?> responseEntity;

        AggregatedRating aggregatedRating = ratingService.getAggregatedRatingByOrganization(id);
        AggregatedRatingResponseDTO aggregatedRatingResponseDTO = AggregatedRatingResponseDTO
            .fromEntity(aggregatedRating, null);
        responseEntity = new ResponseEntity<>(aggregatedRatingResponseDTO, HttpStatus.OK);

        return responseEntity;
    }
}
