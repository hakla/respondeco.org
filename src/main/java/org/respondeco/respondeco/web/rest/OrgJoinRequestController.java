package org.respondeco.respondeco.web.rest;

import com.codahale.metrics.annotation.Timed;
import org.respondeco.respondeco.domain.OrgJoinRequest;
import org.respondeco.respondeco.repository.OrgJoinRequestRepository;
import org.respondeco.respondeco.security.AuthoritiesConstants;
import org.respondeco.respondeco.service.OrgJoinRequestService;
import org.respondeco.respondeco.service.exception.*;
import org.respondeco.respondeco.web.rest.dto.OrgJoinRequestDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.annotation.security.RolesAllowed;
import javax.inject.Inject;
import javax.transaction.Transactional;
import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * REST controller for managing OrgJoinRequest.
 */
@RestController
@RequestMapping("/app")
@Transactional
public class OrgJoinRequestController {

    private final Logger log = LoggerFactory.getLogger(OrgJoinRequestController.class);

    private OrgJoinRequestService orgJoinRequestService;

    @Inject
    public OrgJoinRequestController(OrgJoinRequestService orgJoinRequestService) {
        this.orgJoinRequestService = orgJoinRequestService;
    }

    /**
     * POST  /rest/orgjoinrequests -> Create a new orgjoinrequest.
     */
    @RequestMapping(value = "/rest/orgjoinrequests",
            method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<?> create(@RequestBody @Valid OrgJoinRequestDTO orgjoinrequest) {
        log.debug("REST request to save OrgJoinRequest : {}", orgjoinrequest);
        ResponseEntity<?> responseEntity;
        try {
            orgJoinRequestService.createOrgJoinRequest(orgjoinrequest.getOrganization(),
                    orgjoinrequest.getUser());
            responseEntity = new ResponseEntity<>(HttpStatus.CREATED);
        } catch (NoSuchOrganizationException e) {
            log.error("Could not save OrgJoinRequest : {}", orgjoinrequest, e);
            responseEntity = new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        } catch (NoSuchUserException e) {
            log.error("Could not save OrgJoinRequest : {}", orgjoinrequest, e);
            responseEntity = new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        return responseEntity;
    }

    /**
     * GET  /rest/orgjoinrequests -> get all the orgjoinrequests.
     */
    @RolesAllowed(AuthoritiesConstants.ADMIN)
    @RequestMapping(value = "/rest/orgjoinrequests",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<List<OrgJoinRequestDTO>> getAll() {
        log.debug("REST request to get OrgJoinRequests");
        List<OrgJoinRequestDTO> orgJoinRequestDTOs = new ArrayList<>();

        for (OrgJoinRequest orgJoinRequest : orgJoinRequestService.getAll()) {
            orgJoinRequestDTOs.add(new OrgJoinRequestDTO(orgJoinRequest));
        }

        return new ResponseEntity<List<OrgJoinRequestDTO>>(orgJoinRequestDTOs, HttpStatus.OK);
    }

    /**
     * DELETE  /rest/orgjoinrequests/:id -> accept user and delete the "id" orgjoinrequest.
     */
    @RolesAllowed(AuthoritiesConstants.USER)
    @RequestMapping(value = "/rest/orgjoinrequests/accept",
            method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<?> acceptRequest(@RequestBody OrgJoinRequestDTO id) {
        log.debug("REST request to accept user and delete OrgJoinRequest : {}", id);
        ResponseEntity<?> responseEntity;
        try {
            orgJoinRequestService.acceptRequest(id.getId());
            responseEntity = new ResponseEntity<>(HttpStatus.OK);
        } catch (NoSuchOrgJoinRequestException e) {
            log.error("Could not accept OrgJoinRequest : {}", e);
            responseEntity = new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } catch (NoSuchOrganizationException e) {
            log.error("Could not accept OrgJoinRequest : {}", e);
            responseEntity = new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } catch (AlreadyInOrganizationException e) {
            log.error("Could not accept OrgJoinRequest : {}", e);
            responseEntity = new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        return responseEntity;
    }

    /**
     * DELETE  /rest/orgjoinrequests/:id -> decline user and delete the "id" orgjoinrequest.
     */
    @RolesAllowed(AuthoritiesConstants.USER)
    @RequestMapping(value = "/rest/orgjoinrequests/decline",

        method = RequestMethod.POST,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<?> declineRequest(@RequestBody OrgJoinRequestDTO id) {
        log.debug("REST request to decline user and delete OrgJoinRequest : {}", id);
        ResponseEntity<?> responseEntity;
        try {
            orgJoinRequestService.declineRequest(id.getId());
            responseEntity = new ResponseEntity<>(HttpStatus.OK);
        } catch (NoSuchOrgJoinRequestException e) {
            log.error("Could not decline OrgJoinRequest : {}", e);
            responseEntity = new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } catch (NoSuchOrganizationException e) {
            log.error("Could not decline OrgJoinRequest : {}", e);
            responseEntity = new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return responseEntity;
    }

    /**
     * DELETE  /rest/orgjoinrequests/:id -> decline user and delete the "id" orgjoinrequest.
     */
    @RolesAllowed(AuthoritiesConstants.USER)
    @RequestMapping(value = "/rest/orgjoinrequests/{id}",
        method = RequestMethod.DELETE,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<?> delete(@PathVariable Long id) {
        log.debug("REST request to delete OrgJoinRequest : {}", id);
        ResponseEntity<?> responseEntity;
        try {
            orgJoinRequestService.delete(id);
            responseEntity = new ResponseEntity<>(HttpStatus.OK);
        } catch (NoSuchOrgJoinRequestException e) {
            log.error("Could not delete OrgJoinRequest : {}", e);
            responseEntity = new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } catch (NotOwnerOfOrganizationException | IllegalArgumentException e) {
            log.error("Could not delete OrgJoinRequest : {}", e);
            responseEntity = new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
        return responseEntity;
    }
}
