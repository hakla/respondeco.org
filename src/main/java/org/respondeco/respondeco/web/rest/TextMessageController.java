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
import org.respondeco.respondeco.service.exception.NoSuchUserException;
import org.respondeco.respondeco.web.rest.dto.TextMessageRequestDTO;
import org.respondeco.respondeco.web.rest.dto.TextMessageResponseDTO;
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

/**
 * REST controller for managing TextMessage.
 */
@Api(value = "textmessages", description = "Text messaging API")
@RestController
@RequestMapping("/app")
public class TextMessageController {

    private final Logger log = LoggerFactory.getLogger(TextMessageController.class);

    private TextMessageService textMessageService;

    @Inject
    public TextMessageController(TextMessageService textMessageService) {
        this.textMessageService =  textMessageService;
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
            textMessageService.createTextMessage(textMessageRequestDTO.getReceiver(), textMessageRequestDTO.getContent());
            responseEntity = new ResponseEntity<>(HttpStatus.CREATED);
        } catch (NoSuchUserException | IllegalArgumentException e) {
            log.error("could not save text TextMessage", e);
            responseEntity = new ResponseEntity<>(HttpStatus.BAD_REQUEST);
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
     * GET  /rest/textmessages/:receiver -> get the textmessages for the given receiver
     */
    @ApiOperation(value = "Get text messages",
            notes = "Get all text messages for the given userlogin (admin only)")
    @RequestMapping(value = "/rest/textmessages/admin/{receiver}",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    @RolesAllowed(AuthoritiesConstants.ADMIN)
    public List<TextMessageResponseDTO> getAllForReceiver(@PathVariable String receiver) {
        log.debug("REST request to get TextMessages for : {}", receiver);
        return textMessageService.getTextMessagesForUser(receiver);
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
        } catch (IllegalArgumentException e) {
            log.error("could not delete text TextMessage", e);
            responseEntity = new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        return responseEntity;
    }
}
