package org.respondeco.respondeco.repository;

import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.respondeco.respondeco.Application;
import org.respondeco.respondeco.domain.*;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.AbstractTransactionalJUnit4SpringContextTests;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import org.springframework.test.context.support.DirtiesContextTestExecutionListener;
import org.springframework.test.context.transaction.TransactionalTestExecutionListener;
import org.springframework.test.context.web.WebAppConfiguration;

import javax.inject.Inject;
import java.util.List;

import static org.junit.Assert.*;

/**
 * Created by Clemens Puehringer on 02/12/14.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Application.class)
@WebAppConfiguration
@TestExecutionListeners({
    DependencyInjectionTestExecutionListener.class,
    DirtiesContextTestExecutionListener.class,
    TransactionalTestExecutionListener.class })
public class UserRepositoryTest extends AbstractTransactionalJUnit4SpringContextTests {

    @Inject
    private UserRepository userRepository;

    @Inject
    private OrganizationRepository organizationRepository;

    @Inject
    private PostingFeedRepository postingFeedRepository;

    private User user;
    private User user2;
    private User user3;

    private PostingFeed postingFeed;

    private Organization organization;

    @Before
    public void setup() {

        user = new User();
        user.setLogin("testuser");
        user.setGender(Gender.UNSPECIFIED);
        user.setActivationKey("key1");
        userRepository.save(user);

        user2 = new User();
        user2.setLogin("testuser2");
        user2.setGender(Gender.UNSPECIFIED);
        user.setActivationKey("key2");
        userRepository.save(user2);

        user3 = new User();
        user3.setLogin("testuser3");
        user3.setGender(Gender.UNSPECIFIED);

        user.setActivationKey("key3");
        userRepository.save(user3);

        postingFeed = new PostingFeed();
        postingFeedRepository.save(postingFeed);

        organization = new Organization();
        organization.setName("testorg");
        organization.setOwner(user3);
        organization.setPostingFeed(postingFeed);

        organizationRepository.save(organization);

        user3.setOrganization(organization);
        userRepository.save(user3);

    }
/*
    @Test
    public void testGetUserByActivationKey() {

        User savedUser = userRepository.getUserByActivationKey(user.getActivationKey());
        assertTrue(savedUser.equals(user));

        User savedUser2 = userRepository.getUserByActivationKey(user2.getActivationKey());
        assertTrue(savedUser2.equals(user2));

        User savedUser3 = userRepository.getUserByActivationKey(user3.getActivationKey());
        assertTrue(savedUser3.equals(user3));

    }
*/
    @Test
    public void testFindNotActivatedUsersByCreationDateBefore() {

        DateTime dateTime = DateTime.now();

        List<User> savedUsers = userRepository.findNotActivatedUsersByCreationDateBefore(dateTime);

        assertFalse(savedUsers.contains(user));
        assertFalse(savedUsers.contains(user2));
        assertFalse(savedUsers.contains(user3));
    }

    @Test
    public void testFindInvitableUsers() {

        List<User> savedUsers = userRepository.findInvitableUsers();

        assertNotNull(savedUsers);
        assertTrue(savedUsers.contains(user));
        assertTrue(savedUsers.contains(user2));
        assertFalse(savedUsers.contains(user3));

    }

    @Test
    public void testUsersByOrganizationId() {

        List<User> savedUsers = userRepository.findUsersByOrganizationId(organization.getId());

        assertNotNull(savedUsers);
        assertFalse(savedUsers.contains(user));
        assertFalse(savedUsers.contains(user2));
        assertTrue(savedUsers.contains(user3));

    }

    @Test
    public void testFindByLogin() {

        User savedUser = userRepository.findByLogin(user.getLogin());

        assertTrue(savedUser.equals(user));
    }

    @Test
    public void testFindUsernamesLike() {

        List<User> savedUserNames = userRepository.findUsersByNameLike("er2", null);

        assertTrue(savedUserNames.contains(user2));
    }

    @Test
    public void testFindByIdAndActiveIsTrue() {

        User savedUser = userRepository.findByIdAndActiveIsTrue(user.getId());

        assertTrue(savedUser.equals(user));

        user.setActive(false);
        userRepository.save(user);
        savedUser = userRepository.findByIdAndActiveIsTrue(user.getId());

        assertNull(savedUser);
    }
}
