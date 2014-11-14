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

        when(userServiceMock.getUserWithAuthorities()).thenReturn(currentUser);
        when(userRepositoryMock.exists(1L)).thenReturn(true);

        doAnswer(invocation -> {
            Object[] args = invocation.getArguments();
            savedMessage = (TextMessage) args[0];
            return (null);
        })
        .when(textMessageRepositoryMock).save(isA(TextMessage.class));

        textMessageService.createTextMessage(receiver, content);

        assertEquals(savedMessage.getSender(), currentUser.getLogin());
        assertEquals(savedMessage.getReceiver(), receiver);
        assertEquals(savedMessage.getContent(), content);
        assertNotNull(savedMessage.getTimestamp());

        verify(textMessageRepositoryMock, times(1)).save(isA(TextMessage.class));
        verify(userServiceMock, times(1)).getUserWithAuthorities();
        verify(userRepositoryMock, times(1)).exists(1L);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testCreateTextMessage_contentMustNotBeNull() throws Exception {
        String receiver = "testReceiver";
        textMessageService.createTextMessage(receiver, null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testCreateTextMessage_contentMustNotBeEmpty() throws Exception {
        String receiver = "testReceiver";
        String content = "";
        textMessageService.createTextMessage(receiver, content);
    }

    @Test(expected = NoSuchUserException.class)
    public void testCreateTextMessage_receiverHasToExist() throws Exception {
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

        TextMessage message1 = new TextMessage();
        message1.setTimestamp(DateTime.now());
        message1.setSender(1L);
        message1.setReceiver(2L);
        message1.setContent("testContent1");

        TextMessage message2 = new TextMessage();
        message2.setTimestamp(DateTime.now());
        message2.setSender(3L);
        message2.setReceiver(2L);
        message2.setContent("testContent2");

        when(userServiceMock.getUserWithAuthorities()).thenReturn(currentUser);
        when(textMessageRepositoryMock.findByReceiverAndActiveIsTrue(2L))
                .thenReturn(Arrays.asList(message1, message2));

        List<TextMessage> messages = textMessageService.getTextMessagesForCurrentUser();

        assertTrue(messages.size() == 2);
        verify(textMessageRepositoryMock, times(1)).findByReceiverAndActiveIsTrue(2L);
        verify(userServiceMock, times(1)).getUserWithAuthorities();
    }

    @Test
    public void testDeleteTextMessage_shouldSetActiveToFalse() throws Exception {
        TextMessage testMessage = new TextMessage();
        testMessage.setId(1L);
        testMessage.setActive(true);
        testMessage.setReceiver(1L);
        User currentUser = new User();
        currentUser.setLogin("testReceiver");

        when(textMessageRepositoryMock.findOne(1L)).thenReturn(testMessage);
        when(userServiceMock.getUserWithAuthorities()).thenReturn(currentUser);

        doAnswer(invocation -> {
            Object[] args = invocation.getArguments();
            savedMessage = (TextMessage) args[0];
            return (null);
        })
        .when(textMessageRepositoryMock).save(isA(TextMessage.class));

        textMessageService.deleteTextMessage(1L);

        verify(textMessageRepositoryMock, times(1)).save(isA(TextMessage.class));
        verify(userServiceMock, times(1)).getUserWithAuthorities();
        assertFalse(savedMessage.isActive());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testDeleteTextMessage_messageHasToExist() throws Exception {
        when(textMessageRepositoryMock.findOne(1L)).thenReturn(null);
        textMessageService.deleteTextMessage(1L);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testDeleteTextMessage_currentUserHasToBeReceiver() throws Exception {
        TextMessage testMessage = new TextMessage();
        testMessage.setId(1L);
        testMessage.setActive(true);
        testMessage.setReceiver(1L);
        User currentUser = new User();
        currentUser.setId(1L);
        currentUser.setLogin("someUser");

        when(textMessageRepositoryMock.findOne(1L)).thenReturn(testMessage);
        when(userServiceMock.getUserWithAuthorities()).thenReturn(currentUser);

        textMessageService.deleteTextMessage(1L);
    }

}
