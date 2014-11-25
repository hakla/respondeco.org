package org.respondeco.respondeco.repository;

import org.joda.time.LocalDate;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.respondeco.respondeco.Application;
import org.respondeco.respondeco.domain.*;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import org.springframework.test.context.support.DirtiesContextTestExecutionListener;
import org.springframework.test.context.transaction.TransactionalTestExecutionListener;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;

import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.*;

/**
 * Created by Clemens Puehringer on 21/11/14.
 */

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Application.class)
@WebAppConfiguration
@TestExecutionListeners({ DependencyInjectionTestExecutionListener.class,
        DirtiesContextTestExecutionListener.class,
        TransactionalTestExecutionListener.class })
public class ProjectRepositoryTest {

    @Inject
    private UserRepository userRepository;

    @Inject
    private OrganizationRepository organizationRepository;

    @Inject
    private ProjectRepository projectRepository;

    @Inject
    private PropertyTagRepository propertyTagRepository;

    private User orgAdmin;
    private Organization organization;
    private Project project;

    @Before
    public void setup() {
        propertyTagRepository.deleteAll();
        propertyTagRepository.flush();
        projectRepository.deleteAll();
        projectRepository.flush();
        organizationRepository.deleteAll();
        organizationRepository.flush();
        userRepository.deleteAll();
        userRepository.flush();

        orgAdmin = new User();
        orgAdmin.setLogin("orgAdmin");
        orgAdmin.setGender(Gender.UNSPECIFIED);
        userRepository.save(orgAdmin);

        organization = new Organization();
        organization.setName("testorg");
        organization.setOwner(orgAdmin);
        organizationRepository.save(organization);

        project = new Project();
        project.setName("testproject");
        project.setPurpose("testpurpose");
        project.setConcrete(false);
        project.setOrganization(organization);
        project.setManager(orgAdmin);
    }

    @Test
    public void testSave_shouldSaveCorrectly() throws Exception {
        LocalDate startDate = LocalDate.now();
        LocalDate endDate = LocalDate.now().plusDays(3);
        project.setName("testproject");
        project.setPurpose("testpurpose");
        project.setConcrete(true);
        project.setStartDate(startDate);
        project.setEndDate(endDate);

        projectRepository.save(project);
        Project savedProject = projectRepository.findOne(project.getId());

        assertEquals(savedProject.getName(), "testproject");
        assertEquals(savedProject.getPurpose(), "testpurpose");
        assertEquals(savedProject.isConcrete(), true);
        assertEquals(savedProject.getStartDate(), startDate);
        assertEquals(savedProject.getEndDate(), endDate);
    }

    @Test
    public void testFindByActiveIsTrue_shouldReturnOnlyActive() throws Exception {
        Project project2 = new Project();
        project2.setName("project2");
        project2.setPurpose("testpurpose2");
        project2.setConcrete(false);
        project2.setOrganization(organization);
        project2.setManager(orgAdmin);

        projectRepository.save(project);
        projectRepository.save(project2);

        List<Project> projects = projectRepository.findByActiveIsTrue();
        assertTrue(projects.size() == 2);
        assertTrue(projects.contains(project));
        assertTrue(projects.contains(project2));

        project.setActive(false);
        projectRepository.save(project);

        projects = projectRepository.findByActiveIsTrue();
        assertTrue(projects.size() == 1);
        assertFalse(projects.contains(project));
        assertTrue(projects.contains(project2));
    }

    @Test
    public void testFindByIdAndActiveIsTrue_shouldReturnActiveProject() throws Exception {
        projectRepository.save(project);

        Project savedProject = projectRepository.findByIdAndActiveIsTrue(project.getId());
        assertTrue(savedProject.equals(project));

    }

    @Test
    public void testFindByIdAndActiveIsTrue_shouldNotReturnInactiveProject() throws Exception {
        project.setActive(false);
        projectRepository.save(project);

        Project savedProject = projectRepository.findByIdAndActiveIsTrue(project.getId());
        assertNull(savedProject);

    }

    @Test
    @Transactional //to enable lazy initialization
    public void testFindByNameAndTags_shouldFindProjectByName() throws Exception {
        projectRepository.save(project);
        List<Project> projects = projectRepository.findByNameAndTags("testproject", null, null);
        assertEquals(1, projects.size());
        System.out.println(projects.get(0));
        assertEquals("testproject", projects.get(0).getName());
    }

    @Test
    @Transactional
    public void testFindByNameAndTags_shouldFindProjectsByNameLike() throws Exception {
        Project project2 = new Project();
        project2.setName("project2");
        project2.setPurpose("blub");
        project2.setConcrete(false);
        project2.setOrganization(organization);
        project2.setManager(orgAdmin);

        Project project3 = new Project();
        project3.setName("xxxxxxxx");
        project3.setPurpose("blub");
        project3.setConcrete(false);
        project3.setOrganization(organization);
        project3.setManager(orgAdmin);

        projectRepository.save(project);
        projectRepository.save(project2);
        projectRepository.save(project3);
        List<Project> projects = projectRepository.findByNameAndTags("project", null, null);
        assertEquals(2, projects.size());

    }

    @Test
    @Transactional
    public void testFindByNameAndTags_shouldFindProjectsByTags() throws Exception {
        PropertyTag tag1 = new PropertyTag();
        tag1.setName("testtag");
        PropertyTag tag2 = new PropertyTag();
        tag2.setName("blub");
        PropertyTag tag3 = new PropertyTag();
        tag3.setName("xxxxxxx");

        Project project2 = new Project();
        project2.setName("project2");
        project2.setPurpose("blub");
        project2.setConcrete(false);
        project2.setOrganization(organization);
        project2.setManager(orgAdmin);

        Project project3 = new Project();
        project3.setName("xxxxxxxx");
        project3.setPurpose("blub");
        project3.setConcrete(false);
        project3.setOrganization(organization);
        project3.setManager(orgAdmin);

        project.setPropertyTags(Arrays.asList(tag1, tag3));
        project2.setPropertyTags(Arrays.asList(tag3));
        project3.setPropertyTags(Arrays.asList(tag1, tag2));

        propertyTagRepository.save(tag1);
        propertyTagRepository.save(tag2);
        propertyTagRepository.save(tag3);
        propertyTagRepository.flush();

        projectRepository.save(project);
        projectRepository.save(project2);
        projectRepository.save(project3);
        projectRepository.flush();

        List<Project> projects = projectRepository.findByNameAndTags(null, Arrays.asList(tag3.getName()), null);
        assertEquals(2, projects.size());
        assertTrue(projects.contains(project));
        assertTrue(projects.contains(project2));
    }

    @Test
    @Transactional
    public void testFindByNameAndTags_shouldFindProjectsByNameOrTags() throws Exception {
        PropertyTag tag1 = new PropertyTag();
        tag1.setName("testtag");
        PropertyTag tag2 = new PropertyTag();
        tag2.setName("blub");
        PropertyTag tag3 = new PropertyTag();
        tag3.setName("xxxxxxx");

        Project project2 = new Project();
        project2.setName("project2");
        project2.setPurpose("blub");
        project2.setConcrete(false);
        project2.setOrganization(organization);
        project2.setManager(orgAdmin);

        Project project3 = new Project();
        project3.setName("xxxxxxxx");
        project3.setPurpose("blub");
        project3.setConcrete(false);
        project3.setOrganization(organization);
        project3.setManager(orgAdmin);

        project.setPropertyTags(Arrays.asList(tag1, tag3));
        project2.setPropertyTags(Arrays.asList(tag3));
        project3.setPropertyTags(Arrays.asList(tag2));

        propertyTagRepository.save(tag1);
        propertyTagRepository.save(tag2);
        propertyTagRepository.save(tag3);
        propertyTagRepository.flush();

        projectRepository.save(project);
        projectRepository.save(project2);
        projectRepository.save(project3);
        projectRepository.flush();

        List<Project> projects = projectRepository.findByNameAndTags("xxxxx", Arrays.asList(tag1.getName()), null);
        assertEquals(2, projects.size());
        assertTrue(projects.contains(project));
        assertTrue(projects.contains(project3));

    }

    /*
    @Test
    public void testFindByNameAndTags_shouldWorkWithOffsetAndLimit() throws Exception {
        Project p;
        for(int i=0;i<20;i++) {
            p = new Project();
            p.setName("project" + i);
            p.setPurpose("purpose");
            p.setConcrete(false);
            p.setManagerId(orgAdmin.getId());
            p.setOrganizationId(organization.getId());
            projectRepository.save(p);
        }
        projectRepository.flush();

        PageRequest request = new PageRequest(5, 15, new Sort(
                new Sort.Order(Sort.Direction.ASC, "name")
        ));
        List<Project> projects = projectRepository.findByNameAndTags("project", null, request);
        assertEquals(10, projects.size());
        for(int i=5;i<15;i++) {
            assertEquals("project" + i, projects.get(i).getName());
        }
    }
    */
}
