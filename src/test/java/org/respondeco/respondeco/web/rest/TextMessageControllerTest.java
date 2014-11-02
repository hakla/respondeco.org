package org.respondeco.respondeco.web.rest;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import javax.inject.Inject;

import org.joda.time.DateTime;
import org.joda.time.LocalDate;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.MockitoAnnotations;
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
import org.respondeco.respondeco.domain.TextMessage;
import org.respondeco.respondeco.repository.TextMessageRepository;

/**
 * Test class for the TextMessageController REST controller.
 *
 * @see TextMessageController
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Application.class)
@WebAppConfiguration
@TestExecutionListeners({ DependencyInjectionTestExecutionListener.class,
    DirtiesContextTestExecutionListener.class,
    TransactionalTestExecutionListener.class })
public class TextMessageControllerTest {
    
    private static final Long DEFAULT_ID = new Long(1L);
    
    private static final String DEFAULT_SENDER = "SAMPLE_TEXT";
    private static final String UPDATED_SENDER = "UPDATED_TEXT";
        
    private static final String DEFAULT_RECEIVER = "SAMPLE_TEXT";
    private static final String UPDATED_RECEIVER = "UPDATED_TEXT";
        
    private static final DateTime DEFAULT_TIMESTAMP = DateTime.now();
    private static final DateTime UPDATED_TIMESTAMP = DateTime.now().minus(500);
        
    private static final String DEFAULT_CONTENT = "SAMPLE_TEXT";
    private static final String UPDATED_CONTENT = "UPDATED_TEXT";
        
    @Inject
    private TextMessageRepository textmessageRepository;

    private MockMvc restTextMessageMockMvc;

    private TextMessage textmessage;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        TextMessageController textmessageController = new TextMessageController();
        ReflectionTestUtils.setField(textmessageController, "textmessageRepository", textmessageRepository);

        this.restTextMessageMockMvc = MockMvcBuilders.standaloneSetup(textmessageController).build();

        textmessage = new TextMessage();
        textmessage.setId(DEFAULT_ID);

        textmessage.setSender(DEFAULT_SENDER);
        textmessage.setReceiver(DEFAULT_RECEIVER);
        textmessage.setTimestamp(DEFAULT_TIMESTAMP);
        textmessage.setContent(DEFAULT_CONTENT);
    }

    @Test
    public void testCRUDTextMessage() throws Exception {

        // Create TextMessage
        restTextMessageMockMvc.perform(post("/app/rest/textmessages")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(textmessage)))
                .andExpect(status().isOk());

        // Read TextMessage
        restTextMessageMockMvc.perform(get("/app/rest/textmessages/{id}", DEFAULT_ID))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(DEFAULT_ID.intValue()))
                .andExpect(jsonPath("$.sender").value(DEFAULT_SENDER.toString()))
                .andExpect(jsonPath("$.receiver").value(DEFAULT_RECEIVER.toString()))
                .andExpect(jsonPath("$.timestamp").value(DEFAULT_TIMESTAMP.toString()))
                .andExpect(jsonPath("$.content").value(DEFAULT_CONTENT.toString()));

        // Update TextMessage
        textmessage.setSender(UPDATED_SENDER);
        textmessage.setReceiver(UPDATED_RECEIVER);
        textmessage.setTimestamp(UPDATED_TIMESTAMP);
        textmessage.setContent(UPDATED_CONTENT);

        restTextMessageMockMvc.perform(post("/app/rest/textmessages")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(textmessage)))
                .andExpect(status().isOk());

        // Read updated TextMessage
        restTextMessageMockMvc.perform(get("/app/rest/textmessages/{id}", DEFAULT_ID))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(DEFAULT_ID.intValue()))
                .andExpect(jsonPath("$.sender").value(UPDATED_SENDER.toString()))
                .andExpect(jsonPath("$.receiver").value(UPDATED_RECEIVER.toString()))
                .andExpect(jsonPath("$.timestamp").value(UPDATED_TIMESTAMP.toString()))
                .andExpect(jsonPath("$.content").value(UPDATED_CONTENT.toString()));

        // Delete TextMessage
        restTextMessageMockMvc.perform(delete("/app/rest/textmessages/{id}", DEFAULT_ID)
                .accept(TestUtil.APPLICATION_JSON_UTF8))
                .andExpect(status().isOk());

        // Read nonexisting TextMessage
        restTextMessageMockMvc.perform(get("/app/rest/textmessages/{id}", DEFAULT_ID)
                .accept(TestUtil.APPLICATION_JSON_UTF8))
                .andExpect(status().isNotFound());

    }
}
