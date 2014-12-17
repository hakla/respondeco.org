package org.respondeco.respondeco.repository;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.respondeco.respondeco.Application;
import org.respondeco.respondeco.domain.*;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.AbstractTransactionalJUnit4SpringContextTests;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import org.springframework.test.context.support.DirtiesContextTestExecutionListener;
import org.springframework.test.context.transaction.TransactionalTestExecutionListener;
import org.springframework.test.context.web.WebAppConfiguration;

import javax.inject.Inject;
import java.math.BigDecimal;
import java.util.List;

import static org.junit.Assert.*;

/**
 * Created by Clemens Puehringer on 02/12/14.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Application.class)
@WebAppConfiguration
@TestExecutionListeners({
    DependencyInjectionTestExecutionListener.class,
    DirtiesContextTestExecutionListener.class,
    TransactionalTestExecutionListener.class })
public class TextMessageRepositoryTest extends AbstractTransactionalJUnit4SpringContextTests {

    @Inject
    private UserRepository userRepository;

    @Inject
    private TextMessageRepository textMessageRepository;

    private User receiver;
    private User receiver2;

    private TextMessage textMessage;
    private TextMessage textMessage2;

    @Before
    public void setup() {

        receiver = new User();
        receiver.setLogin("receiver");
        receiver.setGender(Gender.UNSPECIFIED);
        userRepository.save(receiver);

        receiver2 = new User();
        receiver2.setLogin("receiver2");
        receiver2.setGender(Gender.UNSPECIFIED);
        userRepository.save(receiver2);

        textMessage = new TextMessage();
        textMessage.setReceiver(receiver);
        textMessageRepository.save(textMessage);

        textMessage2 = new TextMessage();
        textMessage2.setReceiver(receiver);
        textMessageRepository.save(textMessage2);

    }

    @Test
    public void testFindByReceiverAndActiveIsTrue() {

        List<TextMessage> textMessages = textMessageRepository.findByReceiverAndActiveIsTrue(receiver);

        assertNotNull(textMessages);
        assertTrue(textMessages.contains(textMessage));
        assertTrue(textMessages.contains(textMessage));

        textMessage2.setReceiver(receiver2);

        textMessages = textMessageRepository.findByReceiverAndActiveIsTrue(receiver);
        assertNotNull(textMessages);
        assertTrue(textMessages.contains(textMessage));
        assertFalse(textMessages.contains(textMessage2));

    }

    @Test
    public void testCountByReceiverAndActiveIsTrueAndReadIsFalse() {

        Long count = textMessageRepository.countByReceiverAndActiveIsTrueAndReadIsFalse(receiver);

        assertTrue(count.equals(2L));

        textMessage2.setReceiver(receiver2);

        count = textMessageRepository.countByReceiverAndActiveIsTrueAndReadIsFalse(receiver);

        assertTrue(count.equals(1L));

        textMessage2.setRead(true);

        count = textMessageRepository.countByReceiverAndActiveIsTrueAndReadIsFalse(receiver);

        assertTrue(count.equals(1L));

        textMessage2.setActive(false);

        count = textMessageRepository.countByReceiverAndActiveIsTrueAndReadIsFalse(receiver);

        assertTrue(count.equals(1L));
    }

}
