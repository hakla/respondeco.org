package org.respondeco.respondeco.web.rest;

import com.codahale.metrics.annotation.Timed;
import com.wordnik.swagger.annotations.ApiOperation;
import org.respondeco.respondeco.aop.RESTWrapped;
import org.respondeco.respondeco.domain.Organization;
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

import javax.annotation.security.PermitAll;
import javax.annotation.security.RolesAllowed;
import javax.inject.Inject;
import javax.transaction.Transactional;
import javax.validation.Valid;

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
    private OrgJoinRequestService orgJoinRequestService;
    private RatingService ratingService;
    private PostingFeedService postingFeedService;

    @Inject
    public OrganizationController(OrganizationService organizationService,
                                  UserService userService,
                                  ResourceService resourceService,
                                  OrgJoinRequestService orgJoinRequestService,
                                  RatingService ratingService,
                                  PostingFeedService postingFeedService) {
        this.organizationService = organizationService;
        this.resourceService = resourceService;
        this.orgJoinRequestService = orgJoinRequestService;
        this.ratingService = ratingService;
        this.postingFeedService = postingFeedService;
    }

    /**
     * POST  /rest/organizations -> Create a new organization.
     * <p/>
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
    @RESTWrapped
    public Object create(@RequestBody @Valid OrganizationRequestDTO newOrganization) {
        log.debug("REST request to save Organization : {}", newOrganization);
        return organizationService.createOrganizationInformation(
            newOrganization.getName(),
            newOrganization.getDescription(),
            newOrganization.getEmail(),
            newOrganization.getIsNpo(),
            newOrganization.getLogo(),
            newOrganization.getIsoCategories());
    }

    /**
     * GET  /rest/organizations -> get all the organizations.
     * <p/>
     * by default, get the first 20 of all the organizations, the returned organizations can be filtered via the
     * input parameters
     * <p/>
     * page and pageSize work as follows, supposed that there are 50 projects in the database, if page = 2 and
     * pageSize = 15, database entries 16-30 will be returned, the offset and limit can be computed as follows:
     * offset = (page - 1) * pageSize
     * limit = pageSize
     *
     * @param page     optional parameter indicating the page of projects to be returned, works in conjunction with
     *                 pageSize, dafault is 1 (first page)
     * @param pageSize optional parameter indicating the size of the pages of projects to be returned
     * @param fields   optional parameter indicating the fields of the responses to be returned, if specified, only the
     *                 corresponding fields in the response DTO will be set.
     *                 example: fields=id,name
     *                 response: [{id: 0, name: "example1"}, {id: 1, name: "ex2"}, ...]
     * @param order    optional parameter indicating the order of the returned values, orders can be specified as follows:
     *                 fieldname: orders the responses by the fieldname ascending,
     *                 +fieldname: same as fieldname,
     *                 -fieldname: orders the responses by the fieldname descending
     *                 example: order=-id,+name orders by id descending and name ascending
     * @return a list of matching organization DTOs
     */
    @ApiOperation(value = "Get organizations", notes = "Get organizations")
    @RequestMapping(value = "/rest/organizations",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    @RESTWrapped
    public Object get(
        @RequestParam(required = false) Integer page,
        @RequestParam(required = false) Integer pageSize,
        @RequestParam(required = false) String fields,
        @RequestParam(required = false) String order) {
        log.debug("REST request to get organizations");
        RestParameters restParameters = new RestParameters(page, pageSize, order, fields);
        return organizationService.get(restParameters.buildPageRequest());
    }

    /**
     * GET  /rest/organization/:id -> get the "id" organization.
     *
     * @param id the id of the organization to return
     * @return response status OK and an organization DTO if the organization was found, response status NOT FOUND if
     * an organization with the given id does not exist
     */
    @PermitAll
    @ApiOperation(value = "Get organization", notes = "Get a organization by its id")
    @RequestMapping(value = "/rest/organizations/{id}",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    @RESTWrapped
    public Object getById(@PathVariable Long id) {
        log.debug("REST request to get Organization : {}", id);
        return organizationService.getById(id);
    }

    /**
     * PUT  /rest/organizations -> Update an organization.
     * <p/>
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
    @RESTWrapped
    public Object update(@RequestBody @Valid OrganizationRequestDTO organization) {
        log.debug("REST request to update Organization : {}", organization);
        Long logoId = null;
        if(organization.getLogo() != null) {
            logoId = organization.getLogo().getId();
        }
        return organizationService.update(
            organization.getName(),
            organization.getDescription(),
            organization.getEmail(),
            organization.getIsNpo(),
            logoId,
            organization.getWebsite(),
            organization.getIsoCategories());
    }

    /**
     * DELETE  /rest/organizations/:organization
     * -> delete the "organization" of which the currently logged in user is the owner
     *
     * @return response status OK if the organization was deleted successfully, or NOT FOUND if the user is not
     * the owner of an existing organization
     */
    @RolesAllowed(AuthoritiesConstants.ADMIN)
    @RequestMapping(value = "/rest/organizations/{id}",
        method = RequestMethod.DELETE,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    @RESTWrapped
    public Object delete(@PathVariable Long id) {
        log.debug("REST request to delete Organization : {}");
        organizationService.delete(id);
        return null;
    }

    /**
     * GET /rest/organizations/{id}/resourceoffers -> get the resourceOffers for the organization {id}
     * <p/>
     * Returns all ResourceOffers created by a specific Organzation given by id
     *
     * @param id the id of the organization
     * @return a list of ResourceOffers
     */
    @PermitAll
    @RequestMapping(value = "/rest/organizations/{id}/resourceoffers",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    @RESTWrapped
    public Object getOffersForOrganization(@PathVariable Long id) {
        log.debug("REST request to get all resource offers which belong to Organization id: {}", id);
        return this.resourceService.getOffersForOrganization(id);
    }


    /**
     * get resource requests which were directed at an organization.
     * NGOs can create resource requests in the context of a project, these requests are created if the NGO specifically
     * browses resource offers and wants to claim one (or more) of them, organizations then recieve the requests and
     * can accept or decline them
     *
     * @param id       the id of the organization
     * @param page     optional parameter indicating the page of projects to be returned, works in conjunction with
     *                 pageSize, dafault is 1 (first page)
     * @param pageSize optional parameter indicating the size of the pages of projects to be returned
     * @param order    optional parameter indicating the order of the returned values, orders can be specified as follows:
     *                 fieldname: orders the responses by the fieldname ascending,
     *                 +fieldname: same as fieldname,
     *                 -fieldname: orders the responses by the fieldname descending
     *                 example: order=-id,+name orders by id descending and name ascending
     * @return resources for the organization
     */
    @RolesAllowed(AuthoritiesConstants.USER)
    @RequestMapping(value = "/rest/organizations/{id}/resourcerequests",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    @RESTWrapped
    public Object getAllResourceRequests(
        @PathVariable Long id,
        @RequestParam(required = false) Integer page,
        @RequestParam(required = false) Integer pageSize,
        @RequestParam(required = false) String order) {

        log.debug("REST request to get all resource claim requests for organization with id " + id);
        RestParameters restParameters = new RestParameters(page, pageSize, order);
        return resourceService.getResourcesForOrganization(id, restParameters.buildPageRequest());
    }


    /**
     * GET  /rest/organizations/{id}/orgJoinRequests -> get the orgjoinrequests for organization {id}
     * <p/>
     * organization join requests are requests of organizations directed towards users. organizations can invite
     * users who do not belong to any organization, in doing so, an org join request is created which can be accepted
     * or declined by the user
     *
     * @param id the id of the organization for which to return the org join requests
     * @return a list of org join requests
     */
    @RolesAllowed(AuthoritiesConstants.USER)
    @RequestMapping(value = "/rest/organizations/{id}/orgjoinrequests",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    @RESTWrapped
    public Object getByOrgId(@PathVariable Long id) {
        log.debug("REST request to get OrgJoinRequest : {}", id);
        return orgJoinRequestService.getOrgJoinRequestByOrganization(id);
    }

    /**
     * GET  /rest/organizations/{id}/members -> get the members for the organization {id}
     *
     * @param id the id of the organization for which to get the members
     * @return a list of all members
     */
    @RequestMapping(value = "/rest/organizations/{id}/members",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    @RESTWrapped
    public Object getMembers(@PathVariable Long id) {
        log.debug("REST request to get Users by OrgId : {}", id);
        return organizationService.getUserByOrgId(id);
    }

    /**
     * DELETE  /rest/organizations/{id}/members/{userId} -> delete Member by user id
     * <p/>
     * removes a user from an organization
     *
     * @param userId the id of the user to remove
     * @return response status OK if the request was executed successfully, or NOT FOUND if the organization or the user
     * could not be found
     */
    @RequestMapping(value = "/rest/organizations/{id}/members/{userId}",
        method = RequestMethod.DELETE,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    @RolesAllowed(AuthoritiesConstants.USER)
    @RESTWrapped
    public Object deleteMember(@PathVariable Long userId) {
        log.debug("REST request to delete Member : {}", userId);
        organizationService.deleteMember(userId);
        return null;
    }

    /**
     * GET  /rest/organizations/{id}/invitableusers -> get all users which can be invited into the given organization
     *
     * @param id the id of the organization
     * @return list of invitable users
     */
    @RequestMapping(value = "/rest/organizations/{id}/invitableusers",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    @RolesAllowed(AuthoritiesConstants.USER)
    @RESTWrapped
    public Object getInvitableUsers(@PathVariable Long id) {
        return organizationService.findInvitableUsersByOrgId(id);
    }

    /**
     * rate an organization based on a resource match
     *
     * @param ratingRequestDTO the rating,containing the actual rating value [0-5], a match id indicating the
     *                         match with which the rating is associated, and an optional comment
     * @param id               the id of the organization to rate, the organization must be associated with the match via a project
     * @return response status OK and the aggregated rating if it was computed successfully, NOT FOUND if the
     * organization could not be found, or BAD REQUEST with a detailed error
     */
    @RequestMapping(value = "/rest/organizations/{id}/ratings",
        method = RequestMethod.POST,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    @RolesAllowed(AuthoritiesConstants.USER)
    @RESTWrapped
    public Object rateOrganization(
        @RequestBody @Valid RatingRequestDTO ratingRequestDTO,
        @PathVariable Long id) {

        ratingService.rateOrganization(id, ratingRequestDTO.getMatchid(),
            ratingRequestDTO.getRating(),
            ratingRequestDTO.getComment());
        return null;
    }

    /**
     * generates an aggregated rating for the specified organization
     *
     * @param id the id of the organization for which to generate the aggregate rating
     * @return aggregated rating by organization
     */
    @RequestMapping(value = "/rest/organizations/{id}/ratings",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    @RESTWrapped
    public Object getAggregatedRating(@PathVariable Long id) {
        return ratingService.getAggregatedRatingByOrganization(id);
    }

    /**
     * Get the Follow State for the current Organization given by ID
     *
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
     *
     * @return null
     */
    @RequestMapping(value = "/rest/organizations/{id}/follow",
        method = RequestMethod.POST,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @RolesAllowed(AuthoritiesConstants.USER)
    @RESTWrapped
    public Object follow(@PathVariable Long id) {
        organizationService.follow(id);
        return null;
    }

    /**
     * Allow user to un-subscribe a specific organization news
     *
     * @return null
     */
    @RequestMapping(value = "/rest/organizations/{id}/unfollow",
        method = RequestMethod.DELETE,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @RolesAllowed(AuthoritiesConstants.USER)
    @RESTWrapped
    public Object unfollow(@PathVariable Long id) {
        organizationService.unfollow(id);
        return null;
    }


    /**
     * GET gets the postings of the given organization as a page of postings defined by page and pagesize
     *
     * @param id       given id of the organization
     * @param page     the page which is used for the pagerequest (0 by default)
     * @param pageSize the pagesize (elements of the page) used for the pagerequest
     * @return OK and PostingPaginationResponseDTO with found postings; NOT_FOUND if organization doesn't exist
     */
    @RequestMapping(value = "/rest/organizations/{id}/postings",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    @RESTWrapped
    public Object getPostings(@PathVariable Long id,
                              @RequestParam(required = false) Integer page,
                              @RequestParam(required = false) Integer pageSize) {
        RestParameters restParameters = new RestParameters(page, pageSize);
        log.debug("getting postings for organization {}", id);
        return postingFeedService.getPostingsForOrganization(id, restParameters.buildPageRequest());
    }

    /**
     * creates a post for the organization in the postingfeed
     *
     * @param information the string which contains the information of the posting
     * @param id          the id of the organization for which to create the posting
     * @return response status OK if no exception has been thrown; NOT_FOUND if the organization doesn't exist;
     * BAD_REQUEST if a PostingFeedException has been thrown (reason defined in the PostingFeedService)
     */
    @RequestMapping(value = "/rest/organizations/{id}/postings",
        method = RequestMethod.POST,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    @RolesAllowed(AuthoritiesConstants.USER)
    @RESTWrapped
    public Object createPostingForOrganization( @PathVariable Long id,
                                                @RequestBody String information) {
        return postingFeedService.createPostingForOrganization(id, information);
    }

    /**
     * deletes posting by setting active flag false
     *
     * @param pid posting id to find in repository
     * @param id  organization id for path completeness
     * @return ok if posting has been deleted; bad request if not
     */
    @RequestMapping(value = "/rest/organizations/{id}/postings/{pid}",
        method = RequestMethod.DELETE,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    @RolesAllowed(AuthoritiesConstants.USER)
    @RESTWrapped
    public void deletePostingForOrganization(   @PathVariable Long id,
                                                @PathVariable Long pid) {
        postingFeedService.deletePosting(pid);
    }

    /**
     * update organizations verified status
     *
     * @param id organization id
     * @param verified new verified flag
     * @return the new organization object
     */
    @RequestMapping(value = "/rest/organizations/{id}/verify",
        method = RequestMethod.POST,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @RolesAllowed(AuthoritiesConstants.ADMIN)
    @RESTWrapped
    public Object verify(@PathVariable Long id, @RequestBody Boolean verified) {
        return organizationService.verify(id, verified);
    }


    /**
     * Get donated resources for an organization.
     *
     * @param id organization id
     * @return
     */
    @RequestMapping(value = "/rest/organizations/{id}/donatedresources",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    @RESTWrapped
    public Object getDonatedResources(
        @PathVariable Long id,
        @RequestParam(required = false) Integer page,
        @RequestParam(required = false) Integer pageSize,
        @RequestParam(required = false) String fields,
        @RequestParam(required = false) String order) {

        RestParameters restParameters = new RestParameters(page, pageSize, order, fields);
        return resourceService.getDonatedResourcesForOrganization(id, restParameters);
    }

    @RequestMapping(value = "/rest/isocategories/{id}",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @RESTWrapped
    public Object getSubCategoriesOf(@PathVariable Long id) {
        return organizationService.getSubCategoriesOf(id);
    }

    @RequestMapping(value = "/rest/isocategories",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @RESTWrapped
    public Object getSuperCategories() {
        return organizationService.getAllSuperCategories();
    }

}
