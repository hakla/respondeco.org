package org.respondeco.respondeco.repository;

import org.joda.time.LocalDate;
import org.junit.Test;
import org.respondeco.respondeco.RepositoryLayerTest;
import org.respondeco.respondeco.domain.PersistentToken;
import org.respondeco.respondeco.domain.User;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertEquals;

/**
 * Created by clemens on 18/09/15.
 */
public class PersistentTokenRepositoryTest extends RepositoryLayerTest {

    @Test
    public void testName() throws Exception {
        assertEquals(0, persistentTokenRepository.findByUser(model.USER_SAVED_MINIMAL).size());
        LocalDate referenceDate = new LocalDate();
        PersistentToken currentTimeToken = generateUserToken(model.USER_SAVED_MINIMAL, "1111-1111", referenceDate);
        PersistentToken oldToken = generateUserToken(model.USER_SAVED_MINIMAL, "2222-2222", referenceDate.minusDays(32));
        persistentTokenRepository.saveAndFlush(currentTimeToken);
        persistentTokenRepository.saveAndFlush(oldToken);
        assertThat(persistentTokenRepository.findByUser(model.USER_SAVED_MINIMAL)).hasSize(2);
        assertThat(persistentTokenRepository.findByTokenDateBefore(referenceDate)).hasSize(1);
    }

    private PersistentToken generateUserToken(User user, String tokenSeries, LocalDate localDate) {
        PersistentToken token = new PersistentToken();
        token.setSeries(tokenSeries);
        token.setUser(user);
        token.setTokenValue(tokenSeries + "-data");
        token.setTokenDate(localDate);
        token.setIpAddress("127.0.0.1");
        token.setUserAgent("Test agent");
        return token;
    }

}
