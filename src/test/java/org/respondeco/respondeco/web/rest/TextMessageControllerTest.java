package org.respondeco.respondeco.web.rest;

import static junit.framework.TestCase.assertFalse;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import javax.inject.Inject;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.respondeco.respondeco.domain.Authority;
import org.respondeco.respondeco.domain.Gender;
import org.respondeco.respondeco.domain.User;
import org.respondeco.respondeco.repository.UserRepository;
import org.respondeco.respondeco.security.AuthoritiesConstants;
import org.respondeco.respondeco.service.TextMessageService;
import org.respondeco.respondeco.service.UserService;
import org.respondeco.respondeco.web.rest.dto.TextMessageRequestDTO;
import org.respondeco.respondeco.testutil.TestUtil;
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
                new TextMessageController(textMessageService);
        this.restTextMessageMockMvc = MockMvcBuilders.standaloneSetup(textMessageController).build();

        textMessageServiceAllMocked = new TextMessageService(textMessageRepositoryMock, userServiceMock,
                userRepositoryMock);
        TextMessageController textMessageControllerAllMocked =
                new TextMessageController(textMessageServiceAllMocked);
        this.restTextMessageMockMvcAllMocked = MockMvcBuilders.standaloneSetup(textMessageControllerAllMocked).build();

    }

    @Test
    public void testPOST_expectOK_senderCanSaveMessage() throws Exception {

        Set<Authority> senderAuthorities = new HashSet<>();
        senderAuthorities.add(new Authority(AuthoritiesConstants.USER));
        User sender = new User();
        sender.setId(100L);
        sender.setLogin("testSender");
        sender.setAuthorities(senderAuthorities);
        User receiver = new User();
        receiver.setId(200L);
        receiver.setLogin("testReceiver");

        TextMessageRequestDTO textMessageRequestDTO = new TextMessageRequestDTO();
        textMessageRequestDTO.setReceiver("testReceiver");
        textMessageRequestDTO.setContent(DEFAULT_CONTENT);

        when(userServiceMock.getUserWithAuthorities()).thenReturn(sender);
        when(userRepositoryMock.findByLogin(receiver.getLogin())).thenReturn(receiver);

        // Create TextMessage
        restTextMessageMockMvc.perform(post("/app/rest/textmessages")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(textMessageRequestDTO)))
                .andExpect(status().isCreated());

    }

    @Test
    public void testPOST_expectBAD_REQUEST_receiverCanNotBeEqualToSender() throws Exception {
        Set<Authority> senderAuthorities = new HashSet<>();
        senderAuthorities.add(new Authority(AuthoritiesConstants.USER));
        User sender = new User();
        sender.setLogin("testSender");
        sender.setAuthorities(senderAuthorities);

        TextMessageRequestDTO textMessageRequestDTO = new TextMessageRequestDTO();
        textMessageRequestDTO.setReceiver("testSender");
        textMessageRequestDTO.setContent(DEFAULT_CONTENT);

        when(userServiceMock.getUserWithAuthorities()).thenReturn(sender);

        // Create TextMessage
        restTextMessageMockMvc.perform(post("/app/rest/textmessages")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(textMessageRequestDTO)))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void testPOST_expectBAD_REQUEST_receiverHasToExist() throws Exception {
        Set<Authority> senderAuthorities = new HashSet<>();
        senderAuthorities.add(new Authority(AuthoritiesConstants.USER));
        User sender = new User();
        sender.setLogin("testSender");
        sender.setAuthorities(senderAuthorities);

        TextMessageRequestDTO textMessageRequestDTO = new TextMessageRequestDTO();
        textMessageRequestDTO.setReceiver("nonexistingReceiver");
        textMessageRequestDTO.setContent(DEFAULT_CONTENT);

        when(userServiceMock.getUserWithAuthorities()).thenReturn(sender);
        when(userRepositoryMock.findByLogin("nonexistingReceiver")).thenReturn(null);

        // Create TextMessage
        restTextMessageMockMvc.perform(post("/app/rest/textmessages")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(textMessageRequestDTO)))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void testPOST_expectBAD_REQUEST_contentMustNotBeEmpty() throws Exception {
        Set<Authority> senderAuthorities = new HashSet<>();
        senderAuthorities.add(new Authority(AuthoritiesConstants.USER));
        User sender = new User();
        sender.setLogin("testSender");
        sender.setAuthorities(senderAuthorities);
        User receiver = new User();
        receiver.setLogin("testReceiver");

        TextMessageRequestDTO textMessageRequestDTO = new TextMessageRequestDTO();
        textMessageRequestDTO.setReceiver(receiver.getLogin());
        textMessageRequestDTO.setContent("");

        when(userServiceMock.getUserWithAuthorities()).thenReturn(sender);
        when(userRepositoryMock.findByLogin(receiver.getLogin())).thenReturn(receiver);

        // Create TextMessage
        restTextMessageMockMvc.perform(post("/app/rest/textmessages")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(textMessageRequestDTO)))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void testGET_expectOK_receiverCanReadMessage() throws Exception {

        Set<Authority> senderAuthorities = new HashSet<>();
        senderAuthorities.add(new Authority(AuthoritiesConstants.USER));
        User sender = new User();
        sender.setId(100L);
        sender.setLogin("testSender");
        sender.setAuthorities(senderAuthorities);

        Set<Authority> receiverAuthorities = new HashSet<>();
        receiverAuthorities.add(new Authority(AuthoritiesConstants.USER));
        User receiver = new User();
        receiver.setId(200L);
        receiver.setLogin("testReceiver");
        receiver.setAuthorities(receiverAuthorities);

        TextMessageRequestDTO textMessageRequestDTO = new TextMessageRequestDTO();
        textMessageRequestDTO.setReceiver("testReceiver");
        textMessageRequestDTO.setContent(DEFAULT_CONTENT);

        when(userServiceMock.getUserWithAuthorities()).thenReturn(sender);
        when(userRepositoryMock.findByLogin(receiver.getLogin())).thenReturn(receiver);

        // Create TextMessage
        restTextMessageMockMvc.perform(post("/app/rest/textmessages")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(textMessageRequestDTO)))
                .andExpect(status().isCreated());

        when(userServiceMock.getUserWithAuthorities()).thenReturn(receiver);

        restTextMessageMockMvc.perform(get("/app/rest/textmessages"))
                .andExpect(status().isOk())
                .andDo(print())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].sender").value("testSender"))
                .andExpect(jsonPath("$[0].content").value(DEFAULT_CONTENT))
                .andExpect(jsonPath("$[0].timestamp").exists());

    }

    @Test
    public void testDELETE_expectOK_receiverCanDeleteMessage() throws Exception {
        Set<Authority> receiverAuthorities = new HashSet<>();
        receiverAuthorities.add(new Authority(AuthoritiesConstants.USER));
        User receiver = new User();
        receiver.setLogin("testReceiver");
        receiver.setAuthorities(receiverAuthorities);
        User sender = new User();
        sender.setId(100L);

        TextMessage textMessage = new TextMessage();
        textMessage.setId(100L);
        textMessage.setSender(sender);
        textMessage.setReceiver(receiver);
        textMessage.setContent(DEFAULT_CONTENT);
        textMessage.setActive(true);

        when(userServiceMock.getUserWithAuthorities()).thenReturn(receiver);
        when(textMessageRepositoryMock.findOne(100L)).thenReturn(textMessage);

        restTextMessageMockMvcAllMocked.perform(delete("/app/rest/textmessages/{id}", 100L))
                .andExpect(status().isOk());

        assertFalse(textMessage.isActive());
        verify(textMessageRepositoryMock, times(1)).save(textMessage);
    }

    @Test
    public void testDELETE_expectBAD_REQUEST_cannotDeleteForeignMessages() throws Exception {
        Set<Authority> unauthorizedUserAuthorities = new HashSet<>();
        unauthorizedUserAuthorities.add(new Authority(AuthoritiesConstants.USER));
        User unauthorizedUser = new User();
        unauthorizedUser.setLogin("unauthorizedUser");
        unauthorizedUser.setAuthorities(unauthorizedUserAuthorities);
        User sender = new User();
        sender.setId(100L);
        User receiver = new User();
        receiver.setId(200L);

        TextMessage textMessage = new TextMessage();
        textMessage.setId(100L);
        textMessage.setSender(sender);
        textMessage.setReceiver(receiver);
        textMessage.setContent(DEFAULT_CONTENT);
        textMessage.setActive(true);

        when(userServiceMock.getUserWithAuthorities()).thenReturn(unauthorizedUser);
        when(textMessageRepositoryMock.findOne(100L)).thenReturn(textMessage);

        restTextMessageMockMvcAllMocked.perform(delete("/app/rest/textmessages/{id}", 100L))
                .andExpect(status().isBadRequest());

        assertTrue(textMessage.isActive());
        verify(textMessageRepositoryMock, times(1)).findOne(100L);
        verify(textMessageRepositoryMock, times(0)).save(textMessage);
    }

}
