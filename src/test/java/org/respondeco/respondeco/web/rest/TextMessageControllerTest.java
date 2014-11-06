package org.respondeco.respondeco.web.rest;

import static junit.framework.TestCase.assertFalse;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

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
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import org.respondeco.respondeco.Application;
import org.respondeco.respondeco.domain.TextMessage;
import org.respondeco.respondeco.repository.TextMessageRepository;

import java.util.Arrays;
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
        
    private static final String DEFAULT_CONTENT = "SAMPLE_TEXT";

    @Inject
    private TextMessageRepository textMessageRepository;

    @Mock
    private TextMessageRepository textMessageRepositoryMock;

    @Mock
    private UserService userServiceMock;

    @Mock
    private UserRepository userRepositoryMock;

    private TextMessageService textMessageService;
    private TextMessageService textMessageServiceAllMocked;

    private MockMvc restTextMessageMockMvc;
    private MockMvc restTextMessageMockMvcAllMocked;

    @Before
    public void setup() throws Exception {
        MockitoAnnotations.initMocks(this);
        textMessageService = new TextMessageService(textMessageRepository, userServiceMock, userRepositoryMock);
        TextMessageController textMessageController =
                new TextMessageController(textMessageRepository, textMessageService);
        this.restTextMessageMockMvc = MockMvcBuilders.standaloneSetup(textMessageController).build();

        textMessageServiceAllMocked = new TextMessageService(textMessageRepositoryMock, userServiceMock,
                userRepositoryMock);
        TextMessageController textMessageControllerAllMocked =
                new TextMessageController(textMessageRepositoryMock, textMessageServiceAllMocked);
        this.restTextMessageMockMvcAllMocked = MockMvcBuilders.standaloneSetup(textMessageControllerAllMocked).build();

        User admin = new User();
        admin.setLogin("admin");
        admin.setTitle("Dr.");
        admin.setFirstName("ad");
        admin.setLastName("min");
        admin.setGender(Gender.MALE);
        admin.setEmail("ad@min.at");
        admin.setActivated(true);
        when(userServiceMock.getUserWithAuthorities()).thenReturn(admin);

    }

    @Test
    public void testPOST_SenderCanSaveMessage() throws Exception {

        Set<Authority> senderAuthorities = new HashSet<>();
        senderAuthorities.add(new Authority(AuthoritiesConstants.USER));
        User sender = new User();
        sender.setLogin("testSender");
        sender.setAuthorities(senderAuthorities);

        TextMessageDTO textMessageDTO = new TextMessageDTO();
        textMessageDTO.setReceiver("testReceiver");
        textMessageDTO.setContent(DEFAULT_CONTENT);

        when(userServiceMock.getUserWithAuthorities()).thenReturn(sender);
        when(userRepositoryMock.exists("testReceiver")).thenReturn(true);

        // Create TextMessage
        restTextMessageMockMvc.perform(post("/app/rest/textmessages")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(textMessageDTO)))
                .andExpect(status().isCreated());

    }

    @Test
    public void testPOST_ReceiverCanNotBeEqualToSender() throws Exception {
        Set<Authority> senderAuthorities = new HashSet<>();
        senderAuthorities.add(new Authority(AuthoritiesConstants.USER));
        User sender = new User();
        sender.setLogin("testSender");
        sender.setAuthorities(senderAuthorities);

        TextMessageDTO textMessageDTO = new TextMessageDTO();
        textMessageDTO.setReceiver("testSender");
        textMessageDTO.setContent(DEFAULT_CONTENT);

        when(userServiceMock.getUserWithAuthorities()).thenReturn(sender);

        // Create TextMessage
        restTextMessageMockMvc.perform(post("/app/rest/textmessages")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(textMessageDTO)))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void testPOST_ReceiverMustExist() throws Exception {
        Set<Authority> senderAuthorities = new HashSet<>();
        senderAuthorities.add(new Authority(AuthoritiesConstants.USER));
        User sender = new User();
        sender.setLogin("testSender");
        sender.setAuthorities(senderAuthorities);

        TextMessageDTO textMessageDTO = new TextMessageDTO();
        textMessageDTO.setReceiver("nonexistingReceiver");
        textMessageDTO.setContent(DEFAULT_CONTENT);

        when(userServiceMock.getUserWithAuthorities()).thenReturn(sender);
        when(userRepositoryMock.exists("nonexistingReceiver")).thenReturn(false);

        // Create TextMessage
        restTextMessageMockMvc.perform(post("/app/rest/textmessages")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(textMessageDTO)))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void testPOST_ContentMustNotBeEmpty() throws Exception {
        Set<Authority> senderAuthorities = new HashSet<>();
        senderAuthorities.add(new Authority(AuthoritiesConstants.USER));
        User sender = new User();
        sender.setLogin("testSender");
        sender.setAuthorities(senderAuthorities);

        TextMessageDTO textMessageDTO = new TextMessageDTO();
        textMessageDTO.setReceiver("testReceiver");
        textMessageDTO.setContent("");

        when(userServiceMock.getUserWithAuthorities()).thenReturn(sender);
        when(userRepositoryMock.exists("testReceiver")).thenReturn(true);

        // Create TextMessage
        restTextMessageMockMvc.perform(post("/app/rest/textmessages")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(textMessageDTO)))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void testGET_ReceiverCanReadMessage() throws Exception {

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

        TextMessageDTO textMessageDTO = new TextMessageDTO();
        textMessageDTO.setReceiver("testReceiver");
        textMessageDTO.setContent(DEFAULT_CONTENT);

        when(userServiceMock.getUserWithAuthorities()).thenReturn(sender);
        when(userRepositoryMock.exists("testReceiver")).thenReturn(true);

        // Create TextMessage
        restTextMessageMockMvc.perform(post("/app/rest/textmessages")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(textMessageDTO)))
                .andExpect(status().isCreated());

        when(userServiceMock.getUserWithAuthorities()).thenReturn(receiver);

        restTextMessageMockMvc.perform(get("/app/rest/textmessages"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].sender").value("testSender"))
                .andExpect(jsonPath("$[0].receiver").value("testReceiver"))
                .andExpect(jsonPath("$[0].content").value(DEFAULT_CONTENT));

    }

    @Test
    public void testDELETE_ReceiverCanDeleteMessage() throws Exception {
        Set<Authority> receiverAuthorities = new HashSet<>();
        receiverAuthorities.add(new Authority(AuthoritiesConstants.USER));
        User receiver = new User();
        receiver.setLogin("testReceiver");
        receiver.setAuthorities(receiverAuthorities);

        TextMessage textMessage = new TextMessage();
        textMessage.setId(1L);
        textMessage.setSender("testSender");
        textMessage.setReceiver("testReceiver");
        textMessage.setContent(DEFAULT_CONTENT);
        textMessage.setActive(true);

        when(userServiceMock.getUserWithAuthorities()).thenReturn(receiver);
        when(textMessageRepositoryMock.findOne(1L)).thenReturn(textMessage);

        restTextMessageMockMvcAllMocked.perform(delete("/app/rest/textmessages/{id}", 1L))
                .andExpect(status().isOk());

        assertFalse(textMessage.isActive());
        verify(textMessageRepositoryMock, times(1)).save(textMessage);
    }

    @Test
    public void testDELETE_CannotDeleteForeignMessages() throws Exception {
        Set<Authority> unauthorizedUserAuthorities = new HashSet<>();
        unauthorizedUserAuthorities.add(new Authority(AuthoritiesConstants.USER));
        User unauthorizedUser = new User();
        unauthorizedUser.setLogin("unauthorizedUser");
        unauthorizedUser.setAuthorities(unauthorizedUserAuthorities);

        TextMessage textMessage = new TextMessage();
        textMessage.setId(1L);
        textMessage.setSender("testSender");
        textMessage.setReceiver("testReceiver");
        textMessage.setContent(DEFAULT_CONTENT);
        textMessage.setActive(true);

        when(userServiceMock.getUserWithAuthorities()).thenReturn(unauthorizedUser);
        when(textMessageRepositoryMock.findOne(1L)).thenReturn(textMessage);

        restTextMessageMockMvcAllMocked.perform(delete("/app/rest/textmessages/{id}", 1L))
                .andExpect(status().isBadRequest());

        assertTrue(textMessage.isActive());
        verify(textMessageRepositoryMock, times(1)).findOne(1L);
        verify(textMessageRepositoryMock, times(0)).save(textMessage);
    }

}
