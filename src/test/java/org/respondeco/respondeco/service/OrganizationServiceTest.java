
package org.respondeco.respondeco.service;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.respondeco.respondeco.Application;
import org.respondeco.respondeco.domain.Organization;
import org.respondeco.respondeco.domain.User;
import org.respondeco.respondeco.repository.*;
import org.respondeco.respondeco.service.exception.*;
import org.respondeco.respondeco.testutil.ArgumentCaptor;
import org.respondeco.respondeco.testutil.domain.DomainModel;
import org.springframework.beans.BeanUtils;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import java.util.ArrayList;
import java.util.Arrays;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

/**
 * Created by Christoph Schiessl on 16/11/14.
 */

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Application.class)
@WebAppConfiguration
public class OrganizationServiceTest {

    @Mock
    private OrganizationRepository organizationRepositoryMock;

    @Mock
    private UserService userServiceMock;

    @Mock
    private UserRepository userRepositoryMock;

    @Mock
    private ImageRepository imageRepositoryMock;

    @Mock
    private ProjectService projectServiceMock;

    @Mock
    private PostingFeedRepository postingFeedRepositoryMock;

    @Mock
    private ProjectRepository projectRepositoryMock;

    @Mock
    private ResourceOfferRepository resourceOfferRepositoryMock;

    @Mock
    private ISOCategoryRepository isoCategoryRepository;

    private OrganizationService organizationService;

    private User defaultUser;
    private User orgOwner;
    private Organization testOrganization;
    private ArgumentCaptor<Organization> organizationArgumentCaptor;

    private DomainModel model;

    @Before
    public void setup() throws Exception {
        MockitoAnnotations.initMocks(this);
        organizationService = new OrganizationService(
            organizationRepositoryMock,
            userServiceMock,
            userRepositoryMock,
            imageRepositoryMock,
            projectServiceMock,
            projectRepositoryMock,
            postingFeedRepositoryMock,
            resourceOfferRepositoryMock,
            isoCategoryRepository);
        defaultUser = new User();
        defaultUser.setId(1L);
        defaultUser.setLogin("testUser");
        defaultUser.setFollowOrganizations(new ArrayList<>());
        orgOwner = new User();
        orgOwner.setId(2L);
        orgOwner.setLogin("testOwner");

        testOrganization = new Organization();
        testOrganization.setFollowingUsers(new ArrayList<>());
        testOrganization.setId(1L);

        model = new DomainModel();
        organizationArgumentCaptor = ArgumentCaptor.forType(Organization.class, 0, false);
    }

    @Test
    public void testCreateOrganization_shouldCreateOrganization() throws Exception {
        Organization organization = model.ORGANIZATION_NEW;
        organization.setOwner(model.USER_SAVED_MINIMAL);

        organizationService.create(organization);

        verify(organizationRepositoryMock, times(1)).findByName(organization.getName());
        verify(organizationRepositoryMock, times(1)).save(isA(Organization.class));
    }

    @Test(expected = IllegalValueException.class)
    public void testCreateOrganization_shouldThrow_orgNameAlreadyExists() throws Exception {
        when(organizationRepositoryMock.findByName(model.ORGANIZATION_NEW.getName()))
            .thenReturn(model.ORGANIZATION_NEW);
        organizationService.create(model.ORGANIZATION_NEW);
    }

    @Test(expected = IllegalValueException.class)
    public void testCreateOrganization_shouldThrow_nameMustNotBeEmpty() throws Exception {
        model.ORGANIZATION1_GOVERNS_P1.setName("");
        organizationService.create(model.ORGANIZATION1_GOVERNS_P1);
    }

    @Test(expected = IllegalValueException.class)
    public void testCreateOrganization_shouldThrow_ownerMustNotBeEmpty() throws Exception {
        organizationService.create(model.ORGANIZATION_NEW);
    }

    @Test
    public void testUpdateOrganization_shouldUpdateValidChanges() throws Exception {
        when(userServiceMock.getUserWithAuthorities()).thenReturn(model.USER1_OWNS_ORG1_MANAGES_P1);
        when(organizationRepositoryMock.findByOwner(model.USER1_OWNS_ORG1_MANAGES_P1)).thenReturn(model.ORGANIZATION1_GOVERNS_P1);
        when(organizationRepositoryMock.save(any(Organization.class))).thenAnswer(organizationArgumentCaptor);
        Organization organizationClone = new Organization();
        BeanUtils.copyProperties(model.ORGANIZATION1_GOVERNS_P1, organizationClone);
        organizationClone.setEmail("new@email.foo");

        organizationService.update(organizationClone);

        Organization savedOrganization = organizationArgumentCaptor.getValue();
        assertEquals("new@email.foo", savedOrganization.getEmail());
    }

    @Test(expected = OperationForbiddenException.class)
    public void testUpdateOrganization_shouldThrow_userNotOwner() throws Exception {
        when(userServiceMock.getUserWithAuthorities()).thenReturn(model.USER1_OWNS_ORG1_MANAGES_P1);
        when(organizationRepositoryMock.findByOwner(model.USER1_OWNS_ORG1_MANAGES_P1)).thenReturn(null);
        when(organizationRepositoryMock.save(any(Organization.class))).thenAnswer(organizationArgumentCaptor);
        Organization organizationClone = new Organization();
        BeanUtils.copyProperties(model.ORGANIZATION1_GOVERNS_P1, organizationClone);
        organizationClone.setEmail("new@email.foo");

        organizationService.update(organizationClone);
    }

    @Test
    public void testGetOrganizationByName() throws Exception {

        when(userServiceMock.getUserWithAuthorities()).thenReturn(orgOwner);

        Organization organization = organizationService.create(testOrganization);

        when(organizationRepositoryMock.findByName("testOrg")).thenReturn(organization);
        organizationService.getOrganizationByName("testOrg");

        assertEquals(organization.getName(), "testOrg");
    }

    @Test(expected = NoSuchEntityException.class)
    public void testGetOrganizationByName_NotExisting() throws Exception {

        when(userServiceMock.getUserWithAuthorities()).thenReturn(orgOwner);

        Organization organization = organizationService.create(testOrganization);

        when(organizationRepositoryMock.findByName("testOrg")).thenReturn(organization);
        organizationService.getOrganizationByName("test");
    }

    @Test
    public void testGetOrganization() throws Exception {

        when(userServiceMock.getUserWithAuthorities()).thenReturn(orgOwner);
        when(userRepositoryMock.exists(null)).thenReturn(true);

        Organization organization = organizationService.create(testOrganization);

        when(organizationRepositoryMock.findOne(organization.getId())).thenReturn(organization);

        Organization organization2 = organizationService.getById(organization.getId());

        assertEquals(organization, organization2);
    }

    @Test
    public void testGetOrganizations() throws Exception {

        when(userServiceMock.getUserWithAuthorities()).thenReturn(orgOwner);
        Organization organization1 = organizationService
            .create(testOrganization);

        when(userServiceMock.getUserWithAuthorities()).thenReturn(defaultUser);
        Organization organization2 = organizationService
            .create(testOrganization);

        Page<Organization> orgPage = new PageImpl<Organization>(Arrays.asList(organization1,organization2));
        when(organizationRepositoryMock.findAll(any(Pageable.class))).thenReturn(orgPage);

        Page<Organization> organizationPage = organizationService.get(null);

        assertEquals(organizationPage.getTotalElements(), 2L);
        verify(organizationRepositoryMock, times(1)).findAll(any(Pageable.class));
    }

    @Test
    public void testDeleteOrganization() throws Exception {

        when(userServiceMock.getUserWithAuthorities()).thenReturn(orgOwner);

        Organization organization = organizationService
            .create(testOrganization);

        when(organizationRepositoryMock.findOne(any(Long.class))).thenReturn(organization);
        doAnswer(organizationArgumentCaptor).when(organizationRepositoryMock).save(any(Organization.class));

        organizationService.delete(organization.getId());

        assertEquals(organizationArgumentCaptor.getValue().getActive(), false);
    }

    @Test(expected = NoSuchEntityException.class)
    public void testDeleteOrganization_NotExisting() throws Exception {

        when(userServiceMock.getUserWithAuthorities()).thenReturn(defaultUser);

        organizationService.delete(anyLong());
    }

    @Test
    public void testDeleteMember() throws Exception {

        when(userServiceMock.getUserWithAuthorities()).thenReturn(orgOwner);
        Organization organization = organizationService
            .create(testOrganization);
        organization.setVerified(true);
        defaultUser.setOrganization(organization);
        when(userRepositoryMock.findOne(defaultUser.getId())).thenReturn(defaultUser);
        when(organizationRepositoryMock.findOne(defaultUser.getOrganization().getId())).thenReturn(organization);
        organizationService.deleteMember(defaultUser.getId());

        verify(userRepositoryMock, times(1)).save(defaultUser);
    }

    @Test(expected = NoSuchEntityException.class)
    public void testDeleteMember_shouldThrowNoSuchUserException() throws Exception {

        when(userServiceMock.getUserWithAuthorities()).thenReturn(orgOwner);
        Organization organization = organizationService.create(testOrganization);
        defaultUser.setOrganization(organization);
        when(userRepositoryMock.findOne(defaultUser.getId())).thenReturn(defaultUser);
        when(organizationRepositoryMock.findOne(defaultUser.getOrganization().getId())).thenReturn(organization);
        organizationService.deleteMember(100L);

    }

    @Test(expected = NoSuchEntityException.class)
    public void testDeleteMember_shouldThrowNoSuchOrganizationException() throws Exception {

        when(userServiceMock.getUserWithAuthorities()).thenReturn(orgOwner);
        Organization organization = organizationService.create(testOrganization);
        defaultUser.setOrganization(organization);
        when(userRepositoryMock.findOne(defaultUser.getId())).thenReturn(defaultUser);
        when(organizationRepositoryMock.findOne(defaultUser.getOrganization().getId())).thenReturn(null);
        organizationService.deleteMember(defaultUser.getId());

    }

    @Test(expected = IllegalValueException.class)
    public void testDeleteMember_shouldThrowOperationForbiddenException() throws Exception {

        when(userServiceMock.getUserWithAuthorities()).thenReturn(orgOwner);
        Organization organization = organizationService
            .create(testOrganization);
        organization.setVerified(true);
        when(userServiceMock.getUserWithAuthorities()).thenReturn(defaultUser);
        defaultUser.setOrganization(organization);
        when(userRepositoryMock.findOne(defaultUser.getId())).thenReturn(defaultUser);
        when(organizationRepositoryMock.findOne(defaultUser.getOrganization().getId())).thenReturn(organization);
        organizationService.deleteMember(defaultUser.getId());

    }

    @Test(expected = NoSuchEntityException.class)
    public void testGetUserByOrgId_shouldThrowNoSuchOrganizationException() throws Exception {

        when(userServiceMock.getUserWithAuthorities()).thenReturn(orgOwner);
        Organization organization = organizationService.create(testOrganization);
        when(organizationRepositoryMock.findOne(organization.getId())).thenReturn(null);
        organizationService.getUserByOrgId(organization.getId());
    }


    @Test
    public void testFollowingState_TRUE() throws Exception{
        doReturn(defaultUser).when(userServiceMock).getUserWithAuthorities();
        doReturn(testOrganization).when(organizationRepositoryMock).findOrganizationIfUserFollows(defaultUser.getId(), testOrganization.getId());

        Boolean testObject = organizationService.followingState(testOrganization.getId());

        verify(organizationRepositoryMock, times(1)).findOrganizationIfUserFollows(defaultUser.getId(), testOrganization.getId());
        verify(userServiceMock, times(1)).getUserWithAuthorities();

        assertTrue(testObject);
    }

    @Test
    public void testFollowingState_FALSE() throws Exception{
        doReturn(defaultUser).when(userServiceMock).getUserWithAuthorities();
        doReturn(null).when(organizationRepositoryMock).findOrganizationIfUserFollows(defaultUser.getId(), testOrganization.getId());

        Boolean testObject = organizationService.followingState(testOrganization.getId());

        verify(organizationRepositoryMock, times(1)).findOrganizationIfUserFollows(defaultUser.getId(), testOrganization.getId());
        verify(userServiceMock, times(1)).getUserWithAuthorities();
        assertFalse(testObject);
    }

    @Test
    public void testFollow_SUCCESS() throws Exception{
        // set default user as follower of the project
        ArrayList<User> usrList = new ArrayList<>();
        usrList.add(defaultUser);
        testOrganization.setFollowingUsers(usrList);

        //config the results for some checks
        doReturn(defaultUser).when(userServiceMock).getUserWithAuthorities();
        // we need here returns null, else we won't be able to add new follower
        doReturn(null).when(organizationRepositoryMock).findOrganizationIfUserFollows(defaultUser.getId(), testOrganization.getId());
        doReturn(testOrganization).when(organizationRepositoryMock).findOne(testOrganization.getId());
        //ignore
        //doReturn(null).when(userRepositoryMock).save(defaultUser);

        organizationService.follow(testOrganization.getId());

        verify(userServiceMock, times(1)).getUserWithAuthorities();
        verify(organizationRepositoryMock, times(1)).findOrganizationIfUserFollows(defaultUser.getId(), testOrganization.getId());
        verify(organizationRepositoryMock, times(1)).findOne(testOrganization.getId());
    }

    @Test(expected = IllegalValueException.class)
    public void testFollow_FAIL_AlreadyFollows() throws Exception{

        //config the results for some checks
        doReturn(defaultUser).when(userServiceMock).getUserWithAuthorities();
        // we need here returns null, else we won't be able to add new follower
        doReturn(testOrganization).when(organizationRepositoryMock).findOrganizationIfUserFollows(defaultUser.getId(), testOrganization.getId());

        organizationService.follow(testOrganization.getId());
    }

    @Test(expected = IllegalValueException.class)
    public void testFollow_FAIL_OrganizationNotFound() throws Exception{

        //config the results for some checks
        doReturn(defaultUser).when(userServiceMock).getUserWithAuthorities();
        // we need here returns null, else we won't be able to add new follower
        doReturn(null).when(organizationRepositoryMock).findOrganizationIfUserFollows(defaultUser.getId(), testOrganization.getId());
        // this trigger our Exception for Project NULL Value
        doReturn(null).when(organizationRepositoryMock).findOne(testOrganization.getId());
        //ignore
        //doReturn(null).when(userRepositoryMock).save(defaultUser);

        organizationService.follow(testOrganization.getId());
    }

    @Test(expected = IllegalValueException.class)
    public void testFollow_FAIL_OrganizationInactive() throws Exception{

        // we need an inactive project.
        testOrganization.setActive(false);

        //config the results for some checks
        doReturn(defaultUser).when(userServiceMock).getUserWithAuthorities();
        // we need here returns null, else we won't be able to add new follower
        doReturn(null).when(organizationRepositoryMock).findOrganizationIfUserFollows(defaultUser.getId(), testOrganization.getId());
        // this trigger our Exception for Project NULL Value
        doReturn(testOrganization).when(organizationRepositoryMock).findOne(testOrganization.getId());

        organizationService.follow(testOrganization.getId());
    }

    @Test
    public void testUnfollow_SUCCESS() throws Exception {
        // set default user as follower of the project
        ArrayList<User> usrList = new ArrayList<>();
        testOrganization.setFollowingUsers(usrList);

        //config the results for some checks
        doReturn(defaultUser).when(userServiceMock).getUserWithAuthorities();
        // we need here returns null, else we won't be able to add new follower
        doReturn(testOrganization).when(organizationRepositoryMock).findOrganizationIfUserFollows(defaultUser.getId(), testOrganization.getId());
        doReturn(testOrganization).when(organizationRepositoryMock).findOne(testOrganization.getId());
        //ignore
        //doReturn(null).when(userRepositoryMock).save(defaultUser);

        organizationService.unfollow(testOrganization.getId());

        verify(userServiceMock, times(1)).getUserWithAuthorities();
        verify(organizationRepositoryMock, times(1)).findOrganizationIfUserFollows(defaultUser.getId(), testOrganization.getId());
    }

    @Test(expected = IllegalValueException.class)
    public void testUnfollow_FAIL_OrganizationNotFound() throws Exception{

        //config the results for some checks
        doReturn(defaultUser).when(userServiceMock).getUserWithAuthorities();
        // we need here returns null, else we won't be able to add new folldefaultUser.getId(), basicProject.getId());

        organizationService.unfollow(testOrganization.getId());
    }

    @Test(expected = IllegalValueException.class)
    public void testUnfollow_FAIL_OrganizationInactive() throws Exception{
        // we need an inactive project.
        testOrganization.setActive(false);

        //config the results for some checks
        doReturn(defaultUser).when(userServiceMock).getUserWithAuthorities();
        // we need here returns null, else we won't be able to add new follower
        doReturn(testOrganization).when(organizationRepositoryMock).findOrganizationIfUserFollows(defaultUser.getId(), testOrganization.getId());

        organizationService.unfollow(testOrganization.getId());
    }
}
