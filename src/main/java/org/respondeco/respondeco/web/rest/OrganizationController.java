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
    public void create(@RequestBody @Valid OrganizationDTO newOrganization) throws AlreadyInOrganizationException, OrganizationAlreadyExistsException {
        log.debug("REST request to save Organization : {}", newOrganization);
        organizationService.createOrganizationInformation(
                newOrganization.getName(),
                newOrganization.getDescription(),
                newOrganization.getEmail(),
                newOrganization.isNpo());
    }

    /**
     * GET  /rest/organizations -> get all the organizations.
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
     * GET  /rest/organizations/:orgName -> get the "orgName" organization.
     */
    @RolesAllowed(AuthoritiesConstants.USER)
    @RequestMapping(value = "/rest/organizations/{orgName}",
            method = RequestMethod.GET,

            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Organization> get(@PathVariable String orgName) throws NoSuchOrganizationException {
        log.debug("REST request to get Organization : {}", orgName);
        return Optional.ofNullable(organizationService.getOrganizationByName(orgName))
                .map(organization -> new ResponseEntity<>(
                        organization,
                        HttpStatus.OK))
                .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    /**
     * GET  /rest/organizations/:owner -> get the organization of current owner.
     */
    @RolesAllowed(AuthoritiesConstants.USER)
    @RequestMapping(value = "/rest/organizations/myOrganization",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Organization> get() throws NoSuchOrganizationException {
        log.debug("REST request to get Organization : {}" ,userService.getUserWithAuthorities().getLogin());
        return Optional.ofNullable(organizationService.getOrganizationByOwner())
                .map(organization -> new ResponseEntity<>(
                        organization,
                        HttpStatus.OK))
                .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }


    /**
     * POST  /rest/organizations -> Update an organization.
     */
    @RolesAllowed(AuthoritiesConstants.USER)
    @RequestMapping(value = "/rest/organizations/updateOrganization",
            method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed

    public void update(@RequestBody @Valid OrganizationDTO organization) throws NoSuchOrganizationException{
        log.debug("REST request to update Organization : {}", organization);
        organizationService.updaterOrganizationInformation(
                organization.getName(),
                organization.getDescription(),
                organization.getEmail(),
                organization.isNpo());
    }

    /**
     * DELETE  /rest/organizations/:orgName -> delete the "orgName" organization.
     */
    @RolesAllowed(AuthoritiesConstants.USER)
    @RequestMapping(value = "/rest/organizations",
            method = RequestMethod.DELETE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public void delete() throws NoSuchOrganizationException {
        log.debug("REST request to delete Organization : {}");
        organizationService.deleteOrganizationInformation();
    }

}
