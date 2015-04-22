package org.respondeco.respondeco.web.rest;

import com.codahale.metrics.annotation.Timed;
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
     * @param request is used for the activation email if user doesn't exist
     * @param response is used for the activation email if user doesn't exist
     * @return OK if orgjoinrequest has been created; BAD_REQUEST if organization doesn't exist, user is already
     * invited or organization is not verified
     */
    @RequestMapping(value = "/rest/orgjoinrequests",
            method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    @RolesAllowed(AuthoritiesConstants.USER)
    public ResponseEntity<?> create(@RequestBody @Valid OrgJoinRequestDTO orgjoinrequest, HttpServletRequest request, HttpServletResponse response) {
        log.debug("REST request to save OrgJoinRequest : {}", orgjoinrequest);
        ResponseEntity<?> responseEntity;
        UserDTO user = orgjoinrequest.getUser();
        try {
            if (user.getId() == null) {
                // TODO throw new NoSuchUserException("");
            }

            orgJoinRequestService.createOrgJoinRequest(orgjoinrequest.getOrganization(),
                user);
            responseEntity = new ResponseEntity<>(HttpStatus.CREATED);
        } catch (NoSuchEntityException e) {
            log.error("Could not save OrgJoinRequest : {}", orgjoinrequest, e);
            responseEntity = new ResponseEntity<>(HttpStatus.BAD_REQUEST);
//        } catch (NoSuchUserException e) {
//            // if the user doesn't exist the owner can send an invitation to the user
//            if ("sendInvitation".matches(user.getLogin())) {
//                // create a new user with the specified email
//                User newUser = userService.createUserInformation(user.getEmail(), "tochange", null, null, null, user.getEmail(), null, null, null, null, true);
//
//                // set the organization for the user
//                userService.setOrganization(newUser, orgjoinrequest.getOrganization().getId());
//
//                // send the invited user an email so he can accept the invitation
//                mailService.sendActivationEmail(user.getEmail(), accountController.createHtmlContentFromTemplate(newUser, Locale.GERMAN, request, response, "invitationEmail"), Locale.GERMAN);
//
//                try {
//                    // and create a new orgjoinrequest that gets accepted automatically when the user registers
//                    UserDTO userDTO = new UserDTO();
//                    userDTO.setId(newUser.getId());
//                    orgJoinRequestService.createOrgJoinRequest(orgjoinrequest.getOrganization(), userDTO);
//                    responseEntity = new ResponseEntity<>(HttpStatus.OK);
//                } catch (AlreadyInvitedToOrganizationException e1) {
//                    log.error("Could not save OrgJoinRequest : {}", orgjoinrequest, e1);
//                    responseEntity = new ResponseEntity<>(0x0001, HttpStatus.BAD_REQUEST);
//                } catch (NoSuchEntityException e1) {
//                    log.error("Could not save OrgJoinRequest : {}", orgjoinrequest, e1);
//                    responseEntity = ErrorHelper.buildErrorResponse(e);
//                } catch (NoSuchUserException e1) {
//                    log.error("Could not save OrgJoinRequest : {}", orgjoinrequest, e1);
//                    responseEntity = ErrorHelper.buildErrorResponse(e);
//                } catch (OrganizationNotVerifiedException e1) {
//                    log.error("Could not save OrgJoinRequest : {}", orgjoinrequest, e);
//                    responseEntity = ErrorHelper.buildErrorResponse(e);
//                }
//            } else {
//                log.error("Could not save OrgJoinRequest : {}", orgjoinrequest, e);
//                responseEntity = ErrorHelper.buildErrorResponse(e);
//            }
        } catch (AlreadyInvitedToOrganizationException e) {
            log.error("Could not save OrgJoinRequest : {}", orgjoinrequest, e);
            responseEntity = new ResponseEntity<>(0x0001, HttpStatus.BAD_REQUEST);
        } catch (OrganizationNotVerifiedException e) {
            log.error("Could not save OrgJoinRequest : {}", orgjoinrequest, e);
            responseEntity = ErrorHelper.buildErrorResponse(e);
        }
        return responseEntity;
    }

    /**
     * GET method to get all orgjoinrequests existing
     * @return a list of orgjoinrequestdtos created out of the found orgjoinrequests in the service
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
     * POST method to accept the orgjoinrequest
     * @param id the id of the orgjoinrequest to be accepted
     * @return OK if the orgjoinrequest hast been accepted; NOT_FOUND if the orgjoinrequest doesn't exist or
     * the organization doesn't eist
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
        } catch (NoSuchEntityException e) {
            log.error("Could not accept OrgJoinRequest : {}", e);
            responseEntity = new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return responseEntity;
    }

    /**
     * POST method to decline the orgjoinrequest
     * @param id the id of the orgjoinrequest to be declined
     * @return OK if the orgjoinrequest hast been declined; NOT_FOUND if the orgjoinrequest doesn't exist or
     * the organization doesn't eist
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
        } catch (NoSuchEntityException e) {
            log.error("Could not decline OrgJoinRequest : {}", e);
            responseEntity = new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return responseEntity;
    }

    /**
     * DELETE method to delete the orgjoinreuest
     * @param id the id of the orgjoinrequest to be deleted
     * @return OK if the orgjoinrequest hast been deleted; NOT_FOUND if the orgjoinrequest doesn't exist,
     * FORBIDDEN if the user is not owner of the organization or BAD_REQUEST if the organization doesn'T exist
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
        } catch (NotOwnerOfOrganizationException | IllegalArgumentException e) {
            log.error("Could not delete OrgJoinRequest : {}", e);
            responseEntity = new ResponseEntity<>(HttpStatus.FORBIDDEN);
        } catch (NoSuchEntityException e) {
            log.error("Could not delete OrgJoinRequest : {}", e);
            responseEntity = new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        }
        return responseEntity;
    }
}
