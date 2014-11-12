package org.respondeco.respondeco.web.rest;

import com.codahale.metrics.annotation.Timed;
import org.respondeco.respondeco.domain.OrgJoinRequest;
import org.respondeco.respondeco.repository.OrgJoinRequestRepository;
import org.respondeco.respondeco.security.AuthoritiesConstants;
import org.respondeco.respondeco.service.OrgJoinRequestService;
import org.respondeco.respondeco.service.OrganizationService;
import org.respondeco.respondeco.web.rest.dto.OrgJoinRequestDTO;
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
 * REST controller for managing OrgJoinRequest.
 */
@RestController
@RequestMapping("/app")
public class OrgJoinRequestController {

    private final Logger log = LoggerFactory.getLogger(OrgJoinRequestController.class);

    private OrgJoinRequestRepository orgjoinrequestRepository;
    private OrgJoinRequestService orgJoinRequestService;

    @Inject
    public OrgJoinRequestController(OrgJoinRequestRepository orgJoinRequestRepository, OrgJoinRequestService orgJoinRequestService) {
        this.orgjoinrequestRepository = orgJoinRequestRepository;
        this.orgJoinRequestService = orgJoinRequestService;
    }

    /**
     * POST  /rest/orgjoinrequests -> Create a new orgjoinrequest.
     */
    @RequestMapping(value = "/rest/orgjoinrequests",
            method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<OrgJoinRequest> create(@RequestBody OrgJoinRequestDTO orgjoinrequest1) {
        log.debug("REST request to save OrgJoinRequest : {}", orgjoinrequest1);
        return Optional.ofNullable(orgJoinRequestService.createOrgJoinRequest(orgjoinrequest1.getOrgId(),orgjoinrequest1.getUserlogin()))
                .map(orgjoinrequest -> new ResponseEntity<>(
                        orgjoinrequest,
                        HttpStatus.OK))
                .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    /**
     * GET  /rest/orgjoinrequests -> get all the orgjoinrequests.
     */
    @RolesAllowed(AuthoritiesConstants.ADMIN)
    @RequestMapping(value = "/rest/orgjoinrequests",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public List<OrgJoinRequest> getAll() {
        log.debug("REST request to get all OrgJoinRequests");
        return orgjoinrequestRepository.findAll();
    }

    /**
     * GET  /rest/orgjoinrequests/:orgName -> get the "orgName" orgjoinrequest.
     */
    @RolesAllowed(AuthoritiesConstants.USER)
    @RequestMapping(value = "/rest/orgjoinrequests/{orgName}",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<List<OrgJoinRequest>> getByOrgName(@PathVariable String orgName) {
        log.debug("REST request to get OrgJoinRequest : {}", orgName);
        return Optional.ofNullable(orgJoinRequestService.getOrgJoinRequestByOrgName(orgName))
            .map(orgjoinrequest -> new ResponseEntity<>(
                orgjoinrequest,
                HttpStatus.OK))
            .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    /**
     * GET  /rest/orgjoinrequests/myOrgJoinRequests -> get the orgjoinrequest of own organization.
     */
    @RolesAllowed(AuthoritiesConstants.USER)
    @RequestMapping(value = "/rest/orgjoinrequests/myOrgJoinrequests",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<List<OrgJoinRequest>> getRequestsByOwner() {
        log.debug("REST request to get OrgJoinRequest : {}");
        return Optional.ofNullable(orgJoinRequestService.getRequestsByOwner())
                .map(orgjoinrequest -> new ResponseEntity<>(
                        orgjoinrequest,
                        HttpStatus.OK))
                .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    /**
     * DELETE  /rest/orgjoinrequests/:id -> accept user and delete the "id" orgjoinrequest.
     */
    @RolesAllowed(AuthoritiesConstants.USER)
    @RequestMapping(value = "/rest/orgjoinrequests/accept/{id}",
            method = RequestMethod.DELETE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public void acceptRequest(@PathVariable Long id) {
        log.debug("REST request to accept user and delete OrgJoinRequest : {}", id);
        orgJoinRequestService.acceptRequest(id);
    }

    /**
     * DELETE  /rest/orgjoinrequests/:id -> decline user and delete the "id" orgjoinrequest.
     */
    @RolesAllowed(AuthoritiesConstants.USER)
    @RequestMapping(value = "/rest/orgjoinrequests/decline/{id}",
            method = RequestMethod.DELETE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public void declineRequest(@PathVariable Long id) {
        log.debug("REST request to decline user and delete OrgJoinRequest : {}", id);
        orgJoinRequestService.declineRequest(id);
    }
}
