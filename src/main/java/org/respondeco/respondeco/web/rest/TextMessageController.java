package org.respondeco.respondeco.web.rest;

import com.codahale.metrics.annotation.Timed;
import org.respondeco.respondeco.domain.TextMessage;
import org.respondeco.respondeco.repository.TextMessageRepository;
import org.respondeco.respondeco.security.AuthoritiesConstants;
import org.respondeco.respondeco.service.TextMessageService;
import org.respondeco.respondeco.service.exception.NoSuchUserException;
import org.respondeco.respondeco.web.rest.dto.TextMessageDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.annotation.security.RolesAllowed;
import javax.inject.Inject;
import java.util.List;

/**
 * REST controller for managing TextMessage.
 */
@RestController
@RequestMapping("/app")
public class TextMessageController {

    private final Logger log = LoggerFactory.getLogger(TextMessageController.class);

    @Inject
    private TextMessageRepository textmessageRepository;

    @Inject
    private TextMessageService textMessageService;

    /**
     * POST  /rest/textmessages -> Create a new textmessage.
     */
    @RequestMapping(value = "/rest/textmessages",
            method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    @RolesAllowed(AuthoritiesConstants.USER)
    public ResponseEntity<?> create(@RequestBody TextMessageDTO textMessageDTO) {
        log.debug("REST request to save TextMessage : {}", textMessageDTO);
        ResponseEntity<?> responseEntity;
        try {
            textMessageService.createTextMessage(textMessageDTO.getReceiver(), textMessageDTO.getContent());
            responseEntity = new ResponseEntity<>(HttpStatus.OK);
        } catch (NoSuchUserException e) {
            log.error("could not save text TextMessage", e);
            responseEntity = new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        return responseEntity;
    }

    /**
     * GET  /rest/textmessages -> get all the textmessages for the current user.
     */
    @RequestMapping(value = "/rest/textmessages",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    @RolesAllowed(AuthoritiesConstants.USER)
    public List<TextMessage> getAllForCurrentUser() {
        log.debug("REST request to get all TextMessages for receiver {}");
        return textMessageService.getTextMessagesForCurrentUser();
    }

    /**
     * GET  /rest/textmessages/:receiver -> get the textmessages for the given receiver
     */
    @RequestMapping(value = "/rest/textmessages/{receiver}",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    @RolesAllowed(AuthoritiesConstants.ADMIN)
    public List<TextMessage> getForUser(@PathVariable String receiver) {
        log.debug("REST request to get TextMessages for : {}", receiver);
        return textmessageRepository.findByReceiver(receiver);
    }

    /**
     * DELETE  /rest/textmessages/:id -> delete the "id" textmessage.
     */
    @RequestMapping(value = "/rest/textmessages/{id}",
            method = RequestMethod.DELETE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    @RolesAllowed(AuthoritiesConstants.ADMIN)
    public void delete(@PathVariable Long id) {
        log.debug("REST request to delete TextMessage : {}", id);
        textmessageRepository.delete(id);
    }
}
