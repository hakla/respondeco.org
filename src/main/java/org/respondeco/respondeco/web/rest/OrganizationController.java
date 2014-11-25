package org.respondeco.respondeco.web.rest;

import com.codahale.metrics.annotation.Timed;
import org.apache.commons.lang.StringUtils;
import org.respondeco.respondeco.domain.Authority;
import org.respondeco.respondeco.domain.Organization;
import org.respondeco.respondeco.domain.User;
import org.respondeco.respondeco.repository.OrganizationRepository;
import org.respondeco.respondeco.security.AuthoritiesConstants;
import org.respondeco.respondeco.service.OrganizationService;
import org.respondeco.respondeco.service.UserService;
import org.respondeco.respondeco.service.exception.AlreadyInOrganizationException;
import org.respondeco.respondeco.service.exception.NoSuchOrganizationException;
import org.respondeco.respondeco.service.exception.OrganizationAlreadyExistsException;
import org.respondeco.respondeco.web.rest.dto.OrganizationDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
 * REST controller for managing Organization.
 */
@RestController
@RequestMapping("/app")
public class OrganizationController {

    private final Logger log = LoggerFactory.getLogger(OrganizationController.class);

    private OrganizationRepository organizationRepository;
    private OrganizationService organizationService;
    private UserService userService;

    @Inject
    public OrganizationController (OrganizationRepository organizationRepository, OrganizationService organizationService, UserService userService) {
        this.organizationRepository = organizationRepository;
        this.organizationService = organizationService;
        this.userService = userService;
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
            organizationService.createOrganizationInformation(
                    newOrganization.getName(),
                    newOrganization.getDescription(),
                    newOrganization.getEmail(),
                    newOrganization.isNpo());
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
    public List<Organization> getAll() {
        log.debug("REST request to get all Organizations");
        return organizationRepository.findAll();
    }

    /**
     * GET  /rest/organizations -> get all the organizations.
     */
    @RolesAllowed(AuthoritiesConstants.USER)
    @RequestMapping(value = "/rest/organization/{id}",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public Organization getById(@PathVariable Long id) {
        log.debug("REST request to get one Organization by id");
        return organizationRepository.findOne(id);
    }

    /**
     * GET  /rest/organizations/:orgName -> get the "orgName" organization.
     */
    @RolesAllowed(AuthoritiesConstants.USER)
    @RequestMapping(value = "/rest/organizations/{orgName}",
            method = RequestMethod.GET,

            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Organization> get(@PathVariable String orgName) {
        log.debug("REST request to get Organization : {}", orgName);
        ResponseEntity<Organization> responseEntity;
        try {
            return Optional.ofNullable(organizationService.getOrganizationByName(orgName))
                    .map(organization -> new ResponseEntity<>(
                            organization,
                            HttpStatus.OK))
                    .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
        } catch (NoSuchOrganizationException e) {
            log.error("Could not get Organization : {}", orgName, e);
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
     * GET  /rest/organizations/:owner -> get the organization of current owner.
     */
    @RolesAllowed(AuthoritiesConstants.USER)
    @RequestMapping(value = "/rest/organizations/{id}/members",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public List<User> getMembers(@PathVariable Long id) {
        log.debug("REST request to get members for organization Organization : {}" ,userService.getUserWithAuthorities().getLogin());
        return userService.getOrganizationMembers(id);
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
                    organization.isNpo());
            responseEntity = new ResponseEntity<>(HttpStatus.OK);
        } catch (NoSuchOrganizationException e) {
            log.error("Could not update Organization : {}", organization, e);
            responseEntity = new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return responseEntity;
    }

    /**
     * DELETE  /rest/organizations/:orgName -> delete the "orgName" organization.
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

}
