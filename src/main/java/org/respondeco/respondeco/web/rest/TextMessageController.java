package org.respondeco.respondeco.web.rest;

import com.codahale.metrics.annotation.Timed;
import com.wordnik.swagger.annotations.Api;
import com.wordnik.swagger.annotations.ApiOperation;
import org.respondeco.respondeco.domain.TextMessage;
import org.respondeco.respondeco.domain.User;
import org.respondeco.respondeco.repository.TextMessageRepository;
import org.respondeco.respondeco.repository.UserRepository;
import org.respondeco.respondeco.security.AuthoritiesConstants;
import org.respondeco.respondeco.service.TextMessageService;
import org.respondeco.respondeco.service.UserService;
import org.respondeco.respondeco.service.exception.IllegalValueException;
import org.respondeco.respondeco.service.exception.NoSuchEntityException;
import org.respondeco.respondeco.service.exception.OperationForbiddenException;
import org.respondeco.respondeco.web.rest.dto.TextMessageRequestDTO;
import org.respondeco.respondeco.web.rest.dto.TextMessageResponseDTO;
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
import javax.validation.Valid;
import java.util.List;
import java.util.Map;

/**
 * REST controller for managing TextMessage.
 */
@Api(value = "textmessages", description = "Text messaging API")
@RestController
@RequestMapping("/app")
public class TextMessageController {

    private final Logger log = LoggerFactory.getLogger(TextMessageController.class);

    private TextMessageService textMessageService;
    private UserService userService;

    @Inject
    public TextMessageController(TextMessageService textMessageService, UserService userService) {
        this.textMessageService =  textMessageService;
        this.userService = userService;
    }

    /**
     * POST  /rest/textmessages -> Create a new textmessage.
     */
    @ApiOperation(value = "Send text message", notes = "Sends a text message to another user")
    @RequestMapping(value = "/rest/textmessages",
            method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    @RolesAllowed(AuthoritiesConstants.USER)
    public ResponseEntity<?> create(@RequestBody @Valid TextMessageRequestDTO textMessageRequestDTO) {
        log.debug("REST request to save TextMessage : {}", textMessageRequestDTO);
        ResponseEntity<?> responseEntity;
        try {
            textMessageService.createTextMessage(textMessageRequestDTO.getReceiver().getId(),
                textMessageRequestDTO.getContent());
            responseEntity = new ResponseEntity<>(HttpStatus.CREATED);
        } catch (NoSuchEntityException e) {
            log.error("could not save text TextMessage", e);
            responseEntity = new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } catch (IllegalValueException e) {
            log.error("could not save text TextMessage", e);
            responseEntity = ErrorHelper.buildErrorResponse(e);
        }
        return responseEntity;
    }

    /**
     * GET  /rest/textmessages -> get all the textmessages for the current user.
     */
    @ApiOperation(value = "Get text messages",
            notes = "Get all active text messages for the current user")
    @RequestMapping(value = "/rest/textmessages",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    @RolesAllowed(AuthoritiesConstants.USER)
    public List<TextMessageResponseDTO> getAllForCurrentUser() {
        log.debug("REST request to get all TextMessages for current user");
        return textMessageService.getTextMessagesForCurrentUser();
    }

    /**
     * DELETE  /rest/textmessages/:id -> delete the "id" textmessage.
     */
    @ApiOperation(value = "Delete a text message",
            notes = "Delete the text message with the given id, if the current user is the receiver")
    @RequestMapping(value = "/rest/textmessages/{id}",
            method = RequestMethod.DELETE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    @RolesAllowed(AuthoritiesConstants.USER)
    public ResponseEntity<?> delete(@PathVariable Long id) {
        log.debug("REST request to delete TextMessage : {}", id);
        ResponseEntity<?> responseEntity;
        try {
            textMessageService.deleteTextMessage(id);
            responseEntity = new ResponseEntity<>(HttpStatus.OK);
        } catch (IllegalValueException e) {
            log.error("could not delete text TextMessage", e);
            responseEntity = ErrorHelper.buildErrorResponse(e);
        }
        return responseEntity;
    }

    /**
     * Returns the amount of unread messages
     * @return
     */
    @RequestMapping(value = "/rest/messages/unread",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    @RolesAllowed(AuthoritiesConstants.USER)
    public ResponseEntity<Long> getCountOfNewMessages() {
        ResponseEntity<Long> responseEntity;

        return new ResponseEntity<>(textMessageService.countNewMessages(userService.getUserWithAuthorities()), HttpStatus.OK);
    }

    /**
     * Marks the message with the given id as read
     * @param id
     * @return
     */
    @RequestMapping(value = "/rest/messages/{id}/markread",
        method = RequestMethod.POST,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    @RolesAllowed(AuthoritiesConstants.USER)
    public ResponseEntity setRead(@PathVariable Long id) {
        ResponseEntity responseEntity = new ResponseEntity<>(HttpStatus.OK);

        try {
            textMessageService.setRead(id);
        } catch (Exception e) {
            responseEntity = new ResponseEntity(HttpStatus.BAD_REQUEST);
        }

        return responseEntity;
    }

}
