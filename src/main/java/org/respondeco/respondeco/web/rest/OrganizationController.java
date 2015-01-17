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
import org.springframework.data.domain.Page;
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
    private PostingFeedService postingFeedService;

    @Inject
    public OrganizationController (OrganizationService organizationService,
                                   UserService userService,
                                   ResourceService resourceService,
                                   OrgJoinRequestService orgJoinRequestService,
                                   RatingService ratingService,
                                   PostingFeedService postingFeedService) {
        this.organizationService = organizationService;
        this.userService = userService;
        this.resourceService = resourceService;
        this.orgJoinRequestService = orgJoinRequestService;
        this.ratingService = ratingService;
        this.postingFeedService = postingFeedService;
    }

    /**
     * POST  /rest/organizations -> Create a new organization.
     *
     * creates a new organization with the given information
     *
     * @param newOrganization the values for the new organization
     * @return response status CREATED if the organization was created successfully, or BAD REQUEST if the request
     * could not be executed without errors
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
            responseEntity = ErrorHelper.buildErrorResponse("organization.error.alreadyInOrganization", "You're already in an organization. You can't be in an organization and create a new organization (at least not in respondeco)");
        } catch (OrganizationAlreadyExistsException e) {
            log.error("Could not save Organization : {}", newOrganization, e);
            responseEntity = ErrorHelper.buildErrorResponse("organization.error.alreadyExists", "You're already the owner of an organization. You can't be the owner of more than one organization (at least not in respondeco)");
        }
        return  responseEntity;
    }

    /**
     * GET  /rest/organizations -> get all the organizations.
     *
     * by default, get the first 20 of all the organizations, the returned organizations can be filtered via the
     * input parameters
     *
     * page and pageSize work as follows, supposed that there are 50 projects in the database, if page = 2 and
     * pageSize = 15, database entries 16-30 will be returned, the offset and limit can be computed as follows:
     * offset = (page - 1) * pageSize
     * limit = pageSize
     *
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
     * @return a list of matching organization DTOs
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
     *
     * @param id the id of the organization to return
     * @param fields optional parameter indicating the fields of the responses to be returned, if specified, only the
     *               corresponding fields in the response DTO will be set.
     *               example: fields=id,name
     *               response: [{id: 0, name: "example1"}, {id: 1, name: "ex2"}, ...]
     * @return response status OK and an organization DTO if the organization was found, response status NOT FOUND if
     * an organization with the given id does not exist
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
     * PUT  /rest/organizations -> Update an organization.
     *
     * update an existing organization with the new information
     *
     * @param organization new values for the organization, id must not be null
     * @return response status OK and the updated organization if the update was successful, NOT FOUND if an
     * organization with the given id could not be found or BAD REQUEST if the update was not executed successfully
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
            organizationService.updateOrganizationInformation(
                organization.getName(),
                organization.getDescription(),
                organization.getEmail(),
                organization.isNpo(),
                organization.getLogo());
            responseEntity = new ResponseEntity<>(HttpStatus.OK);
        } catch (NoSuchOrganizationException e) {
            log.error("Could not update Organization : {}", organization, e);
            responseEntity = new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } catch(IllegalArgumentException e) {
            log.error("Could not update Organization : {}", organization, e);
            responseEntity = new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        return responseEntity;
    }

    /**
     * DELETE  /rest/organizations/:organization
     *      -> delete the "organization" of which the currently logged in user is the owner
     *
     * @return response status OK if the organization was deleted successfully, or NOT FOUND if the user is not
     * the owner of an existing organization
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

    /**
     * GET /rest/organizations/{id}/resourceoffers -> get the resourceOffers for the organization {id}
     *
     * Returns all ResourceOffers created by a specific Organzation given by id
     *
     * @param id the id of the organization
     * @return ResponseEntity which contains a list of ResourceOfferResponseDTOs and a HTTP Status
     */
    @PermitAll
    @RequestMapping(value = "/rest/organizations/{id}/resourceoffers",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<List<ResourceOfferResponseDTO>> getAllResourceOffers(@PathVariable Long id) {
        ResponseEntity<List<ResourceOfferResponseDTO>> responseEntity;

        log.debug("REST request to get all resource offers which belong to Organization id: {}", id);

        List<ResourceOffer> resourceOffers = this.resourceService.getAllOffers(id);
        List<ResourceOfferResponseDTO> resourceOfferResponseDTOs = ResourceOfferResponseDTO.fromEntities(resourceOffers, null);

        responseEntity = new ResponseEntity<>(resourceOfferResponseDTOs, HttpStatus.OK);

        return responseEntity;
    }


    /**
     * get resource requests which were directed at an organization.
     * NGOs can create resource requests in the context of a project, these requests are created if the NGO specifically
     * browses resource offers and wants to claim one (or more) of them, organizations then recieve the requests and
     * can accept or decline them
     *
     * @param id the id of the organization
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
     * @return
     */
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
        @RequestParam(required = false) String order){

        log.debug("REST request to get all resource claim requests for organization with id " + id);
        List<ResourceMatchResponseDTO> list = new ArrayList<>();
        ResponseEntity<List<ResourceMatchResponseDTO>> responseEntity = new ResponseEntity<>(list, HttpStatus.OK);

        RestParameters restParameters = new RestParameters(page, pageSize, order, fields);
        List<ResourceMatch> resourceInQueue = resourceService.getResourcesForOrganization(id, restParameters);

        for(ResourceMatch match : resourceInQueue) {
            ResourceMatchResponseDTO resourceDTO = new ResourceMatchResponseDTO();

            OrganizationResponseDTO organizationDTO = OrganizationResponseDTO.fromEntity(match.getOrganization(), null);
            ProjectResponseDTO projectDTO = ProjectResponseDTO.fromEntity(match.getProject(), null);
            ResourceOfferDTO resourceOfferDTO = new ResourceOfferDTO(match.getResourceOffer());
            ResourceRequirementResponseDTO resourceRequirementResponseDTO = ResourceRequirementResponseDTO.fromEntity(match.getResourceRequirement(), null);

            resourceDTO.setProject(projectDTO);
            resourceDTO.setResourceOffer(resourceOfferDTO);
            resourceDTO.setResourceRequirement(resourceRequirementResponseDTO);
            resourceDTO.setMatchId(match.getId());
            resourceDTO.setMatchDirection(match.getMatchDirection().toString());
            resourceDTO.setOrganization(organizationDTO);
            resourceDTO.setAccepted(match.getAccepted());

            list.add(resourceDTO);
        }

        return responseEntity;
    }




    /**
     * GET  /rest/organizations/{id}/orgJoinRequests -> get the orgjoinrequests for organization {id}
     *
     * organization join requests are requests of organizations directed towards users. organizations can invite
     * users who do not belong to any organization, in doing so, an org join request is created which can be accepted
     * or declined by the user
     *
     * @param id the id of the organization for which to return the org join requests
     * @return response status OK and a list of org join request DTOs if the reques was executed successfully, or status
     * NOT FOUND if the organization does not exist
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
            for (OrgJoinRequest orgJoinRequest: orgJoinRequests) {
                responseEntity.getBody().add(new OrgJoinRequestDTO(orgJoinRequest));
            }

        } catch (NoSuchOrganizationException e) {
            log.error("Could not find OrgJoinRequest : {}", e);
            responseEntity = new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return responseEntity;
    }

    /**
     * GET  /rest/organizations/{id}/members -> get the members for the organization {id}
     *
     * @param id the id of the organization for which to get the members
     * @return response status OK and a list of all members if the request was executed successfully, status NOT FOUND
     * otherwise
     *
     */
    @RequestMapping(value = "/rest/organizations/{id}/members",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    ResponseEntity<List<UserDTO>> getMembers(@PathVariable Long id) {
        log.debug("REST request to get Users by OrgId : {}", id);
        ResponseEntity<List<UserDTO>> responseEntity;

        try {
            // get all users for organization with id :id
            List<User> users = organizationService.getUserByOrgId(id);
            List<UserDTO> userDTOs = new ArrayList<>();

            // map found users and http status 200
            users.forEach(p -> userDTOs.add(new UserDTO(p)));
            responseEntity = new ResponseEntity<>(userDTOs, HttpStatus.OK);

        } catch (NoSuchOrganizationException e) {
            // if the organization with :id couldn't be found
            log.error("Could not get User by Organization : {}", id, e);
            responseEntity = new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        return responseEntity;
    }

    /**
     * DELETE  /rest/organizations/{id}/members/{userId} -> delete Member by user id
     *
     * removes a user from an organization
     *
     * @param id the id of organization from which to remove the user
     * @param userId the id of the user to remove
     * @return response status OK if the request was executed successfully, or NOT FOUND if the organization or the user
     * could not be found
     */
    @RequestMapping(value = "/rest/organizations/{id}/members/{userId}",
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
        } catch (OrganizationNotVerifiedException e) {
            log.error("Could not delete Member : {}", userId, e);
            responseEntity = ErrorHelper.buildErrorResponse(e);
        }
        return responseEntity;
    }

    /**
     * GET  /rest/organizations/{id}/invitableusers -> get all users which can be invited into the given organization
     *
     * @param id the id of the organization
     * @return response status OK with a list of invitable users
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

    /**
     * rate an organization based on a resource match
     *
     * @param ratingRequestDTO the rating,containing the actual rating value [0-5], a match id indicating the
     *                         match with which the rating is associated, and an optional comment
     * @param id the id of the organization to rate, the organization must be associated with the match via a project
     * @return response status OK and the aggregated rating if it was computed successfully, NOT FOUND if the
     * organization could not be found, or BAD REQUEST with a detailed error
     */
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

    /**
     * generates an aggregated rating for the specified organization
     *
     * @param id the id of the organization for which to generate the aggregate rating
     * @return response status OK and the AggregatedRating for the organization
     */
    @RequestMapping(value = "/rest/organizations/{id}/ratings",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<?> getAggregatedRating(@PathVariable Long id) {
        ResponseEntity<?> responseEntity;

        AggregatedRating aggregatedRating = ratingService.getAggregatedRatingByOrganization(id);
        AggregatedRatingResponseDTO aggregatedRatingResponseDTO = AggregatedRatingResponseDTO
            .fromEntity(aggregatedRating, null);
        responseEntity = new ResponseEntity<>(aggregatedRatingResponseDTO, HttpStatus.OK);

        return responseEntity;
    }

    /**
     * Get the Follow State for the current Organization given by ID
     * @return Error or OK Response Entity {true/false} as result
     */
    @RequestMapping(value = "/rest/organizations/{id}/followingstate",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @RolesAllowed(AuthoritiesConstants.USER)
    public ResponseEntity followingState(@PathVariable Long id) {
        FollowStateDTO result = new FollowStateDTO();
        result.setState(organizationService.followingState(id));
        return new ResponseEntity(result, HttpStatus.OK);
    }


    /**
     * Allow user to subscribe a specific organization news
     * @return Error or OK Response Entity
     */
    @RequestMapping(value = "/rest/organizations/{id}/follow",
        method = RequestMethod.POST,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @RolesAllowed(AuthoritiesConstants.USER)
    public ResponseEntity follow(@PathVariable Long id) {
        ResponseEntity responseEntity;

        try {
            organizationService.follow(id);
            responseEntity = new ResponseEntity(HttpStatus.CREATED);
        }
        catch (IllegalValueException e){
            responseEntity = ErrorHelper.buildErrorResponse(e);
        }

        return responseEntity;
    }

    /**
     * Allow user to un-subscribe a specific organization news
     * @return Error or OK Response Entity
     */
    @RequestMapping(value = "/rest/organizations/{id}/unfollow",
        method = RequestMethod.DELETE,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @RolesAllowed(AuthoritiesConstants.USER)
    public ResponseEntity unfollow(@PathVariable Long id) {
        ResponseEntity responseEntity;

        try {
            organizationService.unfollow(id);
            responseEntity = new ResponseEntity(HttpStatus.OK);
        }
        catch (IllegalValueException e){
            responseEntity = ErrorHelper.buildErrorResponse(e);
        }

        return responseEntity;
    }


    /**
     * GET gets the postings of the given organization as a page of postings defined by page and pagesize
     *
     * @param id given id of the organization
     * @param page the page which is used for the pagerequest (0 by default)
     * @param pageSize the pagesize (elements of the page) used for the pagerequest
     * @return OK and PostingPaginationResponseDTO with found postings; NOT_FOUND if organization doesn't exist
     */
    @RequestMapping(value = "/rest/organizations/{id}/postings",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<PostingPaginationResponseDTO> getPostings(@PathVariable Long id,
                                                        @RequestParam(required = false) Integer page,
                                                        @RequestParam(required = false) Integer pageSize) {
        RestParameters restParameters = new RestParameters(page, pageSize);
        ResponseEntity<PostingPaginationResponseDTO> responseEntity;

        List<PostingDTO> postings = new ArrayList<>();
        PostingPaginationResponseDTO responseDTO = new PostingPaginationResponseDTO();
        try {
            log.debug("getting postings for organization {}", id);
            Page<Posting> currentPage = postingFeedService.getPostingsForOrganization(id, restParameters);
            log.debug("got page {} with elements {}", id, currentPage.getContent());
            for (Posting posting : currentPage.getContent()) {
                postings.add(new PostingDTO(posting));
            }
            responseDTO.setTotalElements(currentPage.getTotalElements());
            responseDTO.setPostings(postings);
            responseEntity = new ResponseEntity<>(responseDTO, HttpStatus.OK);
        } catch (NoSuchOrganizationException e) {
            log.error("Could not get postings for organization {}", id, e);
            responseEntity = new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return responseEntity;
    }

    /**
     * creates a post for the organization in the postingfeed
     * @param information the string which contains the information of the posting
     * @param id the id of the organization for which to create the posting
     * @return response status OK if no exception has been thrown; NOT_FOUND if the organization doesn't exist;
     * BAD_REQUEST if a PostingFeedException has been thrown (reason defined in the PostingFeedService)
     */
    @RequestMapping(value = "/rest/organizations/{id}/postings",
            method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    @RolesAllowed(AuthoritiesConstants.USER)
    public ResponseEntity<?> postingForOrganization(
            @RequestBody String information,
            @PathVariable Long id) {
        ResponseEntity<?> responseEntity;
        try {
            postingFeedService.addPostingForOrganization(id,information);

            responseEntity = new ResponseEntity<>(HttpStatus.OK);
        } catch (NoSuchOrganizationException e) {
            log.error("Could not post for organization {}", id, e);
            responseEntity = new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } catch (PostingFeedException e) {
            responseEntity = ErrorHelper.buildErrorResponse(e);
        }
        return responseEntity;
    }

    /**
     * deletes posting by setting active flag false
     * @param pid posting id to find in repository
     * @param id organization id for path completeness
     * @return ok if posting has been deleted; bad request if not
     */
    @RequestMapping(value = "/rest/organizations/{id}/postings/{pid}",
            method = RequestMethod.DELETE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    @RolesAllowed(AuthoritiesConstants.USER)
    public ResponseEntity<?> deletePostingForOrganization(
            @PathVariable Long id,
            @PathVariable Long pid) {
        ResponseEntity<?> responseEntity;
        try {
            postingFeedService.deletePosting(pid);

            responseEntity = new ResponseEntity<>(HttpStatus.OK);
        } catch (PostingException e) {
            responseEntity = ErrorHelper.buildErrorResponse(e);     }
        return responseEntity;
    }

    @RequestMapping(value = "/rest/organizations/{id}/verify",
        method = RequestMethod.POST,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @RolesAllowed(AuthoritiesConstants.ADMIN)
    public ResponseEntity<?> verify(@PathVariable Long id, @RequestBody Boolean verified) {
        ResponseEntity<?> responseEntity;
        try {
            Organization organization = organizationService.verify(id, verified);
            responseEntity = new ResponseEntity<>(OrganizationResponseDTO.fromEntity(organization, null), HttpStatus.OK);
        } catch (NoSuchOrganizationException e) {
            responseEntity = ErrorHelper.buildErrorResponse(e);
        } catch (OperationForbiddenException e) {
            responseEntity = new ResponseEntity<>(e.getMessage(), HttpStatus.FORBIDDEN);
        }
        return responseEntity;
    }

}
