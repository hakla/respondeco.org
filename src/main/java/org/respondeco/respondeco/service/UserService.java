package org.respondeco.respondeco.service;

import org.respondeco.respondeco.domain.*;
import org.respondeco.respondeco.repository.*;
import org.respondeco.respondeco.security.AuthoritiesConstants;
import org.respondeco.respondeco.security.SecurityUtils;
import org.respondeco.respondeco.service.exception.NoSuchEntityException;
import org.respondeco.respondeco.service.exception.OperationForbiddenException;
import org.respondeco.respondeco.service.exception.ServiceException.ErrorPrefix;
import org.respondeco.respondeco.service.util.Assert;
import org.respondeco.respondeco.service.util.RandomUtil;
import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.respondeco.respondeco.web.rest.util.RestParameters;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.WebAuthenticationDetails;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationToken;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.inject.Inject;
import java.util.*;

/**
 * Service class for managing users.
 */
@Service
public class UserService {

    private static final Logger log = LoggerFactory.getLogger(UserService.class);

    public static final ErrorPrefix ERROR_PREFIX        = new ErrorPrefix("user");
    public static final String ERROR_NO_AUTHORIZATION   = "no_authorization";
    public static final String ERROR_PASSWORD_INVALID   = "password_invalid";

    @Inject
    private PasswordEncoder passwordEncoder;

    @Inject
    private UserRepository userRepository;

    @Inject
    private PersistentTokenRepository persistentTokenRepository;

    @Inject
    private AuthorityRepository authorityRepository;

    @Inject
    private OrganizationRepository organizationRepository;

    @Inject
    private ImageRepository imageRepository;

    @Inject
    private PostingFeedRepository postingFeedRepository;

    @Inject
    AuthenticationManager authenticationManager;

    public User activateRegistration(String key) {
        log.debug("Activating user for activation key {}", key);
        return Optional.ofNullable(userRepository.getUserByActivationKey(key))
            .map(user -> {
                // activate given user for the registration key.
                user.setActivated(true);
                user.setActivationKey(null);
                userRepository.save(user);

                log.debug("Activated user: {}", user);

                /*
                 * Create authentication token
                 *
                 * Because the default PreAuthenticatedAuthenticationProvider checks if the given credentials are not null
                 * (this isn't necessary because they won't be checked, no idea why they do it) we provide an empty but non-null value
                 */
                PreAuthenticatedAuthenticationToken token = new PreAuthenticatedAuthenticationToken(user.getLogin(), "", user.getAuthorities());

                // Get request
                ServletRequestAttributes attr = (ServletRequestAttributes) RequestContextHolder.currentRequestAttributes();

                // Create a session if there isn't already one
                attr.getRequest().getSession(true);

                // Set request
                token.setDetails(new WebAuthenticationDetails(attr.getRequest()));

                // Create authentication object
                Authentication authentication = authenticationManager.authenticate(token);

                // Set authentication - user should be logged in now
                SecurityContextHolder.getContext().setAuthentication(authentication);

                log.debug("User {} is now logged in", user);

                return user;
            })
            .orElse(null);
    }

    public User create(User user) {
        return create(user, false);
    }

    public User create(User user, Boolean invited) {
        Authority authority = authorityRepository.findOne("ROLE_USER");
        Set<Authority> authorities = new HashSet<>();
        String encryptedPassword = passwordEncoder.encode(user.getPassword());
        user.setPassword(encryptedPassword);
        if(user.getGender() == null) {
            user.setGender(Gender.UNSPECIFIED);
        }

        // new user is not active
        user.setActivated(false);
        // new user gets registration key
        user.setActivationKey(RandomUtil.generateActivationKey());
        authorities.add(authority);
        user.setAuthorities(authorities);
        // user can be invited by an organization
        user.setInvited(invited);
        userRepository.save(user);
        log.debug("Created User: {}", user);
        return user;
    }

    public void update(User updatedUser) {
        User currentUser = getUserWithAuthorities();
        if(updatedUser.getGender() == null) {
            currentUser.setGender(Gender.UNSPECIFIED);
        } else {
            currentUser.setGender(updatedUser.getGender());
        }
        currentUser.setTitle(updatedUser.getTitle());
        currentUser.setFirstName(updatedUser.getFirstName());
        currentUser.setLastName(updatedUser.getLastName());
        currentUser.setEmail(updatedUser.getEmail());
        currentUser.setDescription(updatedUser.getDescription());
        currentUser.setProfilePicture(updatedUser.getProfilePicture());
        userRepository.save(currentUser);
        log.debug("Changed Information for User: {}", currentUser);
    }

    public void delete(User user) {
        User currentUser = getUserWithAuthorities();
        if(currentUser == null) {
            throw new OperationForbiddenException(ERROR_PREFIX.join(ERROR_NO_AUTHORIZATION),
                "Current user does not have proper authorization to delete user %1",
                Arrays.asList("targetUserId"),
                Arrays.asList(user.getId()));
        }
        if(!isAdmin(user) && !currentUser.equals(user)) {
            throw new OperationForbiddenException(ERROR_PREFIX.join(ERROR_NO_AUTHORIZATION),
                "Current user (%2) does not have proper authorization to delete user %4",
                Arrays.asList("currentUserId", "currentUserLogin", "targetUserId", "targetUserLogin"),
                Arrays.asList(currentUser.getId(), currentUser.getLogin(), user.getId(), user.getLogin()));
        }
        userRepository.delete(user);
    }

    public void changePassword(String password) {
        Assert.isValid(password, 8, 100, ERROR_PREFIX.join(ERROR_PASSWORD_INVALID),
            "Password invalid, please make sure that it is between 8 and 100 characters long.", null, null);
        User currentUser = getUserWithAuthorities();
        String encryptedPassword = passwordEncoder.encode(password);
        currentUser.setPassword(encryptedPassword);
        userRepository.save(currentUser);
        log.debug("Changed password for User: {}", currentUser);
    }

    @Transactional(readOnly = true)
    public User getUserWithAuthorities() {
        log.debug("getUserWithAuthorities() called");
        String currentLogin = SecurityUtils.getCurrentLogin();
        User currentUser = null;
        if(currentLogin != null) {
            currentUser = userRepository.findByLogin(currentLogin);
            currentUser.getAuthorities().size(); // eagerly load the association
        }
        return currentUser;
    }

    public Boolean isAdmin(User user) {
        if(user != null && user.getAuthorities() != null) {
            for(Authority authority : user.getAuthorities()) {
                if(AuthoritiesConstants.ADMIN.equals(authority.getName())) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Persistent Token are used for providing automatic authentication, they should be automatically deleted after
     * 30 days.
     * <p/>
     * <p>
     * This is scheduled to get fired everyday, at midnight.
     * </p>
     */
    @Scheduled(cron = "0 0 0 * * ?")
    public void removeOldPersistentTokens() {
        LocalDate now = new LocalDate();
        List<PersistentToken> tokens = persistentTokenRepository.findByTokenDateBefore(now.minusMonths(1));
        for (PersistentToken token : tokens) {
            log.debug("Deleting token {}", token.getSeries());
            User user = token.getUser();
            user.getPersistentTokens().remove(token);
            persistentTokenRepository.delete(token);
        }
    }

    /**
     * Not activated users should be automatically deleted after 3 days.
     * <p/>
     * <p>
     * This is scheduled to get fired everyday, at 01:00 (am).
     * </p>
     */
    @Scheduled(cron = "0 0 1 * * ?")
    public void removeNotActivatedUsers() {
        DateTime now = new DateTime();
        List<User> users = userRepository.findNotActivatedUsersByCreationDateBefore(now.minusDays(3));
        for (User user : users) {
            log.debug("Deleting not activated user {}", user.getLogin());
            userRepository.delete(user);
        }
    }

    public void leaveOrganization() {
        User user = getUserWithAuthorities();

        log.debug("Leaving organization");
        user.setOrganization(null);
    }

    public Page<User> findUsersByNameLike(String usernamePart, RestParameters restParameters) {
        return userRepository.findByLoginLike(usernamePart, restParameters.buildPageRequest());
    }

    public User getUser(Long id) throws NoSuchEntityException {
        User user = userRepository.findOne(id);
        if(user == null) {
            throw new NoSuchEntityException(ERROR_PREFIX, id, User.class);
        }
        return user;
    }

    public void setOrganization(User user, Long id) {
        user.setOrganization(organizationRepository.findOne(id));
        userRepository.save(user);
    }

    public User findUserByLogin(String loginName) {
        return userRepository.findByLogin(loginName);
    }

    /**
     * method to get the newsfeed (page of postings) for the given user
     * newsfeed is defined by the postings from organization and projects which the user is following
     * @param restParameters given from the controller; embodies information to create the pagerequest
     * @return a page of postings found by followed organization and projects
     */
    public Page<Posting> getNewsfeed(RestParameters restParameters) {
        // get the signed in user
        User user = getUserWithAuthorities();

        PageRequest pageable = restParameters.buildPageRequest();

        Page<Posting> posts;

        List<Long> organizationIds = new ArrayList<>();
        List<Long> projectIds = new ArrayList<>();

        // get all organizations the user currently follows
        List<Organization> organizations = user.getFollowOrganizations();
        if(organizations != null) {

            // and collect the ids of the organizations
            for (Organization organization : organizations) {
                organizationIds.add(organization.getId());
            }
        }

        // get all projects the user currently follows
        List<Project> projects = user.getFollowProjects();
        if(projects != null) {

            // and collect the ids of the projects
            for(Project project : projects) {
                projectIds.add(project.getId());
            }
        }

        if (organizationIds.size() == 0) {
            // get all postings for a given set of projectIds
            posts = postingFeedRepository.getPostingsForProjects(projectIds, pageable);
        } else if (projectIds.size() == 0) {
            // get all postings for a given set of organizationIds
            posts = postingFeedRepository.getPostingsForOrganizations(organizationIds, pageable);
        } else {
            // get all postings for the given sets of organizationIds and projectIds
            posts = postingFeedRepository.getPostingsForOrganizationsAndProjects(organizationIds, projectIds, pageable);
        }

        return posts;
    }
}
