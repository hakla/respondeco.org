package org.respondeco.respondeco;

import org.junit.Before;
import org.junit.runner.RunWith;
import org.respondeco.respondeco.testutil.domain.DomainModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.AbstractJUnit4SpringContextTests;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

/**
 * Created by Clemens Puehringer on 25/08/15.
 */
@SpringApplicationConfiguration(classes = Application.class)
@WebAppConfiguration
@ActiveProfiles("dev")
public class ModelBackedTest extends AbstractJUnit4SpringContextTests {

    protected Logger log = LoggerFactory.getLogger(this.getClass());

    protected DomainModel model;

    @Before
    public void baseSetup() {
        model = new DomainModel();
    }

}
