package org.respondeco.respondeco.web.rest;

import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.respondeco.respondeco.Application;
import org.respondeco.respondeco.domain.TextMessage;
import org.respondeco.respondeco.domain.User;
import org.respondeco.respondeco.repository.TextMessageRepository;
import org.respondeco.respondeco.repository.UserRepository;
import org.respondeco.respondeco.service.TextMessageService;
import org.respondeco.respondeco.service.UserService;
import org.respondeco.respondeco.service.exception.IllegalValueException;
import org.respondeco.respondeco.service.exception.NoSuchEntityException;
import org.respondeco.respondeco.testutil.TestUtil;
import org.respondeco.respondeco.web.rest.dto.TextMessageRequestDTO;
import org.respondeco.respondeco.web.rest.dto.UserDTO;
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

import java.util.Arrays;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

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

    @Mock
    private TextMessageRepository Mock;

    @Mock
    private TextMessageRepository textMessageRepositoryMock;

    @Mock
    private UserService userServiceMock;

    @Mock
    private UserRepository userRepositoryMock;

    @Mock
    private TextMessageService textMessageServiceMock;

    private MockMvc restTextMessageMockMvc;

    @Before
    public void setup() throws Exception {
        MockitoAnnotations.initMocks(this);

        TextMessageController textMessageController =
                new TextMessageController(textMessageServiceMock, userServiceMock);
        this.restTextMessageMockMvc = MockMvcBuilders.standaloneSetup(textMessageController).build();

    }

    /**
     * test if OK is returned if message was created successfully
     */
    @Test
    public void testPOST_expectCREATED_senderCanSaveMessage() throws Exception {
        User receiver = new User();
        receiver.setId(200L);
        receiver.setLogin("testReceiver");

        TextMessageRequestDTO textMessageRequestDTO = new TextMessageRequestDTO();
        textMessageRequestDTO.setReceiver(new UserDTO(receiver));
        textMessageRequestDTO.setContent(DEFAULT_CONTENT);

        doReturn(new TextMessage()).when(textMessageServiceMock).createTextMessage(anyLong(), anyString());

        // Create TextMessage
        restTextMessageMockMvc.perform(post("/app/rest/textmessages")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(textMessageRequestDTO)))
                .andExpect(status().isCreated());

    }

    /**
     * test if BAD REQUEST is returned if the service throws an IllegalArgumentException
     */
    @Test
    public void testPOST_expectBAD_REQUEST_serviceThrowsIllegalArgumentException() throws Exception {
        User sender = new User();
        sender.setId(100L);
        sender.setLogin("testSender");

        TextMessageRequestDTO textMessageRequestDTO = new TextMessageRequestDTO();
        textMessageRequestDTO.setReceiver(new UserDTO(sender));
        textMessageRequestDTO.setContent(DEFAULT_CONTENT);

        IllegalValueException illegalValueException = new IllegalValueException(
            "test.illegalvalue", "Test IllegalValueException");
        doThrow(illegalValueException).when(textMessageServiceMock).createTextMessage(anyLong(), anyString());

        // Create TextMessage
        restTextMessageMockMvc.perform(post("/app/rest/textmessages")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(textMessageRequestDTO)))
                .andExpect(status().isBadRequest());
    }

    /**
     * test if NOT FOUND is throws if the service throws an NoSuchUserException
     */
    @Test
    public void testPOST_expectNOT_FOUND_serviceThrowsNoSuchUserException() throws Exception {
        User sender = new User();
        sender.setId(100L);
        sender.setLogin("testSender");

        TextMessageRequestDTO textMessageRequestDTO = new TextMessageRequestDTO();
        textMessageRequestDTO.setReceiver(new UserDTO(sender));
        textMessageRequestDTO.setContent(DEFAULT_CONTENT);

        doThrow(NoSuchEntityException.class).when(textMessageServiceMock).createTextMessage(anyLong(), anyString());

        // Create TextMessage
        restTextMessageMockMvc.perform(post("/app/rest/textmessages")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(textMessageRequestDTO)))
                .andExpect(status().isNotFound());
    }

    /**
     * test if messages are returned correctly
     */
    @Test
    public void testGET_expectOK_receiverCanReadMessages() throws Exception {

        User sender = new User();
        sender.setId(100L);
        sender.setLogin("testSender");
        TextMessage textMessage1 = new TextMessage();
        textMessage1.setSender(sender);
        textMessage1.setContent(DEFAULT_CONTENT);
        textMessage1.setTimestamp(DateTime.now());
        TextMessage textMessage2 = new TextMessage();
        textMessage2.setSender(sender);
        textMessage2.setContent(DEFAULT_CONTENT);
        textMessage2.setTimestamp(DateTime.now());

        doReturn(Arrays.asList(textMessage1, textMessage2))
            .when(textMessageServiceMock).getTextMessagesForCurrentUser();

        restTextMessageMockMvc.perform(get("/app/rest/textmessages"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].sender.login").value("testSender"))
                .andExpect(jsonPath("$[0].content").value(DEFAULT_CONTENT))
                .andExpect(jsonPath("$[0].timestamp").exists());

    }

    /**
     * test if OK is returned if the service deleted the message successfully
     */
    @Test
    public void testDELETE_expectOK_receiverCanDeleteMessage() throws Exception {
        doReturn(new TextMessage()).when(textMessageServiceMock).deleteTextMessage(100L);

        restTextMessageMockMvc.perform(delete("/app/rest/textmessages/{id}", 100L))
                .andExpect(status().isOk());

    }

    /**
     * test if BAD REQUEST is returned if the service throw san IllegalArgumentException while deleting a message
     */
    @Test
    public void testDELETE_expectBAD_REQUEST_cannotDeleteForeignMessages() throws Exception {
        IllegalValueException illegalValueException = new IllegalValueException(
            "test.notreceiver", "Current user is not authorized to delete this message.");
        doThrow(illegalValueException).when(textMessageServiceMock).deleteTextMessage(100L);

        restTextMessageMockMvc.perform(delete("/app/rest/textmessages/{id}", 100L))
                .andExpect(status().isBadRequest());

    }

}
