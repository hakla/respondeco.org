package org.respondeco.respondeco.web.rest.mapping;

import org.junit.Before;
import org.junit.Test;
import org.respondeco.respondeco.domain.Project;
import org.respondeco.respondeco.domain.TextMessage;
import org.respondeco.respondeco.domain.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.Map;

import static org.junit.Assert.assertEquals;

/**
 * Created by clemens on 11/03/15.
 */
public class ResponseMapperTest {

    private final Logger log = LoggerFactory.getLogger(ResponseMapperTest.class);

    private TextMessage textMessage;
    private Project project;

    private ResponseMapper responseMapper;

    @Before
    public void setup() {
        textMessage = new TextMessage();
        textMessage.setId(20L);
        textMessage.setContent("haha test");

        User user = new User();
        user.setId(50L);
        user.setLogin("test@user");
        user.setFirstName("Bob");
        textMessage.setReceiver(user);

        responseMapper = new ResponseMapper();
    }

    @Test
    public void testFirstLevel() throws Exception {
        Map<String, Object> mapped = responseMapper.map(textMessage, Arrays.asList("content"));
        log.debug("{}", mapped);

        assertEquals(20L, mapped.get("id"));
        assertEquals("haha test", mapped.get("content"));
    }

    @Test
    public void testSecondLevel() throws Exception {
        Map<String, Object> mapped = responseMapper.map(textMessage, Arrays.asList("receiver(login,firstName)"));
        log.debug("{}", mapped);

        assertEquals(50L, ((Map<String, Object>) mapped.get("receiver")).get("id"));
        assertEquals("test@user", ((Map<String, Object>)mapped.get("receiver")).get("login"));
        assertEquals("Bob", ((Map<String, Object>)mapped.get("receiver")).get("firstName"));
    }
}
