package org.respondeco.respondeco.service;

import lombok.Setter;
import org.joda.time.DateTime;
import org.respondeco.respondeco.domain.TextMessage;
import org.respondeco.respondeco.domain.User;
import org.respondeco.respondeco.repository.ProfilePictureRepository;
import org.respondeco.respondeco.repository.TextMessageRepository;
import org.respondeco.respondeco.repository.UserRepository;
import org.respondeco.respondeco.service.exception.NoSuchUserException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;
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

    public TextMessage createTextMessage(String receiver, String content) throws NoSuchUserException {
        if(content == null || content.length() <= 0) {
            throw new IllegalArgumentException("Content must not be empty");
        }
        User currentUser = userService.getUserWithAuthorities();
        if(currentUser.getLogin().equals(receiver)) {
            throw new IllegalArgumentException(String.format("Receiver cannot be equal to sender: %s", receiver));
        }
        if(userRepository.exists(receiver) == false) {
            throw new NoSuchUserException(String.format("Receiver %s does not exist", receiver));
        }
        TextMessage newTextMessage = new TextMessage();
        newTextMessage.setSender(currentUser.getLogin());
        newTextMessage.setTimestamp(DateTime.now());
        newTextMessage.setReceiver(receiver);
        newTextMessage.setContent(content);
        textMessageRepository.save(newTextMessage);
        return newTextMessage;
    }

    public List<TextMessage> getTextMessagesForCurrentUser() {
        User currentUser = userService.getUserWithAuthorities();
        return textMessageRepository.findByReceiverAndActiveIsTrue(currentUser.getLogin());
    }

    public TextMessage deleteTextMessage(Long id) {
        TextMessage textMessage = textMessageRepository.findOne(id);
        if(textMessage == null) {
            throw new IllegalArgumentException(String.format("A text message with id %d does not exist", id));
        }
        User currentUser = userService.getUserWithAuthorities();
        if(currentUser.getLogin().equals(textMessage.getReceiver()) == false) {
            throw new IllegalArgumentException(
                    String.format("User %s is not the receiver of the text message %d", currentUser.getLogin(), id));
        }
        textMessage.setActive(false);
        textMessageRepository.save(textMessage);
        return textMessage;
    }

}
