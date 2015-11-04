package org.respondeco.respondeco.service;

import org.joda.time.DateTime;
import org.respondeco.respondeco.domain.TextMessage;
import org.respondeco.respondeco.domain.User;
import org.respondeco.respondeco.repository.TextMessageRepository;
import org.respondeco.respondeco.repository.UserRepository;
import org.respondeco.respondeco.service.exception.IllegalValueException;
import org.respondeco.respondeco.service.exception.NoSuchEntityException;
import org.respondeco.respondeco.service.exception.OperationForbiddenException;
import org.respondeco.respondeco.service.exception.ServiceException;
import org.respondeco.respondeco.service.util.Assert;
import org.respondeco.respondeco.web.rest.dto.TextMessageResponseDTO;
import org.respondeco.respondeco.web.rest.dto.UserDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by clemens on 02/11/14.
 */

@Service
@Transactional
public class TextMessageService {

    private final Logger log = LoggerFactory.getLogger(TextMessageService.class);

    public static final ServiceException.ErrorPrefix ERROR_PREFIX = new ServiceException.ErrorPrefix("textmessage");
    public static final String ERROR_NO_AUTHORITY   = "no_authority";
    public static final String ERROR_NO_CONTENT     = "no_content";
    public static final String ERROR_SEND_TO_SELF   = "send_to_self";

    private TextMessageRepository textMessageRepository;
    private UserService userService;
    private UserRepository userRepository;

    @Inject
    public TextMessageService(TextMessageRepository textMessageRepository, UserService userService,
                              UserRepository userRepository) {
        this.textMessageRepository = textMessageRepository;
        this.userService = userService;
        this.userRepository = userRepository;
    }

    public TextMessage createTextMessage(Long receiverId, String content) throws IllegalValueException {
        Assert.isValid(content, ERROR_PREFIX.join(ERROR_NO_CONTENT), "The content of a message must not be empty",
            null, null);
        User currentUser = userService.getUserWithAuthorities();
        User receivingUser = userService.getUser(receiverId);
        if(currentUser.equals(receivingUser)) {
            throw new OperationForbiddenException(ERROR_PREFIX.join(ERROR_SEND_TO_SELF),
                "The sender of a message (%2) can not be equal to the receiver",
                Arrays.asList("userId", "userLogin"),
                Arrays.asList(currentUser.getId(), currentUser.getLogin()));
        }

        if(receivingUser == null) {
            throw new NoSuchEntityException(UserService.ERROR_PREFIX, receiverId, User.class);
        }

        TextMessage newTextMessage = new TextMessage();
        newTextMessage.setSender(currentUser);
        newTextMessage.setTimestamp(DateTime.now());
        newTextMessage.setReceiver(receivingUser);
        newTextMessage.setContent(content);
        log.debug("saving text message {}", newTextMessage);
        textMessageRepository.save(newTextMessage);
        return newTextMessage;
    }

    public List<TextMessageResponseDTO> getTextMessagesForCurrentUser() {
        User currentUser = userService.getUserWithAuthorities();
        log.debug("getting text messages for user {}", currentUser);
        List<TextMessageResponseDTO> response =
                textMessagesToDTO(textMessageRepository.findByReceiverAndActiveIsTrue(currentUser));
        log.debug("got messages for user {}: {}", currentUser, response);
        return response;
    }

    public List<TextMessageResponseDTO> getTextMessagesForUser(Long id) {
        User user = userRepository.findOne(id);
        return textMessagesToDTO(textMessageRepository.findByReceiverAndActiveIsTrue(user));
    }

    public TextMessage deleteTextMessage(Long id) throws IllegalValueException {
        TextMessage textMessage = textMessageRepository.findOne(id);
        if(textMessage == null) {
            throw new NoSuchEntityException(ERROR_PREFIX, id, TextMessage.class);
        }
        User currentUser = userService.getUserWithAuthorities();
        if(currentUser == null) {
            throw new OperationForbiddenException(ERROR_PREFIX.join(ERROR_NO_AUTHORITY),
                "Current user (not logged in) has no authority to delete textmessage %1",
                Arrays.asList("textMessageId"),
                Arrays.asList(textMessage.getId()));
        }
        if(!textMessage.getReceiver().equals(currentUser)) {
            throw new OperationForbiddenException(ERROR_PREFIX.join(ERROR_NO_AUTHORITY),
                "Current user (%2) has no authority to delete textmessage %3",
                Arrays.asList("userId", "userLogin", "textMessageId"),
                Arrays.asList(currentUser.getId(), currentUser.getLogin(), textMessage.getId()));
        }
        textMessage.setActive(false);
        textMessageRepository.save(textMessage);
        return textMessage;
    }

    private TextMessageResponseDTO textMessageToDTO(TextMessage message) {
        return new TextMessageResponseDTO(
                message.getId(),
                new UserDTO(message.getSender()),
                message.getContent(),
                message.getTimestamp(),
                !message.isRead());
    }

    private List<TextMessageResponseDTO> textMessagesToDTO(List<TextMessage> messages) {
        List<TextMessageResponseDTO> result = new ArrayList<TextMessageResponseDTO>();
        for(TextMessage message : messages) {
            result.add(textMessageToDTO(message));
        }
        return result;
    }

    public Long countNewMessages(User user) {
        return textMessageRepository.countByReceiverAndActiveIsTrueAndReadIsFalse(user);
    }

    public void setRead(Long id) {
        TextMessage message = textMessageRepository.findOne(id);
        message.setRead(true);
        textMessageRepository.save(message);
    }
}
