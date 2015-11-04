package org.respondeco.respondeco;

import org.junit.After;
import org.junit.Before;
import org.respondeco.respondeco.testutil.domain.DomainModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.AbstractJUnit4SpringContextTests;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.util.ReflectionTestUtils;

/**
 * Created by Clemens Puehringer on 25/08/15.
 */
@SpringApplicationConfiguration(classes = Application.class)
@WebAppConfiguration
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
@ActiveProfiles("dev")
public abstract class AbstractBaseTest extends AbstractJUnit4SpringContextTests {

    protected Logger log = LoggerFactory.getLogger(this.getClass());

    public DomainModel model;

    @Before
    public void setupBase() {
        model = new DomainModel();
    }

}
