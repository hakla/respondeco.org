package org.respondeco.respondeco.web.rest;

import com.codahale.metrics.annotation.Timed;
import org.respondeco.respondeco.domain.Organization;
import org.respondeco.respondeco.domain.Posting;
import org.respondeco.respondeco.domain.User;
import org.respondeco.respondeco.repository.OrganizationRepository;
import org.respondeco.respondeco.repository.UserRepository;
import org.respondeco.respondeco.security.AuthoritiesConstants;
import org.respondeco.respondeco.service.TextMessageService;
import org.respondeco.respondeco.service.UserService;
import org.respondeco.respondeco.service.exception.NoSuchOrganizationException;
import org.respondeco.respondeco.service.exception.NoSuchProjectException;
import org.respondeco.respondeco.service.exception.NoSuchUserException;
import org.respondeco.respondeco.service.exception.NotOwnerOfOrganizationException;
import org.respondeco.respondeco.web.rest.dto.PostingDTO;
import org.respondeco.respondeco.web.rest.dto.PostingPaginationResponseDTO;
import org.respondeco.respondeco.web.rest.dto.UserDTO;
import org.respondeco.respondeco.web.rest.util.RestParameters;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import javax.annotation.security.RolesAllowed;
import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * REST controller for managing users.
 */
@RestController
@RequestMapping("/app")
public class UserController {

    private final Logger log = LoggerFactory.getLogger(UserController.class);

    private UserRepository userRepository;
    private UserService userService;
    private TextMessageService textMessageService;

    @Inject
    public UserController(UserRepository userRepository, UserService userService, TextMessageService textMessageService) {
        this.userRepository = userRepository;
        this.userService = userService;
        this.textMessageService = textMessageService;
    }

    /**
     * GET  /rest/users/:login -> get the "login" user.
     */
    @RequestMapping(value = "/rest/users/{id}",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    @RolesAllowed(AuthoritiesConstants.ADMIN)
    ResponseEntity<User> getUser(@PathVariable Long id) {
        log.debug("REST request to get User : {}", id);
        return Optional.ofNullable(userRepository.findOne(id))
            .map(user -> new ResponseEntity<>(user, HttpStatus.OK))
            .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    /**
     * GET  /rest/users -> get users where the name matches the filter parameter
     */
    @RequestMapping(value = "/rest/users",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    @RolesAllowed(AuthoritiesConstants.USER)
    public List<UserDTO> getMatchingUsers(
            @RequestParam(required = false) String filter,
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer pageSize,
            @RequestParam(required = false) String order,
            @RequestParam(required = false) String fields) {
        log.debug("REST request to get usernames matching : {}", filter);
        RestParameters restParameters = new RestParameters(page, pageSize, order, fields);
        List<User> users = userService.findUsersByNameLike(filter, restParameters);
        return UserDTO.fromEntities(users, restParameters.getFields());
    }


}
