package org.respondeco.respondeco.service;

import org.respondeco.respondeco.domain.*;
import org.respondeco.respondeco.repository.*;
import org.respondeco.respondeco.security.SecurityUtils;
import org.respondeco.respondeco.service.exception.NoSuchEntityException;
import org.respondeco.respondeco.service.util.RandomUtil;
import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.respondeco.respondeco.web.rest.dto.ImageDTO;
import org.respondeco.respondeco.web.rest.util.RestParameters;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.WebAuthenticationDetails;
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
@Transactional
public class UserService {

    private final Logger log = LoggerFactory.getLogger(UserService.class);

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

                // Create user-password token
                UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(user.getLogin(), user.getPassword());

                // Get request
                ServletRequestAttributes attr = (ServletRequestAttributes) RequestContextHolder.currentRequestAttributes();

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

    public User createUserInformation(String login, String password, String title, String firstName, String lastName,
                                       String email, String gender, String description, String langKey, ImageDTO profilePicture) {
        return createUserInformation(login, password, title, firstName, lastName, email, gender, description, langKey, profilePicture, false);
    }

    public User createUserInformation(String login, String password, String title, String firstName, String lastName,
                                      String email, String gender, String description, String langKey, ImageDTO profilePicture, Boolean invited) {
        User newUser = new User();
        Authority authority = authorityRepository.findOne("ROLE_USER");
        Set<Authority> authorities = new HashSet<>();
        String encryptedPassword = passwordEncoder.encode(password);
        newUser.setLogin(login);
        newUser.setOrganization(null);
        newUser.setPassword(encryptedPassword);
        newUser.setTitle(title);
        if(gender == null) {
            newUser.setGender(Gender.UNSPECIFIED);
        } else {
            newUser.setGender(Gender.valueOf(gender));
        }

        if (profilePicture != null && profilePicture.getId() != null) {
            Image image = imageRepository.findOne(profilePicture.getId());
            newUser.setProfilePicture(image);
        }
        newUser.setFirstName(firstName);
        newUser.setLastName(lastName);
        newUser.setEmail(email);
        newUser.setDescription(description);
        newUser.setLangKey(langKey);
        // new user is not active
        newUser.setActivated(false);
        // new user gets registration key
        newUser.setActivationKey(RandomUtil.generateActivationKey());
        authorities.add(authority);
        newUser.setAuthorities(authorities);
        // user can be invited by an organization
        newUser.setInvited(invited);
        userRepository.save(newUser);
        log.debug("Created Information for User: {}", newUser);
        return newUser;
    }

    public void updateUserInformation(String title, String gender, String firstName, String lastName, String email,
                                      String description, ImageDTO profilePicture) {
        User currentUser = getUserWithAuthorities();
        currentUser.setTitle(title);
        Gender newGender = Gender.valueOf(gender);
        if(newGender == null) {
            currentUser.setGender(Gender.UNSPECIFIED);
        } else {
            currentUser.setGender(newGender);
        }

        if (profilePicture != null && profilePicture.getId() != null) {
            Image image = imageRepository.findOne(profilePicture.getId());
            currentUser.setProfilePicture(image);
        }

        currentUser.setFirstName(firstName);
        currentUser.setLastName(lastName);
        currentUser.setEmail(email);
        currentUser.setDescription(description);
        userRepository.save(currentUser);
        log.debug("Changed Information for User: {}", currentUser);
    }

    public void changePassword(String password) {
        User currentUser = userRepository.findByLogin(SecurityUtils.getCurrentLogin());
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
        return userRepository.findUsersByNameLike(usernamePart, restParameters.buildPageRequest());
    }

    public User getUser(Long id) throws NoSuchEntityException {
        User user = userRepository.findByIdAndActiveIsTrue(id);
        if(user == null) {
            throw new NoSuchEntityException(id);
        }
        return user;
    }

    public void setOrganization(User user, Long id) {
        user.setOrganization(organizationRepository.findByIdAndActiveIsTrue(id));
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
