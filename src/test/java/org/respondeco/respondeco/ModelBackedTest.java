package org.respondeco.respondeco;

import org.junit.Before;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.respondeco.respondeco.repository.OrganizationRepository;
import org.respondeco.respondeco.repository.ProjectRepository;
import org.respondeco.respondeco.repository.UserRepository;
import org.respondeco.respondeco.service.OrganizationService;
import org.respondeco.respondeco.service.ProjectService;
import org.respondeco.respondeco.service.UserService;
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

    @Mock public UserRepository userRepositoryMock;
    @Mock public OrganizationRepository organizationRepositoryMock;
    @Mock public ProjectRepository projectRepositoryMock;

    @Mock public UserService userServiceMock;
    @Mock public OrganizationService organizationServiceMock;
    @Mock public ProjectService projectServiceMock;

    public DomainModel model;

    @Before
    public void baseSetup() {
        MockitoAnnotations.initMocks(this);
        model = new DomainModel();
    }

}
