package org.respondeco.respondeco.web.rest;

import org.respondeco.respondeco.Application;
import org.respondeco.respondeco.domain.Authority;
import org.respondeco.respondeco.domain.Gender;
import org.respondeco.respondeco.domain.User;
import org.respondeco.respondeco.repository.UserRepository;
import org.respondeco.respondeco.security.AuthoritiesConstants;
import org.respondeco.respondeco.service.UserService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.respondeco.respondeco.testutil.TestUtil;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import javax.inject.Inject;
import java.util.HashSet;
import java.util.Set;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Test class for the AccountController REST controller.
 *
 * @see UserService
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Application.class)
@WebAppConfiguration
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)

public class AccountControllerTest {

    @Inject
    private UserRepository userRepository;

    @Mock
    private UserService userService;

    private MockMvc restUserMockMvc;

    private User defaultAdmin;
    private User defaultUser;
    private Set<Authority> adminAuthorities;
    private Set<Authority> userAuthorities;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        AccountController accountController = new AccountController();
        ReflectionTestUtils.setField(accountController, "userRepository", userRepository);
        ReflectionTestUtils.setField(accountController, "userService", userService);
        this.restUserMockMvc = MockMvcBuilders.standaloneSetup(accountController).build();

        adminAuthorities = new HashSet<>();
        userAuthorities = new HashSet<>();
        Authority authority = new Authority();
        authority.setName(AuthoritiesConstants.USER);
        userAuthorities.add(authority);
        authority = new Authority();
        authority.setName(AuthoritiesConstants.ADMIN);
        adminAuthorities.add(authority);
        this.defaultAdmin = new User();
        this.defaultAdmin.setCreatedDate(null);
        this.defaultAdmin.setLastModifiedDate(null);
        this.defaultAdmin.setLogin("testadmin");
        this.defaultAdmin.setCreatedBy(this.defaultAdmin.getLogin());
        this.defaultAdmin.setTitle("Dr.");
        this.defaultAdmin.setGender(Gender.MALE);
        this.defaultAdmin.setFirstName("john");
        this.defaultAdmin.setLastName("doe");
        this.defaultAdmin.setEmail("john.doe@jhipter.com");
        this.defaultAdmin.setDescription("just a regular everyday normal guy");
        this.defaultAdmin.setAuthorities(adminAuthorities);

    }

    @Test
    public void testCRUDAccountResource() throws Exception {

        when(userService.getUserWithAuthorities()).thenReturn(defaultAdmin);
        // Update Account
        defaultAdmin.setFirstName("jane");
        defaultAdmin.setDescription("just a regular everyday normal girl");

        restUserMockMvc.perform(post("/app/rest/account")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(defaultAdmin)))
                .andExpect(status().isOk());

    }
    @Test
    public void testNonAuthenticatedUser() throws Exception {
        restUserMockMvc.perform(get("/app/rest/authenticate")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string(""));

    }

    @Test
    public void testAuthenticatedUser() throws Exception {
        restUserMockMvc.perform(get("/app/rest/authenticate")
                .with(request -> {
                    request.setRemoteUser("test");
                    return request;
                })
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string("test"));
    }

    @Test
    public void testGetExistingAccount() throws Exception {
        when(userService.getUserWithAuthorities()).thenReturn(defaultAdmin);

        restUserMockMvc.perform(get("/app/rest/account")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.login").value(defaultAdmin.getLogin()))
                .andExpect(jsonPath("$.title").value(defaultAdmin.getTitle()))
                .andExpect(jsonPath("$.gender").value(defaultAdmin.getGender().name()))
                .andExpect(jsonPath("$.firstName").value(defaultAdmin.getFirstName()))
                .andExpect(jsonPath("$.lastName").value(defaultAdmin.getLastName()))
                .andExpect(jsonPath("$.email").value(defaultAdmin.getEmail()))
                .andExpect(jsonPath("$.description").value(defaultAdmin.getDescription()))
                .andExpect(jsonPath("$.roles").value(AuthoritiesConstants.ADMIN));
    }

    @Test
    public void testGetUnknownAccount() throws Exception {
        when(userService.getUserWithAuthorities()).thenReturn(null);

        restUserMockMvc.perform(get("/app/rest/account")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isInternalServerError());
    }

}
