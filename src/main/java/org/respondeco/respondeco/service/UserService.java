package org.respondeco.respondeco.service;

import org.respondeco.respondeco.domain.*;
import org.respondeco.respondeco.repository.AuthorityRepository;
import org.respondeco.respondeco.repository.OrganizationRepository;
import org.respondeco.respondeco.repository.PersistentTokenRepository;
import org.respondeco.respondeco.repository.UserRepository;
import org.respondeco.respondeco.security.SecurityUtils;
import org.respondeco.respondeco.service.exception.NoSuchOrganizationException;
import org.respondeco.respondeco.service.exception.NoSuchUserException;
import org.respondeco.respondeco.service.exception.NotOwnerOfOrganizationException;
import org.respondeco.respondeco.service.util.RandomUtil;
import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

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



    public User activateRegistration(String key) {
        log.debug("Activating user for activation key {}", key);
        return Optional.ofNullable(userRepository.getUserByActivationKey(key))
            .map(user -> {
                // activate given user for the registration key.
                user.setActivated(true);
                user.setActivationKey(null);
                userRepository.save(user);
                log.debug("Activated user: {}", user);
                return user;
            })
            .orElse(null);
    }

    public User createUserInformation(String login, String password, String title, String firstName, String lastName,
                                      String email, String gender, String description, String langKey) {
        User newUser = new User();
        Authority authority = authorityRepository.findOne("ROLE_USER");
        Set<Authority> authorities = new HashSet<>();
        String encryptedPassword = passwordEncoder.encode(password);
        newUser.setLogin(login);
        newUser.setOrgId(null);
        // new user gets initially a generated password
        newUser.setPassword(encryptedPassword);
        newUser.setTitle(title);
        if(gender == null) {
            newUser.setGender(Gender.UNSPECIFIED);
        } else {
            newUser.setGender(Gender.valueOf(gender));
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
        userRepository.save(newUser);
        log.debug("Created Information for User: {}", newUser);
        return newUser;
    }

    public void updateUserInformation(String title, String gender, String firstName, String lastName, String email,
                                      String description) {
        User currentUser = getUserWithAuthorities();
        currentUser.setTitle(title);
        Gender newGender = Gender.valueOf(gender);
        if(newGender == null) {
            currentUser.setGender(Gender.UNSPECIFIED);
        } else {
            currentUser.setGender(newGender);
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

    public void deleteMember(String userlogin) throws NoSuchUserException, NoSuchOrganizationException, NotOwnerOfOrganizationException {
        User user = getUserWithAuthorities();
        User member = userRepository.findByLogin(userlogin);
        Organization organization = organizationRepository.findOne(member.getOrgId());

        if(member == null) {
            throw new NoSuchUserException(String.format("User %s does not exist", userlogin));
        }
        if(organization == null) {
            throw new NoSuchOrganizationException(String.format("Organization %s does not exist", member.getOrgId()));
        }
        if(organization.getOwner().equals(user.getLogin())==false) {
            throw new NotOwnerOfOrganizationException(String.format("Current User is not owner of Organization %s ", organization.getOwner()));
        }
        log.debug("Deleting member from organization", user.getLogin(), organization.getName());
        member.setOrgId(null);
    }

    public void leaveOrganization() {
        User user = getUserWithAuthorities();

        log.debug("Leaving organization");
        user.setOrgId(null);
    }

    public List<User> getUserByOrgId(Long orgId) throws NoSuchOrganizationException, NotOwnerOfOrganizationException {
        Organization organization = organizationRepository.findOne(orgId);
        User user = getUserWithAuthorities();
        if(organization == null) {
            throw new NoSuchOrganizationException(String.format("Organization %s does not exist", orgId));
        }
        if(organization.getOwner().equals(user)==false) {
            throw new NotOwnerOfOrganizationException(String.format("Current User is not owner of Organization %s", orgId));
        }
        log.debug("Finding members of organization", organization.getName());
        return userRepository.findUserByOrgId(orgId);
    }

    public List<String> findUsernamesByRegex(String usernamePart) {
        List<String> result = userRepository.findUsernamesByRegex("%" + usernamePart + "%");
        result.remove("system");
        result.remove("anonymousUser");
        return result;
    }

    public List<User> findInvitableUsersByOrgId(Long orgId) {
        Organization organization = organizationRepository.getOne(orgId);

        // if there is no organization than all users should be returned
        if(organization != null) {
            List<User> users = userRepository.findInvitableUsers();
            User owner = null;

            // find the owner and remove him from the list
            // @TODO set the orgId of the owner when set as owner
            for (User user: users) {
                if (organization.getOwner().equals(user.getLogin())) {
                    owner = user;
                    break;
                }
            }

            if (owner != null) {
                users.remove(owner);
            }

            return users;
        }
        return userRepository.findAll();
    }

    public List<User> getOrganizationMembers(Long id) {
        return userRepository.findUserByOrgId(id);
    }
}
