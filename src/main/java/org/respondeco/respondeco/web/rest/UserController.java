package org.respondeco.respondeco.web.rest;

import com.codahale.metrics.annotation.Timed;
import org.respondeco.respondeco.domain.Organization;
import org.respondeco.respondeco.domain.User;
import org.respondeco.respondeco.repository.OrganizationRepository;
import org.respondeco.respondeco.repository.UserRepository;
import org.respondeco.respondeco.security.AuthoritiesConstants;
import org.respondeco.respondeco.service.UserService;
import org.respondeco.respondeco.service.exception.NoSuchOrganizationException;
import org.respondeco.respondeco.service.exception.NoSuchUserException;
import org.respondeco.respondeco.service.exception.NotOwnerOfOrganizationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.MediaType;
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
public class UserController {

    private final Logger log = LoggerFactory.getLogger(UserController.class);

    private UserRepository userRepository;
    private UserService userService;

    @Inject
    public UserController(UserRepository userRepository, UserService userService) {
        this.userRepository = userRepository;
        this.userService = userService;
    }

    /**
     * GET  /rest/users/:login -> get the "login" user.
     */
    @RequestMapping(value = "/rest/users/{login}",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    @RolesAllowed(AuthoritiesConstants.ADMIN)
    ResponseEntity<User> getUser(@PathVariable String login) {
        log.debug("REST request to get User : {}", login);
        return Optional.ofNullable(userRepository.findByLogin(login))
                .map(user -> new ResponseEntity<>(user, HttpStatus.OK))
                .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    /**
     * GET  /rest/users/:login -> get the "login" user.
     */
    @RequestMapping(value = "/rest/users/getByOrgId/{orgId}",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    @RolesAllowed(AuthoritiesConstants.USER)
    ResponseEntity<List<User>> getUserByOrgId(@PathVariable Long orgId) throws NoSuchOrganizationException, NotOwnerOfOrganizationException {
        log.debug("REST request to get Users by OrgId : {}", orgId);
        return Optional.ofNullable(userService.getUserByOrgId(orgId))
                .map(user -> new ResponseEntity<>(user, HttpStatus.OK))
                .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    /**
     * POST  /rest/deleteMember-> changes the current user's password
     */
    @RequestMapping(value = "/rest/user/deleteMember/{userlogin}",
            method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    @RolesAllowed(AuthoritiesConstants.USER)
    public void deleteMember(@PathVariable String userlogin) throws NoSuchOrganizationException, NoSuchUserException, NotOwnerOfOrganizationException {
        log.debug("REST request to delete Member : {}", userlogin);
        userService.deleteMember(userlogin);
    }

    /**
     * GET  /rest/users/find?filter= -> get usernames matching the filter parameter
     */
    @RequestMapping(value = "/rest/names/users",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    @RolesAllowed(AuthoritiesConstants.USER)
    public List<String> getMatchingUsernames(
            @RequestParam(required = false) String filter,
            @RequestParam(required = false) Integer limit) {
        log.debug("REST request to get usernames matching : {}", filter);
        if(filter == null) {
            filter = "";
        }
        if(limit == null) {
            limit = 20;
        }
        return userService.findUsernamesLike(filter, limit);
    }

    /**
     * POST  /rest/leaveOrg
     */
    @RequestMapping(value = "/rest/user/deleteMember/leaveOrganization",
            method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    @RolesAllowed(AuthoritiesConstants.USER)
    public void leaveOrganization() {
        log.debug("REST request to leave Organization : {}");
        userService.leaveOrganization();
    }
}
