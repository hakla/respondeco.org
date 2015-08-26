package org.respondeco.respondeco;

import org.junit.Before;
import org.respondeco.respondeco.repository.OrganizationRepository;
import org.respondeco.respondeco.repository.ProjectRepository;
import org.respondeco.respondeco.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.test.context.transaction.TransactionalTestExecutionListener;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;

import static org.junit.Assert.assertNotNull;

/**
 * Created by Clemens Puehringer on 25/08/15.
 */
@TestExecutionListeners(TransactionalTestExecutionListener.class)
@TransactionConfiguration(defaultRollback = true)
@Transactional
public class DatabaseBackedTest extends ModelBackedTest {

    @Inject protected UserRepository userRepository;
    @Inject protected OrganizationRepository organizationRepository;
    @Inject protected ProjectRepository projectRepository;

    @Before
    public void setupDatabase() {
        saveUsers();
        saveOrganizations();
        saveProjects();
    }

    private void saveUsers() {
        model.USER_SAVED_MINIMAL.setId(null);
        model.USER_SAVED_MINIMAL.setOrganization(null);
        model.USER1_OWNS_ORG1_MANAGES_P1.setId(null);
        model.USER1_OWNS_ORG1_MANAGES_P1.setOrganization(null);
        userRepository.save(model.USER_SAVED_MINIMAL);
        userRepository.save(model.USER1_OWNS_ORG1_MANAGES_P1);
        assertNotNull(model.USER_SAVED_MINIMAL.getId());
        assertNotNull(model.USER1_OWNS_ORG1_MANAGES_P1.getId());
        model.USER_SAVED_MINIMAL.setOrganization(model.ORGANIZATION_SAVED_MINIMAL);
        model.USER1_OWNS_ORG1_MANAGES_P1.setOrganization(model.ORGANIZATION1_GOVERNS_P1);
    }

    private void saveOrganizations() {
        model.ORGANIZATION_SAVED_MINIMAL.setId(null);
        model.ORGANIZATION1_GOVERNS_P1.setId(null);
        organizationRepository.save(model.ORGANIZATION_SAVED_MINIMAL);
        organizationRepository.save(model.ORGANIZATION1_GOVERNS_P1);
        assertNotNull(model.ORGANIZATION_SAVED_MINIMAL.getId());
        assertNotNull(model.ORGANIZATION1_GOVERNS_P1.getId());
    }

    private void saveProjects() {
        model.PROJECT1.setId(null);
        projectRepository.save(model.PROJECT1);
        assertNotNull(model.PROJECT1.getId());
    }

}
