package org.respondeco.respondeco.service;

import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.respondeco.respondeco.Application;
import org.respondeco.respondeco.domain.TextMessage;
import org.respondeco.respondeco.domain.User;
import org.respondeco.respondeco.repository.TextMessageRepository;
import org.respondeco.respondeco.repository.UserRepository;
import org.respondeco.respondeco.service.exception.NoSuchUserException;
import org.respondeco.respondeco.web.rest.dto.TextMessageResponseDTO;
import org.respondeco.respondeco.testutil.ArgumentCaptor;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import javax.inject.Inject;

import java.util.Arrays;
import java.util.List;

import static junit.framework.TestCase.assertFalse;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.isA;
import static org.mockito.Mockito.*;

/**
 * Created by Clemens Puehringer on 06/11/14.
 */

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Application.class)
@WebAppConfiguration
public class TextMessageServiceTest {

    @Mock
    private TextMessageRepository textMessageRepositoryMock;

    @Mock
    private UserService userServiceMock;

    @Mock
    private UserRepository userRepositoryMock;

    private TextMessageService textMessageService;
    private TextMessage savedMessage;

    @Before
    public void setup() throws Exception {
        MockitoAnnotations.initMocks(this);
        textMessageService = new TextMessageService(textMessageRepositoryMock, userServiceMock, userRepositoryMock);
    }

    @Test
    public void testCreateTextMessage_shouldUseCurrentUserAsSender() throws Exception {
        String receiver = "testReceiver";
        String content = "testContent";
        User currentUser = new User();
        currentUser.setId(1L);
        currentUser.setLogin("testSender");
        User receivingUser = new User();
        receivingUser.setId(2L);
        receivingUser.setLogin("testReceiver");

        when(userServiceMock.getUserWithAuthorities()).thenReturn(currentUser);
        when(userRepositoryMock.findByLogin("testReceiver")).thenReturn(receivingUser);

        doAnswer(invocation -> {
            Object[] args = invocation.getArguments();
            savedMessage = (TextMessage) args[0];
            return (null);
        })
        .when(textMessageRepositoryMock).save(isA(TextMessage.class));

        textMessageService.createTextMessage(receiver, content);

        assertEquals(savedMessage.getSender(), currentUser);
        assertEquals(savedMessage.getReceiver(), receivingUser);
        assertEquals(savedMessage.getContent(), content);
        assertNotNull(savedMessage.getTimestamp());

        verify(textMessageRepositoryMock, times(1)).save(isA(TextMessage.class));
        verify(userServiceMock, times(1)).getUserWithAuthorities();
    }

    @Test(expected = IllegalArgumentException.class)
    public void testCreateTextMessage_shouldThrowExceptionBecauseContentIsNull() throws Exception {
        String receiver = "testReceiver";
        textMessageService.createTextMessage(receiver, null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testCreateTextMessage_shouldThrowExceptionBecauseContentIsEmpty() throws Exception {
        String receiver = "testReceiver";
        String content = "";
        textMessageService.createTextMessage(receiver, content);
    }

    @Test(expected = NoSuchUserException.class)
    public void testCreateTextMessage_shouldThrowExceptionBecauseReceiverDoesNotExist() throws Exception {
        String receiver = "testReceiver";
        String content = "testContent";
        User currentUser = new User();
        currentUser.setLogin("testSender");

        when(userServiceMock.getUserWithAuthorities()).thenReturn(currentUser);

        textMessageService.createTextMessage(receiver, content);
    }

    @Test
    public void testGetTextMessagesForCurrentUser_shouldReturnAllActiveMessages() throws Exception {
        User currentUser = new User();
        currentUser.setLogin("testReceiver");
        currentUser.setId(1L);
        User sender1 = new User();
        sender1.setId(2L);
        User sender2 = new User();
        sender2.setId(3L);

        TextMessage message1 = new TextMessage();
        message1.setTimestamp(DateTime.now());
        message1.setSender(sender1);
        message1.setReceiver(currentUser);
        message1.setContent("testContent1");

        TextMessage message2 = new TextMessage();
        message2.setTimestamp(DateTime.now());
        message2.setSender(sender2);
        message2.setReceiver(currentUser);
        message2.setContent("testContent2");

        when(userServiceMock.getUserWithAuthorities()).thenReturn(currentUser);
        when(textMessageRepositoryMock.findByReceiverAndActiveIsTrue(currentUser))
                .thenReturn(Arrays.asList(message1, message2));

        List<TextMessageResponseDTO> messages = textMessageService.getTextMessagesForCurrentUser();

        assertTrue(messages.size() == 2);
        verify(textMessageRepositoryMock, times(1)).findByReceiverAndActiveIsTrue(currentUser);
        verify(userServiceMock, times(1)).getUserWithAuthorities();
    }

    @Test
    public void testDeleteTextMessage_shouldSetActiveToFalse() throws Exception {
        User currentUser = new User();
        currentUser.setId(1L);
        currentUser.setLogin("testReceiver");
        TextMessage testMessage = new TextMessage();
        testMessage.setId(1L);
        testMessage.setActive(true);
        testMessage.setReceiver(currentUser);

        when(textMessageRepositoryMock.findOne(1L)).thenReturn(testMessage);
        when(userServiceMock.getUserWithAuthorities()).thenReturn(currentUser);

        ArgumentCaptor<TextMessage> textMessageArgumentCaptor = ArgumentCaptor.forType(TextMessage.class, 0, false);
        doAnswer(textMessageArgumentCaptor).when(textMessageRepositoryMock).save(isA(TextMessage.class));

        textMessageService.deleteTextMessage(1L);
        savedMessage = textMessageArgumentCaptor.getValue();

        verify(textMessageRepositoryMock, times(1)).save(isA(TextMessage.class));
        verify(userServiceMock, times(1)).getUserWithAuthorities();
        assertFalse(savedMessage.isActive());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testDeleteTextMessage_shouldThrowExceptionBecauseMessageDoesNotExist() throws Exception {
        when(textMessageRepositoryMock.findOne(1L)).thenReturn(null);
        textMessageService.deleteTextMessage(1L);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testDeleteTextMessage_shouldThrowExceptionBecauseCurrentUserIsNotReceiver() throws Exception {
        User currentUser = new User();
        currentUser.setId(1L);
        currentUser.setLogin("someUser");
        User receiver = new User();
        receiver.setLogin("receiver");
        receiver.setId(2L);
        TextMessage testMessage = new TextMessage();
        testMessage.setId(1L);
        testMessage.setActive(true);
        testMessage.setReceiver(receiver);

        when(textMessageRepositoryMock.findOne(1L)).thenReturn(testMessage);
        when(userServiceMock.getUserWithAuthorities()).thenReturn(currentUser);

        textMessageService.deleteTextMessage(1L);
    }

}
