package org.respondeco.respondeco.web.rest;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import javax.inject.Inject;

import javafx.scene.effect.Reflection;
import org.joda.time.DateTime;
import org.joda.time.LocalDate;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.respondeco.respondeco.domain.Authority;
import org.respondeco.respondeco.domain.Gender;
import org.respondeco.respondeco.domain.User;
import org.respondeco.respondeco.repository.UserRepository;
import org.respondeco.respondeco.security.AuthoritiesConstants;
import org.respondeco.respondeco.service.TextMessageService;
import org.respondeco.respondeco.service.UserService;
import org.respondeco.respondeco.web.rest.dto.TextMessageDTO;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
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

import java.util.HashSet;
import java.util.Set;

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
    private TextMessageRepository textMessageRepository;

    @Mock
    private UserService userService;

    @Mock
    private UserRepository userRepository;

    private TextMessageService textMessageService;

    private MockMvc restTextMessageMockMvc;

    @Before
    public void setup() throws Exception {
        MockitoAnnotations.initMocks(this);
        textMessageService = new TextMessageService(textMessageRepository, userService, userRepository);
        TextMessageController textMessageController =
                new TextMessageController(textMessageRepository, textMessageService);
        this.restTextMessageMockMvc = MockMvcBuilders.standaloneSetup(textMessageController).build();

        User admin = new User();
        admin.setLogin("admin");
        admin.setTitle("Dr.");
        admin.setFirstName("ad");
        admin.setLastName("min");
        admin.setGender(Gender.MALE);
        admin.setEmail("ad@min.at");
        admin.setActivated(true);
        when(userService.getUserWithAuthorities()).thenReturn(admin);

    }

    @Test
    public void testSenderCanSaveMessage() throws Exception {

        Set<Authority> senderAuthorities = new HashSet<>();
        senderAuthorities.add(new Authority(AuthoritiesConstants.USER));
        User sender = new User();
        sender.setLogin("testSender");
        sender.setAuthorities(senderAuthorities);

        Set<Authority> receiverAuthorities = new HashSet<>();
        receiverAuthorities.add(new Authority(AuthoritiesConstants.USER));
        User receiver = new User();
        receiver.setLogin("testReceiver");
        receiver.setAuthorities(receiverAuthorities);

        when(userService.getUserWithAuthorities()).thenReturn(sender);
        when(userRepository.exists("testReceiver")).thenReturn(true);

        TextMessageDTO textMessageDTO = new TextMessageDTO();
        textMessageDTO.setReceiver("testReceiver");
        textMessageDTO.setContent(DEFAULT_CONTENT);

        System.out.println(userService.getUserWithAuthorities());

        // Create TextMessage
        restTextMessageMockMvc.perform(post("/app/rest/textmessages")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(textMessageDTO)))
                .andExpect(status().isOk());

    }

    @Test
    public void testReceiverCanReadMessage() throws Exception {

        Set<Authority> senderAuthorities = new HashSet<>();
        senderAuthorities.add(new Authority(AuthoritiesConstants.USER));
        User sender = new User();
        sender.setLogin("testSender");
        sender.setAuthorities(senderAuthorities);

        Set<Authority> receiverAuthorities = new HashSet<>();
        receiverAuthorities.add(new Authority(AuthoritiesConstants.USER));
        User receiver = new User();
        receiver.setLogin("testReceiver");
        receiver.setAuthorities(receiverAuthorities);

        when(userService.getUserWithAuthorities()).thenReturn(sender);
        when(userRepository.exists("testReceiver")).thenReturn(true);

        TextMessageDTO textMessageDTO = new TextMessageDTO();
        textMessageDTO.setReceiver("testReceiver");
        textMessageDTO.setContent(DEFAULT_CONTENT);

        // Create TextMessage
        restTextMessageMockMvc.perform(post("/app/rest/textmessages")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(textMessageDTO)))
                .andExpect(status().isOk());

        when(userService.getUserWithAuthorities()).thenReturn(receiver);

        restTextMessageMockMvc.perform(get("/app/rest/textmessages"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].sender").value("testSender"))
                .andExpect(jsonPath("$[0].receiver").value("testReceiver"))
                .andExpect(jsonPath("$[0].content").value(DEFAULT_CONTENT));

    }

    @Test
    public void testReceiverCanNotBeEqualToSender() throws Exception {
        Set<Authority> senderAuthorities = new HashSet<>();
        senderAuthorities.add(new Authority(AuthoritiesConstants.USER));
        User sender = new User();
        sender.setLogin("testSender");
        sender.setAuthorities(senderAuthorities);

        when(userService.getUserWithAuthorities()).thenReturn(sender);

        TextMessageDTO textMessageDTO = new TextMessageDTO();
        textMessageDTO.setReceiver("testSender");
        textMessageDTO.setContent(DEFAULT_CONTENT);

        // Create TextMessage
        restTextMessageMockMvc.perform(post("/app/rest/textmessages")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(textMessageDTO)))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void testReceiverMustExist() throws Exception {
        Set<Authority> senderAuthorities = new HashSet<>();
        senderAuthorities.add(new Authority(AuthoritiesConstants.USER));
        User sender = new User();
        sender.setLogin("testSender");
        sender.setAuthorities(senderAuthorities);

        when(userService.getUserWithAuthorities()).thenReturn(sender);
        when(userRepository.exists("nonexistingReceiver")).thenReturn(false);

        TextMessageDTO textMessageDTO = new TextMessageDTO();
        textMessageDTO.setReceiver("nonexistingReceiver");
        textMessageDTO.setContent(DEFAULT_CONTENT);

        // Create TextMessage
        restTextMessageMockMvc.perform(post("/app/rest/textmessages")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(textMessageDTO)))
                .andExpect(status().isBadRequest());
    }


/**
    @Test
    public void testCRUDTextMessage() throws Exception {

        // Create TextMessage
        restTextMessageMockMvc.perform(post("/app/rest/textmessages")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(textmessage)))
                .andExpect(status().isOk());

        // Read TextMessage
        restTextMessageMockMvc.perform(get("/app/rest/textmessages/{receiver}", DEFAULT_RECEIVER))
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
    */
}
