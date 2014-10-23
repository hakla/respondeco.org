package org.respondeco.respondeco.web.rest;

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
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import org.springframework.test.context.support.DirtiesContextTestExecutionListener;
import org.springframework.test.context.transaction.TransactionalTestExecutionListener;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import org.respondeco.respondeco.Application;
import org.respondeco.respondeco.domain.ProfilePicture;
import org.respondeco.respondeco.repository.ProfilePictureRepository;

/**
 * Test class for the ProfilePictureResource REST controller.
 *
 * @see ProfilePictureResource
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Application.class)
@WebAppConfiguration
@TestExecutionListeners({ DependencyInjectionTestExecutionListener.class,
    DirtiesContextTestExecutionListener.class,
    TransactionalTestExecutionListener.class })
public class ProfilePictureResourceTest {

    private static final String DEFAULT_USERLOGIN = "testuser";

    private static final String DEFAULT_LABEL = "SAMPLE_TEXT";
    private static final String UPDATED_LABEL = "UPDATED_TEXT";
        
    private static final byte[] DEFAULT_DATA = new byte[] { 1,1,1,1,1,1,1,1,1,1,1,1,1,1 };
    private static final byte[] UPDATED_DATA = new byte[] { 2,2,2,2,2,2,2,2,2,2,2,2,2,2 };
        
    @Inject
    private ProfilePictureRepository profilepictureRepository;

    private MockMvc restProfilePictureMockMvc;

    private ProfilePicture profilepicture;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        ProfilePictureResource profilepictureResource = new ProfilePictureResource();
        ReflectionTestUtils.setField(profilepictureResource, "profilepictureRepository", profilepictureRepository);

        this.restProfilePictureMockMvc = MockMvcBuilders.standaloneSetup(profilepictureResource).build();

        profilepicture = new ProfilePicture();

        profilepicture.setUserlogin(DEFAULT_USERLOGIN);
        profilepicture.setLabel(DEFAULT_LABEL);
        profilepicture.setData(DEFAULT_DATA);
        profilepicture.setActive(true);
    }

    @Test
    public void testCRUDProfilePicture() throws Exception {

        // Create ProfilePicture
        restProfilePictureMockMvc.perform(post("/app/rest/profilepictures")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(profilepicture)))
                .andExpect(status().isOk());

        // Read ProfilePicture
        restProfilePictureMockMvc.perform(get("/app/rest/profilepictures/{userlogin}", DEFAULT_USERLOGIN))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.userlogin").value(DEFAULT_USERLOGIN.toString()))
                .andExpect(jsonPath("$.label").value(DEFAULT_LABEL.toString()))
                .andExpect(jsonPath("$.data").value(Base64.encodeBase64String(DEFAULT_DATA)));

        // Update ProfilePicture
        profilepicture.setLabel(UPDATED_LABEL);
        profilepicture.setData(UPDATED_DATA);

        restProfilePictureMockMvc.perform(post("/app/rest/profilepictures")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(profilepicture)))
                .andExpect(status().isOk());

        // Read updated ProfilePicture
        restProfilePictureMockMvc.perform(get("/app/rest/profilepictures/{userlogin}", DEFAULT_USERLOGIN))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.userlogin").value(DEFAULT_USERLOGIN.toString()))
                .andExpect(jsonPath("$.label").value(UPDATED_LABEL.toString()))
                .andExpect(jsonPath("$.data").value(Base64.encodeBase64String(UPDATED_DATA)));

        // Delete ProfilePicture
        restProfilePictureMockMvc.perform(delete("/app/rest/profilepictures/{userlogin}", DEFAULT_USERLOGIN)
                .accept(TestUtil.APPLICATION_JSON_UTF8))
                .andExpect(status().isOk());

        // Read nonexisting ProfilePicture
        restProfilePictureMockMvc.perform(get("/app/rest/profilepictures/{userlogin}", DEFAULT_USERLOGIN)
                .accept(TestUtil.APPLICATION_JSON_UTF8))
                .andExpect(status().isNotFound());

    }
}
