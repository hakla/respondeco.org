package org.respondeco.respondeco.web.rest;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import javax.inject.Inject;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.MockitoAnnotations;
import org.respondeco.respondeco.testutil.TestUtil;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.http.MediaType;
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
import org.respondeco.respondeco.domain.Image;
import org.respondeco.respondeco.repository.ImageRepository;

/**
 * Test class for the ImageController REST controller.
 *
 * @see ImageController
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Application.class)
@WebAppConfiguration
@TestExecutionListeners({ DependencyInjectionTestExecutionListener.class,
    DirtiesContextTestExecutionListener.class,
    TransactionalTestExecutionListener.class })
public class ImageResourceTest {

    private static final Long DEFAULT_ID = new Long(1L);

    private static final byte[] DEFAULT_DATA = new byte[] {0, 0, 0, 0, 0, 0, 0, 0};
    private static final byte[] UPDATED_DATA = new byte[] {1, 1, 1, 1, 1, 1, 1, 1};

    @Inject
    private ImageRepository imageRepository;

    private MockMvc restImageMockMvc;

    private Image image;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        ImageController imageResource = new ImageController();
        ReflectionTestUtils.setField(imageResource, "imageRepository", imageRepository);

        this.restImageMockMvc = MockMvcBuilders.standaloneSetup(imageResource).build();

        image = new Image();
        image.setId(DEFAULT_ID);

        image.setData(DEFAULT_DATA);
    }

    @Test
    public void testCRUDImage() throws Exception {
    /*
        // Create Image
        restImageMockMvc.perform(post("/app/rest/images")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(image)))
                .andExpect(status().isOk());

        // Read Image
        restImageMockMvc.perform(get("/app/rest/images/{id}", DEFAULT_ID))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(DEFAULT_ID.intValue()));

        // Update Image
        image.setData(UPDATED_DATA);

        restImageMockMvc.perform(post("/app/rest/images")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(image)))
                .andExpect(status().isOk());

        // Read updated Image
        restImageMockMvc.perform(get("/app/rest/images/{id}", DEFAULT_ID))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(DEFAULT_ID.intValue()));

        // Delete Image
        restImageMockMvc.perform(delete("/app/rest/images/{id}", DEFAULT_ID)
                .accept(TestUtil.APPLICATION_JSON_UTF8))
                .andExpect(status().isOk());

        // Read nonexisting Image
        restImageMockMvc.perform(get("/app/rest/images/{id}", DEFAULT_ID)
                .accept(TestUtil.APPLICATION_JSON_UTF8))
                .andExpect(status().isNotFound());

    */
    }
}
