package org.respondeco.respondeco.repository;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.respondeco.respondeco.Application;
import org.respondeco.respondeco.domain.Gender;
import org.respondeco.respondeco.domain.SocialMediaConnection;
import org.respondeco.respondeco.domain.User;
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

import static org.junit.Assert.assertEquals;

/**
 * Created by Benjamin Fraller on 17.01.2015.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Application.class)
@WebAppConfiguration
@TestExecutionListeners({
    DependencyInjectionTestExecutionListener.class,
    DirtiesContextTestExecutionListener.class,
    TransactionalTestExecutionListener.class })
public class SocialMediaRepositoryTest extends AbstractTransactionalJUnit4SpringContextTests{

    @Inject
    private SocialMediaRepository socialMediaRepository;

    @Inject
    private UserRepository userRepository;

    private User user;

    private SocialMediaConnection socialMediaConnection;

    @Before
    public void setup() {
        user = new User();
        user.setLogin("testuser");
        user.setGender(Gender.MALE);
        userRepository.save(user);

        socialMediaConnection = new SocialMediaConnection();
        socialMediaConnection.setToken("token");
        socialMediaConnection.setProvider("facebook");
        socialMediaConnection.setUser(user);
        socialMediaRepository.save(socialMediaConnection);

        socialMediaConnection = new SocialMediaConnection();
        socialMediaConnection.setToken("token");
        socialMediaConnection.setSecret("secret");
        socialMediaConnection.setProvider("twitter");
        socialMediaConnection.setUser(user);
        socialMediaRepository.save(socialMediaConnection);
    }


    @Test
    public void testFindByUserAndProvider() {
        SocialMediaConnection connection = socialMediaRepository.findByUserAndProvider(user, "facebook");
        assertEquals(connection.getProvider(), "facebook");
        assertEquals(connection.getToken(), "token");
        assertEquals(connection.getUser().getId().longValue(), user.getId().longValue());
    }

    @Test
    public void testFindByUser() {
        List<SocialMediaConnection> connections = socialMediaRepository.findByUser(user);

        assertEquals(connections.size(), 2);
        assertEquals(connections.get(0).getProvider(), "facebook");
        assertEquals(connections.get(1).getProvider(), "twitter");
    }


}
