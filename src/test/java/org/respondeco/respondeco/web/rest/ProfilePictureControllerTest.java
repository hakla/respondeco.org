package org.respondeco.respondeco.web.rest;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import javax.inject.Inject;

import org.apache.tomcat.util.codec.binary.Base64;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.respondeco.respondeco.domain.Authority;
import org.respondeco.respondeco.domain.User;
import org.respondeco.respondeco.repository.UserRepository;
import org.respondeco.respondeco.security.AuthoritiesConstants;
import org.respondeco.respondeco.service.ProfilePictureService;
import org.respondeco.respondeco.service.UserService;
import org.respondeco.respondeco.testutil.TestUtil;
import org.respondeco.respondeco.web.rest.dto.ProfilePictureDTO;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import org.springframework.test.context.support.DirtiesContextTestExecutionListener;
import org.springframework.test.context.transaction.TransactionalTestExecutionListener;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import org.respondeco.respondeco.Application;
import org.respondeco.respondeco.repository.ProfilePictureRepository;

import java.util.HashSet;
import java.util.Set;

/**
 * Test class for the ProfilePictureController REST controller.
 *
 * @see ProfilePictureController
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Application.class)
@WebAppConfiguration
@TestExecutionListeners({ DependencyInjectionTestExecutionListener.class,
    DirtiesContextTestExecutionListener.class,
    TransactionalTestExecutionListener.class })
public class ProfilePictureControllerTest {

    private static final String DEFAULT_LABEL = "SAMPLE_TEXT";
    private static final String UPDATED_LABEL = "UPDATED_TEXT";
        
    private static final byte[] DEFAULT_DATA = new byte[] { 1,1,1,1,1,1,1,1,1,1,1,1,1,1 };
    private static final byte[] UPDATED_DATA = new byte[] { 2,2,2,2,2,2,2,2,2,2,2,2,2,2 };

    @Inject
    private ProfilePictureRepository profilePictureRepository;

    @Inject
    private UserRepository userRepository;

    @Mock
    private UserService userService;

    private MockMvc restProfilePictureMockMvc;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        ProfilePictureService profilePictureService = new ProfilePictureService(profilePictureRepository, userService, userRepository);
        ProfilePictureController profilepictureController =
                new ProfilePictureController(profilePictureRepository, profilePictureService, userRepository);

        this.restProfilePictureMockMvc = MockMvcBuilders.standaloneSetup(profilepictureController).build();
    }

    @Test
    public void testCRUDProfilePicture() throws Exception {
        Set<Authority> adminAuthorities = new HashSet<>();
        adminAuthorities.add(new Authority(AuthoritiesConstants.USER));
        adminAuthorities.add(new Authority(AuthoritiesConstants.ADMIN));
        User admin = new User();
        admin.setLogin("admin");
        admin.setAuthorities(adminAuthorities);

        when(userService.getUserWithAuthorities()).thenReturn(admin);

        ProfilePictureDTO profilePictureDTO = new ProfilePictureDTO();
        profilePictureDTO.setLabel(DEFAULT_LABEL);
        profilePictureDTO.setData(DEFAULT_DATA);

        // Create ProfilePicture
        restProfilePictureMockMvc.perform(post("/app/rest/profilepictures")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(profilePictureDTO)))
                .andExpect(status().isOk());

        // Read ProfilePicture
        restProfilePictureMockMvc.perform(get("/app/rest/profilepictures/{userlogin}", admin.getLogin()))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.userlogin").value(admin.getLogin()))
                .andExpect(jsonPath("$.label").value(DEFAULT_LABEL.toString()))
                .andExpect(jsonPath("$.data").value(Base64.encodeBase64String(DEFAULT_DATA)));

        // Update ProfilePicture
        profilePictureDTO = new ProfilePictureDTO();
        profilePictureDTO.setLabel(UPDATED_LABEL);
        profilePictureDTO.setData(UPDATED_DATA);

        restProfilePictureMockMvc.perform(post("/app/rest/profilepictures")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(profilePictureDTO)))
                .andExpect(status().isOk());

        // Read updated ProfilePicture
        restProfilePictureMockMvc.perform(get("/app/rest/profilepictures/{userlogin}", admin.getLogin()))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.userlogin").value(admin.getLogin()))
                .andExpect(jsonPath("$.label").value(UPDATED_LABEL.toString()))
                .andExpect(jsonPath("$.data").value(Base64.encodeBase64String(UPDATED_DATA)));

        // Delete ProfilePicture
        restProfilePictureMockMvc.perform(delete("/app/rest/profilepictures")
                .accept(TestUtil.APPLICATION_JSON_UTF8))
                .andExpect(status().isOk());

        // Read nonexisting ProfilePicture
        restProfilePictureMockMvc.perform(get("/app/rest/profilepictures/nonexistent")
                .accept(TestUtil.APPLICATION_JSON_UTF8))
                .andExpect(status().isNotFound());

    }

}
