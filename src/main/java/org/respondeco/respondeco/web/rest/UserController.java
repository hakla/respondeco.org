package org.respondeco.respondeco.web.rest;

import com.codahale.metrics.annotation.Timed;
import org.respondeco.respondeco.aop.RESTWrapped;
import org.respondeco.respondeco.domain.User;
import org.respondeco.respondeco.security.AuthoritiesConstants;
import org.respondeco.respondeco.service.UserService;
import org.respondeco.respondeco.service.exception.NoSuchEntityException;
import org.respondeco.respondeco.web.rest.dto.UserDTO;
import org.respondeco.respondeco.web.rest.util.RestParameters;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import javax.annotation.security.RolesAllowed;
import javax.inject.Inject;
import java.util.List;
import java.util.Optional;

/**
 * REST controller for managing users.
 */
@RestController
@RequestMapping("/app")
@Transactional
public class UserController {

    private final Logger log = LoggerFactory.getLogger(UserController.class);

    private UserService userService;

    @Inject
    public UserController(UserService userService) {
        this.userService = userService;
    }

    /**
     * GET  /rest/users/:id -> get the "id" user.
     */
    @RequestMapping(value = "/rest/users/{id}",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    @RolesAllowed(AuthoritiesConstants.ADMIN)
    @RESTWrapped
    public Object getUser(@PathVariable Long id) {
        log.debug("REST request to get User : {}", id);
        return userService.getUser(id);
    }

    /**
     * GET  /rest/users -> get users where the name matches the filter parameter
     */
    @RequestMapping(value = "/rest/users",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    @RolesAllowed(AuthoritiesConstants.USER)
    @RESTWrapped
    public Object getMatchingUsers(
        @RequestParam(required = false) String filter,
        @RequestParam(required = false) Integer page,
        @RequestParam(required = false) Integer pageSize,
        @RequestParam(required = false) String order,
        @RequestParam(required = false) String fields) {
        log.debug("REST request to get usernames matching : {}", filter);
        RestParameters restParameters = new RestParameters(page, pageSize, order);
        return userService.findUsersByNameLike(filter, restParameters);
    }


}
