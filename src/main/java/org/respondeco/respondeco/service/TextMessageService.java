package org.respondeco.respondeco.service;

import org.joda.time.DateTime;
import org.respondeco.respondeco.domain.TextMessage;
import org.respondeco.respondeco.domain.User;
import org.respondeco.respondeco.repository.ProfilePictureRepository;
import org.respondeco.respondeco.repository.TextMessageRepository;
import org.respondeco.respondeco.repository.UserRepository;
import org.respondeco.respondeco.service.exception.NoSuchUserException;
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

    @Inject
    private TextMessageRepository textMessageRepository;

    @Inject
    private UserService userService;

    @Inject
    private UserRepository userRepository;

    public TextMessage createTextMessage(String receiver, String content) throws NoSuchUserException {
        if(userRepository.findOne(receiver) == null) {
            throw new NoSuchUserException(String.format("Receiver %s does not exist", receiver));
        }
        User currentUser = userService.getUserWithAuthorities();
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
        return textMessageRepository.findByReceiver(currentUser.getLogin());
    }

}
