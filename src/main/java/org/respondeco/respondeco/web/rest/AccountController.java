package org.respondeco.respondeco.web.rest;

import com.codahale.metrics.annotation.Timed;
import org.respondeco.respondeco.aop.RESTWrapped;
import org.respondeco.respondeco.domain.*;
import org.respondeco.respondeco.repository.PersistentTokenRepository;
import org.respondeco.respondeco.repository.UserRepository;
import org.respondeco.respondeco.security.AuthoritiesConstants;
import org.respondeco.respondeco.security.SecurityUtils;
import org.respondeco.respondeco.service.MailService;
import org.respondeco.respondeco.service.OrgJoinRequestService;
import org.respondeco.respondeco.service.OrganizationService;
import org.respondeco.respondeco.service.UserService;
import org.respondeco.respondeco.service.exception.*;
import org.respondeco.respondeco.web.rest.dto.*;
import org.apache.commons.lang.StringUtils;
import org.respondeco.respondeco.web.rest.util.RestParameters;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.thymeleaf.context.IWebContext;
import org.thymeleaf.spring4.SpringTemplateEngine;
import org.thymeleaf.spring4.context.SpringWebContext;

import javax.annotation.security.RolesAllowed;
import javax.inject.Inject;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.transaction.Transactional;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.*;
import java.util.stream.Collectors;

/**
 * REST controller for managing the current user's account.
 */
@RestController
@RequestMapping("/app")
@Transactional
public class AccountController {

    private final Logger log = LoggerFactory.getLogger(AccountController.class);

    @Inject
    private PasswordEncoder passwordEncoder;

    @Inject
    private ServletContext servletContext;

    @Inject
    private ApplicationContext applicationContext;

    @Inject
    private SpringTemplateEngine templateEngine;

    @Inject
    private UserRepository userRepository;

    @Inject
    private UserService userService;

    @Inject
    private OrganizationService organizationService;

    @Inject
    private PersistentTokenRepository persistentTokenRepository;

    @Inject
    private MailService mailService;

    @Inject
    private OrgJoinRequestService orgJoinRequestService;

    /**
     * POST  /rest/register -> register the user.
     */
    @RequestMapping(value = "/rest/register",
            method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<?> create(@RequestBody RegisterDTO registerDTO, HttpServletRequest request,
                                             HttpServletResponse response) {
        ResponseEntity<?> responseEntity;
        User user = userRepository.findByLogin(registerDTO.getEmail());
        if(user != null) {
            if (user.isActivated()) {
                responseEntity = new ResponseEntity<>(HttpStatus.NOT_MODIFIED);
            } else if (user.getOrganization() != null) {
                // user was invited to join respondeco via an organization
                user.setActivated(true);
                user.setActivationKey(null);
                user.setPassword(passwordEncoder.encode(registerDTO.getPassword()));
                userRepository.save(user);

                List<OrgJoinRequest> orgJoinRequests = orgJoinRequestService.getOrgJoinRequestByUser(user);

                for (OrgJoinRequest orgJoinRequest : orgJoinRequests) {
                    try {
                        if (orgJoinRequest.getOrganization().equals(user.getOrganization())) {
                            orgJoinRequestService.acceptRequest(orgJoinRequest.getId(), user);
                        } else {
                            orgJoinRequestService.declineRequest(orgJoinRequest.getId());
                        }
                    } catch (NoSuchEntityException e) {
                        return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
                    }
                }

                responseEntity = new ResponseEntity<>(HttpStatus.OK);
            } else {
                responseEntity = new ResponseEntity<>(HttpStatus.BAD_REQUEST);
            }
        } else {
            user = userService.createUserInformation(registerDTO.getEmail().toLowerCase(),
                registerDTO.getPassword(), null, null, null, registerDTO.getEmail().toLowerCase(),
                "UNSPECIFIED", null, registerDTO.getLangKey(), null);

            // #90 create the organization when the user registers
            organizationService.createOrganizationInformation(registerDTO.getOrganization(), null, registerDTO.getEmail(), false, null, null, user);

            final Locale locale = Locale.forLanguageTag(registerDTO.getLangKey());
            String content = createHtmlContentFromTemplate(user, locale, request,
                response, "activationEmail");
            mailService.sendActivationEmail(registerDTO.getEmail(), content, locale);
            responseEntity = new ResponseEntity<>(HttpStatus.CREATED);
        }

        return responseEntity;
    }

    /**
     * GET  /rest/account -> get the current user.
     */
    @RequestMapping(value = "/rest/account",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    @RESTWrapped
    public Object getCurrent() {
        return userService.getUserWithAuthorities();
    }

    /**
     * POST  /rest/account -> update the current user information.
     */
    @RequestMapping(value = "/rest/account",
        method = RequestMethod.POST,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public void update(@RequestBody UserDTO userDTO) {
        userService.updateUserInformation(userDTO.getTitle(), userDTO.getGender(), userDTO.getFirstName(),
            userDTO.getLastName(), userDTO.getEmail(), userDTO.getDescription(), userDTO.getProfilePicture());
    }

    /**
     * DELETE /rest/account -> deactivate the account
     */
    @RequestMapping(value = "/rest/account",
        method = RequestMethod.DELETE)
    @RolesAllowed(AuthoritiesConstants.USER)
    @Timed
    @RESTWrapped
    public void delete() {
        User currentUser = userService.getUserWithAuthorities();
        if(currentUser != null) {
            currentUser.setActive(false);
            userRepository.save(currentUser);
        }
    }

    /**
     * GET  /rest/activate -> activate the registered user.
     */
    @RequestMapping(value = "/rest/activate",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    @RESTWrapped
    public Object activateAccount(@RequestParam(value = "key") String key) {
        return Optional.ofNullable(userService.activateRegistration(key))
            .map(user -> {


                return user.getLogin();
            })
            .orElse(null); // TODO: generic return value for 404
    }

    /**
     * GET  /rest/authenticate -> check if the user is authenticated, and return its login.
     */
    @RequestMapping(value = "/rest/authenticate",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public String isAuthenticated(HttpServletRequest request) {
        log.debug("REST request to check if the current user is authenticated");
        return request.getRemoteUser();
    }

    /**
     * POST  /rest/change_password -> changes the current user's password
     */
    @RequestMapping(value = "/rest/account/change_password",
            method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    @RESTWrapped
    public void changePassword(@RequestBody String password) {
        if (StringUtils.isEmpty(password))
            throw new OperationForbiddenException("Password cannot be empty.");
        userService.changePassword(password);
    }

    /**
     * GET  /rest/account/sessions -> get the current open sessions.
     */
    @RequestMapping(value = "/rest/account/sessions",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    @RESTWrapped
    public Object getCurrentSessions() {
        return Optional.ofNullable(userRepository.findByLogin(SecurityUtils.getCurrentLogin()))
            .map(user -> persistentTokenRepository.findByUser(user))
            .orElse(null); // TODO: generic return value for 404
    }

    /**
     * DELETE  /rest/account/sessions?series={series} -> invalidate an existing session.
     *
     * - You can only delete your own sessions, not any other user's session
     * - If you delete one of your existing sessions, and that you are currently logged in on that session, you will
     *   still be able to use that session, until you quit your browser: it does not work in real time (there is
     *   no API for that), it only removes the "remember me" cookie
     * - This is also true if you invalidate your current session: you will still be able to use it until you close
     *   your browser or that the session times out. But automatic login (the "remember me" cookie) will not work
     *   anymore.
     *   There is an API to invalidate the current session, but there is no API to check which session uses which
     *   cookie.
     */
    @RequestMapping(value = "/rest/account/sessions/{series}",
            method = RequestMethod.DELETE)
    @Timed
    public void invalidateSession(@PathVariable String series) throws UnsupportedEncodingException {
        String decodedSeries = URLDecoder.decode(series, "UTF-8");
        User user = userRepository.findByLogin(SecurityUtils.getCurrentLogin());
        if (persistentTokenRepository.findByUser(user).stream()
                .filter(persistentToken -> StringUtils.equals(persistentToken.getSeries(), decodedSeries))
                .count() > 0) {

            persistentTokenRepository.delete(decodedSeries);
        }
    }

    public String createHtmlContentFromTemplate(final User user, final Locale locale, final HttpServletRequest request,
                                                 final HttpServletResponse response, final String template) {
        Map<String, Object> variables = new HashMap<>();
        variables.put("user", user);
        variables.put("baseUrl", request.getScheme() + "://" +   // "http" + "://
            request.getServerName() +       // "myhost"
            ":" + request.getServerPort() + "/app.html");
        IWebContext context = new SpringWebContext(request, response, servletContext,
            locale, variables, applicationContext);
        return templateEngine.process(template, context);
    }

    /**
     * POST  /rest/leaveOrg
     */
    @RequestMapping(value = "/rest/account/leaveorganization",
            method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    @RolesAllowed(AuthoritiesConstants.USER)
    @RESTWrapped
    public void leaveOrganization() {
        log.debug("REST request to leave Organization : {}");
        userService.leaveOrganization();
    }

    /**
     * GET  /rest/orgjoinrequests/:organization -> get the "organization" orgjoinrequest.
     */
    @RolesAllowed(AuthoritiesConstants.USER)
    @RequestMapping(value = "/rest/account/orgjoinrequests",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    @RESTWrapped
    public Object getByCurrentUser() {
        log.debug("REST request to get OrgJoinRequest as User: {}");
        return orgJoinRequestService.getOrgJoinRequestByCurrentUser();
    }

    /**
     * method to get the newsfeed of the current account; newsfeed is defined by the followed projects/organizations
     * @param page the given page used to create the pagerequest (0 by default)
     * @param pageSize the given pagesize (elements of the page;20 by default) used to create the pagerequest
     * @return returns OK and a PostingPaginationResponseDTO with found postings
     */
    @RequestMapping(value = "/rest/account/newsfeed",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    @RolesAllowed(AuthoritiesConstants.USER)
    @RESTWrapped
    public Object getNewsFeed(@RequestParam(required = false) Integer page,
                              @RequestParam(required = false) Integer pageSize) {
        RestParameters restParameters = new RestParameters(page, pageSize);
        List<Posting> postings = new ArrayList<>();

        Page<Posting> currentPage = userService.getNewsfeed(restParameters);
        postings.addAll(currentPage.getContent().stream().collect(Collectors.toList()));
        return postings;
    }
}
