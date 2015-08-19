package org.respondeco.respondeco.web.rest;

import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.respondeco.respondeco.Application;
import org.respondeco.respondeco.domain.Gender;
import org.respondeco.respondeco.domain.User;
import org.respondeco.respondeco.repository.*;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.respondeco.respondeco.service.TextMessageService;
import org.respondeco.respondeco.service.UserService;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import javax.inject.Inject;

import java.util.ArrayList;
import java.util.HashSet;

import static org.mockito.Mockito.doReturn;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Test class for the UserController REST controller.
 *
 * @see UserController
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Application.class)
@WebAppConfiguration
@DirtiesContext(classMode= DirtiesContext.ClassMode.AFTER_CLASS)

public class UserControllerTest {

    private MockMvc restUserMockMvc;

    @Mock
    private UserService userService;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private OrganizationRepository organizationRepository;

    @Mock
    private PersistentTokenRepository persistentTokenRepository;

    @Mock
    private AuthorityRepository authorityRepository;

    @Mock
    private TextMessageService textMessageService;

    private User user;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        UserController userController = new UserController(userService);
        this.restUserMockMvc = MockMvcBuilders.standaloneSetup(userController).build();

        user = new User();
        user.setId(1L);
        user.setLogin("testuser");
        user.setActivated(true);
        user.setAuthorities(new HashSet<>());
        user.setEmail("test@test.at");
        user.setFollowOrganizations(new ArrayList<>());
        user.setFollowProjects(new ArrayList<>());
        user.setGender(Gender.UNSPECIFIED);
        user.setActive(true);
    }

    @Test
    public void testGetExistingUser() throws Exception {
        doReturn(user).when(userService).getUser(user.getId());
        restUserMockMvc.perform(get("/app/rest/users/1")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json"))
                .andExpect(jsonPath("$.login").value("testuser"));
    }


    @Test
    public void testGetUnknownUser() throws Exception {
        doReturn(null).when(userService).getUser(200L);
        restUserMockMvc.perform(get("/app/rest/users/200")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

}
