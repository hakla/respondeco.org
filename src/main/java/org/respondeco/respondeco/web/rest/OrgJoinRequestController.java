package org.respondeco.respondeco.web.rest;

import com.codahale.metrics.annotation.Timed;
import org.respondeco.respondeco.aop.RESTWrapped;
import org.respondeco.respondeco.domain.OrgJoinRequest;
import org.respondeco.respondeco.domain.User;
import org.respondeco.respondeco.security.AuthoritiesConstants;
import org.respondeco.respondeco.service.MailService;
import org.respondeco.respondeco.service.OrgJoinRequestService;
import org.respondeco.respondeco.service.OrganizationService;
import org.respondeco.respondeco.service.UserService;
import org.respondeco.respondeco.service.exception.*;
import org.respondeco.respondeco.web.rest.dto.OrgJoinRequestDTO;
import org.respondeco.respondeco.web.rest.dto.UserDTO;
import org.respondeco.respondeco.web.rest.util.ErrorHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.annotation.security.RolesAllowed;
import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.transaction.Transactional;
import javax.validation.Valid;
import java.util.*;

/**
 * REST controller for managing OrgJoinRequest.
 */
@RestController
@RequestMapping("/app")
@Transactional
public class OrgJoinRequestController {

    private final Logger log = LoggerFactory.getLogger(OrgJoinRequestController.class);

    private OrganizationService organizationService;
    private OrgJoinRequestService orgJoinRequestService;
    private UserService userService;
    private MailService mailService;
    private AccountController accountController;

    @Inject
    public OrgJoinRequestController(OrgJoinRequestService orgJoinRequestService, UserService userService, MailService mailService, AccountController accountController, OrganizationService organizationService) {
        this.orgJoinRequestService = orgJoinRequestService;
        this.userService = userService;
        this.mailService = mailService;
        this.accountController = accountController;
        this.organizationService = organizationService;
    }

    /**
     * POST method to create orgjoinrequest
     * @param orgjoinrequest orgjoinrequestdto with the given informations to create an orgjoinrequest
     * @return null
     */
    @RequestMapping(value = "/rest/orgjoinrequests",
            method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    @RolesAllowed(AuthoritiesConstants.USER)
    @RESTWrapped
    public Object create(@RequestBody @Valid OrgJoinRequestDTO orgjoinrequest) {
        log.debug("REST request to save OrgJoinRequest : {}", orgjoinrequest);
        orgJoinRequestService.createOrgJoinRequest(orgjoinrequest.getOrganization(),
            orgjoinrequest.getUser());
        return null;
    }

    /**
     * GET method to get all orgjoinrequests existing
     * @return a list of orgjoinrequests
     */
    @RolesAllowed(AuthoritiesConstants.ADMIN)
    @RequestMapping(value = "/rest/orgjoinrequests",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    @RESTWrapped
    public Object getAll() {
        log.debug("REST request to get OrgJoinRequests");
        return orgJoinRequestService.getAll();
    }

    /**
     * POST method to accept the orgjoinrequest
     * @param id the id of the orgjoinrequest to be accepted
     * @return null
     */
    @RolesAllowed(AuthoritiesConstants.USER)
    @RequestMapping(value = "/rest/orgjoinrequests/accept",
            method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    @RESTWrapped
    public Object acceptRequest(@RequestBody OrgJoinRequestDTO id) {
        log.debug("REST request to accept user and delete OrgJoinRequest : {}", id);
        orgJoinRequestService.acceptRequest(id.getId());
        return null;
    }

    /**
     * POST method to decline the orgjoinrequest
     * @param id the id of the orgjoinrequest to be declined
     * @return null
     */
    @RolesAllowed(AuthoritiesConstants.USER)
    @RequestMapping(value = "/rest/orgjoinrequests/decline",

        method = RequestMethod.POST,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    @RESTWrapped
    public Object declineRequest(@RequestBody OrgJoinRequestDTO id) {
        log.debug("REST request to decline user and delete OrgJoinRequest : {}", id);
        orgJoinRequestService.declineRequest(id.getId());
        return null;
    }

    /**
     * DELETE method to delete the orgjoinreuest
     * @param id the id of the orgjoinrequest to be deleted
     * @return null
     */
    @RolesAllowed(AuthoritiesConstants.USER)
    @RequestMapping(value = "/rest/orgjoinrequests/{id}",
        method = RequestMethod.DELETE,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    @RESTWrapped
    public Object delete(@PathVariable Long id) {
        log.debug("REST request to delete OrgJoinRequest : {}", id);
        orgJoinRequestService.delete(id);
        return null;
    }
}
