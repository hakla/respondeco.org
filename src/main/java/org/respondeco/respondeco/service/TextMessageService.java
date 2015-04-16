package org.respondeco.respondeco.service;

import org.joda.time.DateTime;
import org.respondeco.respondeco.domain.TextMessage;
import org.respondeco.respondeco.domain.User;
import org.respondeco.respondeco.repository.TextMessageRepository;
import org.respondeco.respondeco.repository.UserRepository;
import org.respondeco.respondeco.service.exception.IllegalValueException;
import org.respondeco.respondeco.service.exception.NoSuchEntityException;
import org.respondeco.respondeco.web.rest.dto.TextMessageResponseDTO;
import org.respondeco.respondeco.web.rest.dto.UserDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by clemens on 02/11/14.
 */

@Service
@Transactional
public class TextMessageService {

    private final Logger log = LoggerFactory.getLogger(TextMessageService.class);

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
        if(content == null || content.length() <= 0) {
            throw new IllegalValueException("global.textmessages.error.contentlength", "Content must not be empty");
        }
        User currentUser = userService.getUserWithAuthorities();
        User receivingUser = userService.getUser(receiverId);
        if(currentUser.equals(receivingUser)) {
            throw new IllegalValueException("global.textmessages.error.receivercurrentuser",
                String.format("Receiver cannot be equal to sender: %s", receiverId));
        }

        if(receivingUser == null) {
            throw new NoSuchEntityException(String.format("Receiver %s does not exist", receiverId));
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
            throw new IllegalValueException("global.textmessages.error.usernotfound",
                String.format("A text message with id %d does not exist", id));
        }
        User currentUser = userService.getUserWithAuthorities();
        if(currentUser.equals(textMessage.getReceiver()) == false) {
            throw new IllegalValueException("global.textmessages.error.notreceiver",
                    String.format("User %s is not the receiver of the text message %d", currentUser.getLogin(), id));
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
