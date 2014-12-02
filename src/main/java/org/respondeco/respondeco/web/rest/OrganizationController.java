package org.respondeco.respondeco.web.rest;

import com.codahale.metrics.annotation.Timed;
import org.respondeco.respondeco.domain.OrgJoinRequest;
import org.respondeco.respondeco.domain.Organization;
import org.respondeco.respondeco.domain.User;
import org.respondeco.respondeco.repository.OrganizationRepository;
import org.respondeco.respondeco.security.AuthoritiesConstants;
import org.respondeco.respondeco.service.OrgJoinRequestService;
import org.respondeco.respondeco.service.OrganizationService;
import org.respondeco.respondeco.service.ResourceService;
import org.respondeco.respondeco.service.UserService;
import org.respondeco.respondeco.service.exception.AlreadyInOrganizationException;
import org.respondeco.respondeco.service.exception.NoSuchOrganizationException;
import org.respondeco.respondeco.service.exception.NotOwnerOfOrganizationException;
import org.respondeco.respondeco.service.exception.OrganizationAlreadyExistsException;
import org.respondeco.respondeco.web.rest.dto.OrgJoinRequestDTO;
import org.respondeco.respondeco.web.rest.dto.OrganizationDTO;
import org.respondeco.respondeco.web.rest.dto.ResourceOfferDTO;
import org.respondeco.respondeco.web.rest.dto.UserDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.annotation.security.RolesAllowed;
import javax.inject.Inject;
import javax.validation.Valid;
import javax.xml.ws.Response;
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

    @Inject
    public OrganizationController (OrganizationService organizationService, UserService userService, ResourceService resourceService, OrgJoinRequestService orgJoinRequestService) {
        this.organizationService = organizationService;
        this.userService = userService;
        this.resourceService = resourceService;
        this.orgJoinRequestService = orgJoinRequestService;
    }

    /**
     * POST  /rest/organizations -> Create a new organization.
     */
    @RolesAllowed(AuthoritiesConstants.USER)
    @RequestMapping(value = "/rest/organizations",
            method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<?> create(@RequestBody @Valid OrganizationDTO newOrganization){
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
     * GET  /rest/organizations -> get all organizations.
     */
    @RolesAllowed(AuthoritiesConstants.USER)
    @RequestMapping(value = "/rest/organizations",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public List<OrganizationDTO> getAll() {
        log.debug("REST request to get all Organizations");
        return organizationService.getOrganizations();
    }

    /**
     * GET  /rest/organizations/:id -> get organization by id
     */
    @RolesAllowed(AuthoritiesConstants.USER)
    @RequestMapping(value = "/rest/organizations/{id}",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<OrganizationDTO> getById(@PathVariable Long id) {
        log.debug("REST request to get one Organization by id");
        ResponseEntity<OrganizationDTO> responseEntity;
        OrganizationDTO organizationDTO;

        try {
            organizationDTO = organizationService.getOrganizationById(id);
            responseEntity = new ResponseEntity<OrganizationDTO>(organizationDTO, HttpStatus.OK);

        } catch (NoSuchOrganizationException e) {
            responseEntity = new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        return responseEntity;
    }

    /**
     * GET  /rest/organizations/:owner -> get the organization of current owner.
     */
    @RolesAllowed(AuthoritiesConstants.USER)
    @RequestMapping(value = "/rest/organizations/myOrganization",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Organization> get() {
        log.debug("REST request to get Organization : {}" ,userService.getUserWithAuthorities().getLogin());
        ResponseEntity<Organization> responseEntity;
        try {
            return Optional.ofNullable(organizationService.getOrganizationByOwner())
                    .map(organization -> new ResponseEntity<>(
                            organization,
                            HttpStatus.OK))
                    .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
        } catch (NoSuchOrganizationException e) {
            log.error("Could not get Organization : {}", e);
            responseEntity = new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return responseEntity;
    }

    /**
     * POST  /rest/organizations -> Update an organization.
     */
    @RolesAllowed(AuthoritiesConstants.USER)
    @RequestMapping(value = "/rest/organizations/updateOrganization",
            method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<?> update(@RequestBody @Valid OrganizationDTO organization){
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
        OrganizationDTO organizationDTO;

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

    public ResponseEntity<List<OrgJoinRequestDTO>> getByOrgName(@PathVariable Long id){
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
}
