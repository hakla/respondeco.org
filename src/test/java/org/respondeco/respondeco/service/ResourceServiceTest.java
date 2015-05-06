package org.respondeco.respondeco.service;

import com.mysema.query.types.Predicate;
import org.joda.time.LocalDate;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.respondeco.respondeco.Application;
import org.respondeco.respondeco.domain.*;
import org.respondeco.respondeco.repository.*;
import org.respondeco.respondeco.service.exception.*;
import org.respondeco.respondeco.web.rest.util.RestParameters;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.transaction.annotation.Transactional;
import scala.Console;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.isA;
import static org.mockito.Mockito.*;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Application.class)
@WebAppConfiguration
@Transactional
public class ResourceServiceTest {

    @Mock
    private ResourceOfferRepository resourceOfferRepositoryMock;
    @Mock
    private ResourceRequirementRepository resourceRequirementRepositoryMock;
    @Mock
    private ResourceTagRepository resourceTagRepositoryMock;
    @Mock
    private OrganizationRepository organizationRepositoryMock;
    @Mock
    private ProjectRepository projectRepositoryMock;
    @Mock
    private ImageRepository imageRepositoryMock;
    @Mock
    private UserService userServiceMock;
    @Mock
    private ResourceTagService resourceTagServiceMock;
    @Mock
    private ResourceMatchRepository resourceMatchRepositoryMock;

    private ResourceService resourceService;
    private ResourceOffer expOffer = null;
    private ResourceTag expResourceTag = null;
    private ResourceRequirement expectedReq = new ResourceRequirement();
    private Project expProject;
    private Organization expOrg;

    private User loggedInUser;

    private Long newTagId = 1L;

    private static Class<Long> longCl = Long.class;
    private static Class<String> stringCl = String.class;
    private static Class<ResourceRequirement> reqCl = ResourceRequirement.class;
    private static Class<ResourceOffer> offerCl = ResourceOffer.class;
    private static Class<ResourceTag> tagCl = ResourceTag.class;

    private Long offerId = 1L;
    private Long requirementId = 2L;
    private Long organizationId = 1L;
    private Long projectId = 2L;


    @Before
    public void setup() throws Exception {
        MockitoAnnotations.initMocks(this);
        this.resourceService = new ResourceService(
            resourceOfferRepositoryMock,
            resourceRequirementRepositoryMock,
            resourceTagServiceMock,
            organizationRepositoryMock,
            projectRepositoryMock,
            imageRepositoryMock,
            userServiceMock,
            resourceMatchRepositoryMock
        );

        prepareUser();
    }

    @After
    public void reset() {
        expOffer = null;
        expResourceTag = null;
        expectedReq = null;
        loggedInUser = null;
        expProject = null;
        expOrg = null;
        newTagId = 1L;

        offerId = 1L;
        requirementId = 2L;
        organizationId = 1L;
        projectId = 2L;
    }

    private List<String> prepareCreateOffer() {
        Organization organization = new Organization();
        organization.setId(1L);
        expOffer = new ResourceOffer();
        expOffer.setDescription(" Here is my test Requirement... bla bla. ");
        expOffer.setAmount(new BigDecimal(10));
        expOffer.setOrganization(organization);
        expOffer.setIsCommercial(true);
        expOffer.setStartDate(LocalDate.now());
        expOffer.setId(1L);
        expOffer.setName(" TEST ");
        expResourceTag = new ResourceTag(1L, "test ");
        expResourceTag.setId(1L);
        expOffer.addResourceTag(expResourceTag);
        List<String> tags = new ArrayList<>();
        tags.add(expResourceTag.getName());

        List<ResourceTag> resourceTags = new ArrayList<ResourceTag>();
        List<ResourceOffer> resourceOffers = new ArrayList<ResourceOffer>();

        resourceTags.add(expResourceTag);

        when(resourceOfferRepositoryMock.findByIdAndActiveIsTrue(expOffer.getId())).thenReturn(expOffer);
        when(organizationRepositoryMock.findByIdAndActiveIsTrue(expOffer.getOrganization().getId())).
            thenReturn(expOffer.getOrganization());

        //Step 1 Check if the same ResourceRequirement exists, by Project ID and Description
        when(resourceOfferRepositoryMock.findByNameAndActiveIsTrue(expOffer.getName(), null)).thenReturn(resourceOffers);
        //Assign all variables to new Resource Requirement Objekt and execute Save
        doAnswer(invocation -> {
            Object[] args = invocation.getArguments();
            ResourceOffer result = (ResourceOffer) args[0];
            result.setId(1L);
            Console.out().print(args[0]);
            return result;
        }).when(resourceOfferRepositoryMock).save(isA(offerCl));
        this.prepareTagRepository(tags.get(0), resourceTags);

        return tags;
    }

    private void prepareUser() {
        loggedInUser = new User();
        loggedInUser.setId(1L);
        Set<Authority> authorities = new HashSet<>(1);
        Authority a1 = new Authority();
        a1.setName("ROLE_ADMIN");
        authorities.add(a1);
        Authority a2 = new Authority();
        a2.setName("ROLE_USER");
        authorities.add(a2);
        loggedInUser.setAuthorities(authorities);
        expOrg = new Organization();
        expOrg.setId(1L);
        expOrg.setOwner(loggedInUser);
        expOrg.setActive(true);
        expOrg.setVerified(true);
        loggedInUser.setOrganization(expOrg);
        when(this.userServiceMock.getUserWithAuthorities()).thenReturn(loggedInUser);
        when(this.organizationRepositoryMock.findOne(isA(longCl))).thenReturn(expOrg);
    }

    private void prepareProject(Long projectId) {
        expProject = new Project();
        expProject.setId(projectId);
        expectedReq.setProject(expProject);
        expProject.setOrganization(expOrg);
        when(this.projectRepositoryMock.findOne(expProject.getId())).thenReturn(expProject);
    }

    private List<String> prepareCreateRequirement() {
        this.prepareProject(1L);
        //region Test data
        expectedReq = new ResourceRequirement();
        expectedReq.setDescription(" Here is my test Requirement... bla bla. ");
        expectedReq.setOriginalAmount(new BigDecimal(10));
        expectedReq.setAmount(new BigDecimal(10));
        expectedReq.setProject(expProject);
        expectedReq.setIsEssential(true);
        expectedReq.setId(1L);
        expectedReq.setName(" TEST ");
        expResourceTag = new ResourceTag(1L, "test ");
        expResourceTag.setId(1L);
        expectedReq.addResourceTag(expResourceTag);
        List<String> tags = new ArrayList<>();
        tags.add(expResourceTag.getName());


        List<ResourceTag> resourceTags = new ArrayList<ResourceTag>();
        List<ResourceRequirement> resourceReqs = new ArrayList<ResourceRequirement>();

        resourceTags.add(expResourceTag);
        //endregion

        //Step 1 Check if the same ResourceRequirement exists, by Project ID and Description
        when(resourceRequirementRepositoryMock.findByNameAndProjectAndActiveIsTrue(expectedReq.getName(),
            expectedReq.getProject())).thenReturn(resourceReqs);
        //Assign all variables to new Resource Requirement Objekt and execute Save
        doAnswer(invocation -> {
            Object[] args = invocation.getArguments();
            ResourceRequirement result = (ResourceRequirement) args[0];
            result.setId(1L);
            Console.out().print(args[0]);
            return result;
        }).when(resourceRequirementRepositoryMock).save(isA(reqCl));
        this.prepareTagRepository(tags.get(0), resourceTags);

        return tags;
    }

    private void prepareTagRepository(String tagName, List<ResourceTag> resourceTags) {
        // -> saveResourceTag method
        // Step 3: Return Empty list for our search case
        when(resourceTagRepositoryMock.findByName(tagName)).thenReturn(null);
        when(resourceTagServiceMock.getOrCreateTags(any(List.class))).thenReturn(resourceTags);
        // Step 4: Save Resource Tag
        if (newTagId != null) {
            doAnswer(invocation -> {
                Object[] args = invocation.getArguments();
                ResourceTag tag = (ResourceTag) args[0];
                tag.setId(newTagId);
                Console.out().print(args[0]);
                return tag;
            }).when(resourceTagRepositoryMock).save(isA(tagCl));
        } else {
            when(resourceTagRepositoryMock.save(isA(tagCl))).thenThrow(new IllegalArgumentException());
        }
    }

    @Test
    public void testCreateRequirement_OK() throws Exception {
        this.prepareUser();
        List<String> tags = this.prepareCreateRequirement();

        when(projectRepositoryMock.findByIdAndActiveIsTrue(anyLong())).thenReturn(expectedReq.getProject());

        //save without any tags
        ResourceRequirement actual = this.resourceService
            .createRequirement(expectedReq.getName(), expectedReq.getAmount(),
                expectedReq.getDescription(), expectedReq.getProject().getId(), expectedReq.getIsEssential(), tags);

        assertEquals(expectedReq.getId(), actual.getId());
        assertEquals(expectedReq.getOriginalAmount(), actual.getOriginalAmount());
        assertEquals(expectedReq.getName(), actual.getName());
        assertEquals(expectedReq.getDescription(), actual.getDescription());
        assertEquals(expProject, actual.getProject());
        assertEquals(expProject, expectedReq.getProject());
        assertEquals(expectedReq.getProject(), actual.getProject());
        assertEquals(expectedReq.getProject().getOrganization(), actual.getProject().getOrganization());
        assertEquals(expectedReq.getIsEssential(), actual.getIsEssential());
        assertEquals(expectedReq.getResourceTags().size(), actual.getResourceTags().size());

        for (ResourceTag actTag : expectedReq.getResourceTags()) {
            assertEquals(actTag.getId(), expResourceTag.getId());
            assertEquals(actTag.getName(), expResourceTag.getName());
        }
        //verifying user authorities has been called.
        verify(userServiceMock, times(1)).getUserWithAuthorities();
        verify(organizationRepositoryMock, times(0)).findOne(isA(longCl));
        verify(projectRepositoryMock, times(1)).findByIdAndActiveIsTrue(anyLong());

        verify(resourceRequirementRepositoryMock, times(1)).save(isA(reqCl));
    }

    @Test(expected = NoSuchEntityException.class)
    public void testUpdateRequirement_Fail() throws Exception {
        List<String> tags = this.prepareCreateRequirement();
        //save without any tags
        ResourceRequirement actual = this.resourceService.updateRequirement(expectedReq.getId(),
            expectedReq.getName(), expectedReq.getAmount(), expectedReq.getDescription(), 0L, //just to make the project run
            expectedReq.getIsEssential(), tags);
    }

    @Test
    public void testUpdateRequirement() throws Exception {
        this.prepareUser();
        List<String> tags = this.prepareCreateRequirement();

        when(projectRepositoryMock.findByIdAndActiveIsTrue(anyLong())).thenReturn(expectedReq.getProject());
        when(resourceRequirementRepositoryMock.findByIdAndActiveIsTrue(expectedReq.getId())).thenReturn(expectedReq);
        //save without any tags
        ResourceRequirement actual = this.resourceService.updateRequirement(expectedReq.getId(),
            expectedReq.getName(), expectedReq.getAmount(), expectedReq.getDescription(), expectedReq.getProject().getId(), //just to make the project run
            expectedReq.getIsEssential(), tags);

        assertEquals(expectedReq.getId(), actual.getId());
        assertEquals(expectedReq.getAmount(), actual.getAmount());
        assertEquals(expectedReq.getName(), actual.getName());
        assertEquals(expectedReq.getDescription(), actual.getDescription());
        assertEquals(expectedReq.getProject(), actual.getProject());
        assertEquals(expectedReq.getIsEssential(), actual.getIsEssential());
        assertEquals(expectedReq.getResourceTags().size(), actual.getResourceTags().size());

        for (ResourceTag actTag : expectedReq.getResourceTags()) {
            assertEquals(actTag.getId(), expResourceTag.getId());
            assertEquals(actTag.getName(), expResourceTag.getName());
        }
        //verifying user authorities has been called.
        verify(userServiceMock, times(1)).getUserWithAuthorities();
        verify(projectRepositoryMock, times(1)).findByIdAndActiveIsTrue(anyLong());

        verify(resourceRequirementRepositoryMock, times(1)).save(isA(reqCl));
    }

    @Test
    public void testDeleteRequirement() throws Exception {
        this.prepareUser();
        List<String> tags = this.prepareCreateRequirement();

        when(resourceRequirementRepositoryMock.findByIdAndActiveIsTrue(expectedReq.getId())).thenReturn(expectedReq);
        this.resourceService.deleteRequirement(expectedReq.getId());

        verify(resourceRequirementRepositoryMock, times(1)).findByIdAndActiveIsTrue(isA(longCl));
    }

    @Test
    public void testGetAllRequirements_2Entries() throws Exception {
        expProject = new Project();
        expProject.setId(1L);
        doAnswer(invo -> {
            List<ResourceRequirement> items = new ArrayList<ResourceRequirement>(2);
            ResourceRequirement item1 = new ResourceRequirement();
            item1.setId(1L);
            item1.setProject(expProject);
            items.add(item1);
            ResourceRequirement item2 = new ResourceRequirement();
            item2.setId(10L);
            item2.setProject(expProject);
            items.add(item2);
            return items;
        }).when(resourceRequirementRepositoryMock).findByActiveIsTrue();
        Long expected = 1L;
        int listSize = 2;
        List<ResourceRequirement> items = this.resourceService.getRequirements();
        assertEquals(items.size(), listSize);
        for (int i = 0; i < items.size(); i++) {
            ResourceRequirement current = items.get(i);
            assertEquals(current.getId(), expected);
            assertEquals(current.getProject().getId(), expProject.getId());
            expected += 9L;
        }

        verify(this.resourceRequirementRepositoryMock, times(1)).findByActiveIsTrue();
    }

    @Test
    public void testGetAllRequirements_1EntryForProject2() throws Exception {
        final Long projectID = 2L;
        final Long expectedRequirementID = 10L;
        final Project project1 = new Project();
        project1.setId(1L);
        final Project project2 = new Project();
        project2.setId(2L);

        final List<ResourceRequirement> items = new ArrayList<ResourceRequirement>();
        ResourceRequirement item1 = new ResourceRequirement();
        item1.setId(1L);
        item1.setProject(project1);
        items.add(item1);
        ResourceRequirement item2 = new ResourceRequirement();
        item2.setProject(project2);
        item2.setId(expectedRequirementID);
        items.add(item2);

        doAnswer(invo -> {
            List<ResourceRequirement> internalItems = new ArrayList<ResourceRequirement>();
            for (ResourceRequirement item : items) {
                if (item.getProject() == project2) {
                    internalItems.add(item);
                }
            }
            return internalItems;
        }).when(resourceRequirementRepositoryMock).findByProjectIdAndActiveIsTrue(isA(longCl));
        int listSize = 1;
        List<ResourceRequirement> expectedItem = this.resourceService.getRequirementsForProject(projectID);
        assertEquals(expectedItem.size(), listSize);
        assertEquals(expectedItem.get(0).getProject().getId(), projectID);
        assertEquals(expectedItem.get(0).getId(), expectedRequirementID);
        verify(this.resourceRequirementRepositoryMock, times(1)).findByProjectIdAndActiveIsTrue(isA(longCl));
    }

    @Test
    public void testCreateOffer_OK() throws Exception {
        this.prepareUser();
        List<String> tags = this.prepareCreateOffer();

        expOffer.getOrganization().setVerified(true);
        when(organizationRepositoryMock.findByIdAndActiveIsTrue(expOffer.getOrganization().getId())).
            thenReturn(expOffer.getOrganization());

        //save without any tags
        ResourceOffer actual = this.resourceService.createOffer(expOffer.getName(), expOffer.getAmount(),
            expOffer.getDescription(), expOffer.getOrganization().getId(), expOffer.getIsCommercial(),
            expOffer.getStartDate(), expOffer.getEndDate(), tags, null, expOffer.getPrice());

        assertEquals(expOffer.getId(), actual.getId());
        assertEquals(expOffer.getAmount(), actual.getAmount());
        assertEquals(expOffer.getName(), actual.getName());
        assertEquals(expOffer.getDescription(), actual.getDescription());
        assertEquals(expOffer.getOrganization(), actual.getOrganization());
        assertEquals(expOffer.getIsCommercial(), actual.getIsCommercial());
        assertEquals(expOffer.getStartDate(), actual.getStartDate());
        assertEquals(expOffer.getEndDate(), actual.getEndDate());
        assertEquals(expOffer.getResourceTags().size(), actual.getResourceTags().size());

        for (ResourceTag actTag : expOffer.getResourceTags()) {
            assertEquals(actTag.getId(), expResourceTag.getId());
            assertEquals(actTag.getName(), expResourceTag.getName());
        }

        verify(resourceOfferRepositoryMock, times(1)).save(isA(offerCl));
        verify(organizationRepositoryMock, times(1)).findByIdAndActiveIsTrue(expOffer.getOrganization().getId());
    }

    @Test(expected = ResourceNotFoundException.class)
    public void testUpdateOffer_Fail() throws Exception {
        this.prepareUser();
        List<String> tags = this.prepareCreateOffer();
        this.resourceService.updateOffer(expOffer.getId(), expOffer.getOrganization().getId(), expOffer.getName(),
            expOffer.getAmount(), expOffer.getDescription(), expOffer.getIsCommercial(), expOffer.getStartDate(),
            expOffer.getEndDate(), tags, null, expOffer.getPrice());
    }

    /**
     * Test Update Offer.
     *
     * @throws Exception some unexpected exceptions
     */
    @Test
    public void testUpdateOffer() throws Exception {
        this.prepareUser();
        List<String> tags = this.prepareCreateOffer();
        expOffer.getOrganization().setOwner(loggedInUser);

        ResourceOffer actual = this.resourceService.updateOffer(expOffer.getId(), expOffer.getOrganization().getId(),
            expOffer.getName(), expOffer.getAmount(), expOffer.getDescription(), expOffer.getIsCommercial(),
            expOffer.getStartDate(), expOffer.getEndDate(), tags, null, expOffer.getPrice());

        assertEquals(expOffer.getId(), actual.getId());
        assertEquals(expOffer.getAmount(), actual.getAmount());
        assertEquals(expOffer.getName(), actual.getName());
        assertEquals(expOffer.getDescription(), actual.getDescription());
        assertEquals(expOffer.getOrganization(), actual.getOrganization());
        assertEquals(expOffer.getIsCommercial(), actual.getIsCommercial());
        assertEquals(expOffer.getStartDate(), actual.getStartDate());
        assertEquals(expOffer.getEndDate(), actual.getEndDate());
        assertEquals(expOffer.getResourceTags().size(), actual.getResourceTags().size());

        for (ResourceTag actTag : expOffer.getResourceTags()) {
            assertEquals(actTag.getId(), expResourceTag.getId());
            assertEquals(actTag.getName(), expResourceTag.getName());
        }

        verify(resourceOfferRepositoryMock, times(1)).save(isA(offerCl));
        verify(resourceOfferRepositoryMock, times(1)).findByIdAndActiveIsTrue(expOffer.getId());
        verify(organizationRepositoryMock, times(1)).findByIdAndActiveIsTrue(expOffer.getOrganization().getId());
        //No tags has been added
        verify(resourceTagServiceMock, times(1)).getOrCreateTags(any());
    }

    @Test
    public void testDeleteOffer() throws Exception {
        this.prepareUser();
        List<String> tags = this.prepareCreateOffer();
        when(resourceOfferRepositoryMock.findByIdAndActiveIsTrue(expOffer.getId())).thenReturn(expOffer);
        this.resourceService.deleteOffer(expOffer.getId());

        verify(resourceOfferRepositoryMock, times(1)).findByIdAndActiveIsTrue(isA(longCl));
    }

    @Test
    public void testGetAllOffers_2Entries() throws Exception {
        expOrg = new Organization();
        expOrg.setId(1L);
        doAnswer(invo -> {
            List<ResourceOffer> items = new ArrayList<ResourceOffer>(2);
            ResourceOffer item1 = new ResourceOffer();
            item1.setId(1L);
            item1.setOrganization(expOrg);
            items.add(item1);
            ResourceOffer item2 = new ResourceOffer();
            item2.setId(10L);
            item2.setOrganization(expOrg);
            items.add(item2);
            return items;
        }).when(resourceOfferRepositoryMock).findByOrganizationIdAndActiveIsTrue(expOrg.getId());
        Long expected = 1L;
        int listSize = 2;
        List<ResourceOffer> list = this.resourceService.getOffersForOrganization(expOrg.getId());
        assertEquals(listSize, list.size());
        for (int i = 0; i < list.size(); i++) {
            ResourceOffer current = list.get(i);
            assertEquals(current.getId(), expected);
            assertEquals(current.getOrganization().getId(), expOrg.getId());
            expected += 9L;
        }


        Organization alternative = new Organization();
        alternative.setId(99L);
        expected = 3L;
        doAnswer(inv -> {
            List<ResourceOffer> items = new ArrayList<ResourceOffer>(2);
            ResourceOffer item1 = new ResourceOffer();
            item1.setId(3L);
            item1.setOrganization(alternative);
            items.add(item1);
            return items;
        }).when(resourceOfferRepositoryMock).findByOrganizationIdAndActiveIsTrue(isA(longCl));
        listSize = 1;
        list = this.resourceService.getOffersForOrganization(99L);

        assertEquals(list.size(), listSize);
        for (int i = 0; i < list.size(); i++) {
            ResourceOffer current = list.get(i);
            assertEquals(current.getId(), expected);
            assertEquals(current.getOrganization().getId(), alternative.getId());
        }

        verify(this.resourceOfferRepositoryMock, times(2)).findByOrganizationIdAndActiveIsTrue(isA(longCl));
    }

    /**
     * Prepare for Project Apply Tests
     *
     * @param offer,   that become our donation
     * @param req,     that need our donation
     * @param org,     organisation that apply the offer (donation)
     * @param project, that need our offer (donation)
     */
    private void prepareProjectApply(ResourceOffer offer, ResourceRequirement req, Organization org, Project project) {
        BigDecimal offerAmount = new BigDecimal(18);
        final BigDecimal requirementAmount = offerAmount.divide(new BigDecimal(2));

        // create offer mock
        offer.setId(offerId);
        offer.setAmount(offerAmount);

        // create requirement mock
        req.setId(requirementId);
        req.setAmount(requirementAmount);

        // create organization mock with user that own the organization
        org.setId(organizationId);
        User orgUser = new User();
        orgUser.setId(1L);
        org.setOwner(orgUser);

        // at least our project mock
        project.setId(projectId);
        User projectOwner = new User();
        projectOwner.setId(2L);
        Organization projectOrg = new Organization();
        projectOrg.setId(2L);
        projectOrg.setOwner(projectOwner);
        project.setOrganization(projectOrg);


        // mocks that defines our result
        when(resourceOfferRepositoryMock.findByIdAndActiveIsTrue(offerId)).thenReturn(offer);
        when(resourceRequirementRepositoryMock.findByIdAndActiveIsTrue(requirementId)).thenReturn(req);
        when(organizationRepositoryMock.findByIdAndActiveIsTrue(organizationId)).thenReturn(org);
        when(projectRepositoryMock.findByIdAndActiveIsTrue(projectId)).thenReturn(project);
        when(userServiceMock.getUserWithAuthorities()).thenReturn(orgUser);


    }

    /**
     * Test for successful project apply.
     *
     * @throws Exception should not thrown anything
     */
    @Test
    public void testProjectApply_SUCCESS_OfferHigherThanReq() throws Exception {
        ResourceOffer offer = new ResourceOffer();
        ResourceRequirement req = new ResourceRequirement();
        Organization org = new Organization();
        Project project = new Project();

        prepareProjectApply(offer, req, org, project);

        ResourceMatch expected = new ResourceMatch();
        expected.setId(1L);
        expected.setAmount(req.getAmount());

        doAnswer(invocation -> {
            Object[] args = invocation.getArguments();
            ResourceMatch result = (ResourceMatch) args[0];
            result.setId(expected.getId());
            return result;
        }).when(resourceMatchRepositoryMock).save(isA(ResourceMatch.class));
        when(resourceMatchRepositoryMock.findByResourceOfferAndResourceRequirementAndOrganizationAndProjectAndActiveIsTrue(
            isA(ResourceOffer.class), isA(ResourceRequirement.class), isA(Organization.class), isA((Project.class)))).
            thenReturn(new ArrayList<>());

        ResourceMatch actual = resourceService.createProjectApplyOffer(offerId, requirementId, organizationId, projectId);
        assertEquals(expected.getId(), actual.getId());
        assertEquals(expected.getAmount(), actual.getAmount());

        verify(resourceOfferRepositoryMock, times(1)).findByIdAndActiveIsTrue(offerId);
        verify(resourceRequirementRepositoryMock, times(1)).findByIdAndActiveIsTrue(requirementId);
        verify(organizationRepositoryMock, times(1)).findByIdAndActiveIsTrue(organizationId);
        verify(projectRepositoryMock, times(1)).findByIdAndActiveIsTrue(projectId);
        verify(userServiceMock, times(1)).getUserWithAuthorities();
    }

    /**
     * Test for successful project apply.
     *
     * @throws Exception should not thrown anything
     */
    @Test
    public void testProjectApply_SUCCESS_OfferLowerThanReq() throws Exception {
        ResourceOffer offer = new ResourceOffer();
        ResourceRequirement req = new ResourceRequirement();
        Organization org = new Organization();
        Project project = new Project();

        prepareProjectApply(offer, req, org, project);
        BigDecimal reqAmount = req.getAmount();
        req.setAmount(offer.getAmount());
        offer.setAmount(reqAmount);

        ResourceMatch expected = new ResourceMatch();
        expected.setId(1L);
        expected.setAmount(offer.getAmount());

        doAnswer(invocation -> {
            Object[] args = invocation.getArguments();
            ResourceMatch result = (ResourceMatch) args[0];
            result.setId(expected.getId());
            return result;
        }).when(resourceMatchRepositoryMock).save(isA(ResourceMatch.class));
        when(resourceMatchRepositoryMock.findByResourceOfferAndResourceRequirementAndOrganizationAndProjectAndActiveIsTrue(
            isA(ResourceOffer.class), isA(ResourceRequirement.class), isA(Organization.class), isA((Project.class)))).
            thenReturn(new ArrayList<>());

        ResourceMatch actual = resourceService.createProjectApplyOffer(offerId, requirementId, organizationId, projectId);
        assertEquals(expected.getId(), actual.getId());
        assertEquals(expected.getAmount(), actual.getAmount());

        verify(resourceOfferRepositoryMock, times(1)).findByIdAndActiveIsTrue(offerId);
        verify(resourceRequirementRepositoryMock, times(1)).findByIdAndActiveIsTrue(requirementId);
        verify(organizationRepositoryMock, times(1)).findByIdAndActiveIsTrue(organizationId);
        verify(projectRepositoryMock, times(1)).findByIdAndActiveIsTrue(projectId);
        verify(userServiceMock, times(1)).getUserWithAuthorities();
    }

    @Test(expected = IllegalValueException.class)
    public void testProjectApply_FAIL_ApplyToOwnProject() throws Exception {

        ResourceOffer offer = new ResourceOffer();
        ResourceRequirement req = new ResourceRequirement();
        Organization org = new Organization();
        Project project = new Project();

        prepareProjectApply(offer, req, org, project);
        // this will cause an error
        project.setOrganization(org);
        when(resourceMatchRepositoryMock.findByResourceOfferAndResourceRequirementAndOrganizationAndProjectAndActiveIsTrue(
            isA(ResourceOffer.class), isA(ResourceRequirement.class), isA(Organization.class), isA((Project.class)))).
            thenReturn(new ArrayList<>());

        resourceService.createProjectApplyOffer(offerId, requirementId, organizationId, projectId);
    }

    @Test(expected = IllegalValueException.class)
    public void testProjectApply_FAIL_RequirementIsDepleted() throws Exception {

        ResourceOffer offer = new ResourceOffer();
        ResourceRequirement req = new ResourceRequirement();
        Organization org = new Organization();
        Project project = new Project();

        prepareProjectApply(offer, req, org, project);
        when(resourceMatchRepositoryMock.findByResourceOfferAndResourceRequirementAndOrganizationAndProjectAndActiveIsTrue(
            isA(ResourceOffer.class), isA(ResourceRequirement.class), isA(Organization.class), isA((Project.class)))).
            thenReturn(new ArrayList<>());
        // this will cause an error
        req.setAmount(BigDecimal.ZERO);

        resourceService.createProjectApplyOffer(offerId, requirementId, organizationId, projectId);
    }

    @Test(expected = IllegalValueException.class)
    public void testProjectApply_FAIL_OfferIsDepleted() throws Exception {

        ResourceOffer offer = new ResourceOffer();
        ResourceRequirement req = new ResourceRequirement();
        Organization org = new Organization();
        Project project = new Project();

        prepareProjectApply(offer, req, org, project);
        when(resourceMatchRepositoryMock.findByResourceOfferAndResourceRequirementAndOrganizationAndProjectAndActiveIsTrue(
            isA(ResourceOffer.class), isA(ResourceRequirement.class), isA(Organization.class), isA((Project.class)))).
            thenReturn(new ArrayList<>());
        // this will cause an error
        offer.setAmount(BigDecimal.ZERO);

        resourceService.createProjectApplyOffer(offerId, requirementId, organizationId, projectId);
    }

    @Test(expected = IllegalValueException.class)
    public void testProjectApply_FAIL_OfferAlreadyEntered() throws Exception {

        ResourceOffer offer = new ResourceOffer();
        ResourceRequirement req = new ResourceRequirement();
        Organization org = new Organization();
        Project project = new Project();

        prepareProjectApply(offer, req, org, project);

        // this will cause an error
        List<ResourceMatch> a = new ArrayList<ResourceMatch>();
        a.add(new ResourceMatch());
        when(resourceMatchRepositoryMock.findByResourceOfferAndResourceRequirementAndOrganizationAndProjectAndActiveIsTrue(
            isA(ResourceOffer.class), isA(ResourceRequirement.class), isA(Organization.class), isA((Project.class)))).
            thenReturn(a);

        resourceService.createProjectApplyOffer(offerId, requirementId, organizationId, projectId);
    }


    @Test
    public void testCreateClaimResourceRequest_shouldCreateMatch() throws Exception {

        doAnswer(invocation -> {
            Object[] args = invocation.getArguments();

            ResourceOffer offer = new ResourceOffer();
            Organization organization = new Organization();
            organization.setId(3L);
            offer.setId((Long) args[0]);
            offer.setOrganization(organization);

            return offer;
        }).when(resourceOfferRepositoryMock).findByIdAndActiveIsTrue(anyLong());

        doAnswer(invocation -> {
            Object[] args = invocation.getArguments();

            ResourceRequirement requirement = new ResourceRequirement();
            requirement.setId((Long) args[0]);
            Project project = new Project();
            Organization organization = new Organization();
            project.setId(4L);
            organization.setId(5L);
            project.setOrganization(organization);
            requirement.setProject(project);

            return requirement;
        }).when(resourceRequirementRepositoryMock).findByIdAndActiveIsTrue(anyLong());

        doAnswer(invocation -> {
            return new ArrayList<ResourceMatch>();
        }).when(resourceMatchRepositoryMock).findByResourceOfferAndResourceRequirementAndOrganizationAndProjectAndActiveIsTrue(any(), any(), any(), any());

        ResourceMatch resourceMatch = resourceService.createClaimResourceRequest(1L, 2L);

        verify(resourceOfferRepositoryMock, times(1)).findByIdAndActiveIsTrue(1L);
        verify(resourceRequirementRepositoryMock, times(1)).findByIdAndActiveIsTrue(2L);
        verify(resourceMatchRepositoryMock, times(1)).findByResourceOfferAndResourceRequirementAndOrganizationAndProjectAndActiveIsTrue(any(), any(), any(), any());

        assertTrue(resourceMatch.getResourceOffer().getId().equals(1L));
        assertTrue(resourceMatch.getResourceRequirement().getId().equals(2L));
        assertTrue(resourceMatch.getOrganization().getId().equals(3L));
        assertTrue(resourceMatch.getProject().getId().equals(4L));
    }

    @Test(expected = IllegalValueException.class)
    public void testCreateClaimResourceRequest_shouldThrowExceptionBecauseResourceOfferIsNull() throws Exception {
        doAnswer(invocation -> {
            return null;
        }).when(resourceOfferRepositoryMock).findOne(anyLong());

        resourceService.createClaimResourceRequest(1L, 2L);
    }

    @Test(expected = IllegalValueException.class)
    public void testCreateClaimResourceRequest_shouldThrowExceptionBecauseResourceRequirementIsNull() throws Exception {
        doAnswer(invocation -> {
            return new ResourceOffer();
        }).when(resourceOfferRepositoryMock).findOne(anyLong());
        doAnswer(invocation -> {
            return null;
        }).when(resourceRequirementRepositoryMock).findOne(anyLong());

        resourceService.createClaimResourceRequest(1L, 2L);
    }

    @Test(expected = IllegalValueException.class)
    public void testCreateClaimResourceRequest_shouldThrowExceptionBecauseProjectIsNull() throws Exception {
        doAnswer(invocation -> {
            ResourceOffer resourceOffer = new ResourceOffer();
            resourceOffer.setOrganization(new Organization());
            return resourceOffer;
        }).when(resourceOfferRepositoryMock).findOne(anyLong());
        doAnswer(invocation -> {
            ResourceRequirement requirement = new ResourceRequirement();
            return requirement;
        }).when(resourceRequirementRepositoryMock).findOne(anyLong());

        resourceService.createClaimResourceRequest(1L, 2L);
    }

    @Test(expected = IllegalValueException.class)
    public void testCreateClaimResourceRequest_shouldThrowExceptionBecauseOrganizationIsNull() throws Exception {
        doAnswer(invocation -> {
            ResourceOffer resourceOffer = new ResourceOffer();
            return resourceOffer;
        }).when(resourceOfferRepositoryMock).findOne(anyLong());
        doAnswer(invocation -> {
            ResourceRequirement requirement = new ResourceRequirement();
            requirement.setProject(new Project());
            return requirement;
        }).when(resourceRequirementRepositoryMock).findOne(anyLong());

        resourceService.createClaimResourceRequest(1L, 2L);
    }

    @Test(expected = MatchAlreadyExistsException.class)
    public void testCreateClaimResourceRequest_shouldThrowExceptionBecauseMatchAlreadyExists() throws Exception {
        doAnswer(invocation -> {
            Object[] args = invocation.getArguments();

            ResourceOffer offer = new ResourceOffer();
            Organization organization = new Organization();
            organization.setId((Long) args[0]);
            offer.setId((Long) args[0]);
            offer.setOrganization(organization);

            return offer;
        }).when(resourceOfferRepositoryMock).findByIdAndActiveIsTrue(anyLong());

        doAnswer(invocation -> {
            Object[] args = invocation.getArguments();

            ResourceRequirement requirement = new ResourceRequirement();
            requirement.setId((Long) args[0]);
            Project project = new Project();
            Organization organization = new Organization();
            project.setId(4L);
            organization.setId(5L);
            project.setOrganization(organization);
            requirement.setProject(project);

            return requirement;
        }).when(resourceRequirementRepositoryMock).findByIdAndActiveIsTrue(anyLong());

        doAnswer(invocation -> {
            List<ResourceMatch> matches = new ArrayList<ResourceMatch>();
            matches.add(new ResourceMatch());

            return matches;
        }).when(resourceMatchRepositoryMock).
            findByResourceOfferAndResourceRequirementAndOrganizationAndProjectAndActiveIsTrue(any(ResourceOffer.class),
                any(ResourceRequirement.class), any(Organization.class), any(Project.class));

        resourceService.createClaimResourceRequest(1L, 2L);

        verify(resourceOfferRepositoryMock, times(1)).findByIdAndActiveIsTrue(anyLong());
        verify(resourceRequirementRepositoryMock, times(1)).findByIdAndActiveIsTrue(anyLong());
        verify(resourceMatchRepositoryMock, times(1)).
            findByResourceOfferAndResourceRequirementAndOrganizationAndProjectAndActiveIsTrue(any(ResourceOffer.class),
                any(ResourceRequirement.class), any(Organization.class), any(Project.class));
    }


    /**
     * Creates a new ResourceMatch Object - Helper for ResourceRequest tests
     *
     * @param amountOffer       amount of ResourceOffer
     * @param amountRequirement amount of ResourceRequirement
     * @return ResourceMatch
     */
    private ResourceMatch prepareResourceMatch(int amountOffer, int amountRequirement) {
        ResourceMatch resourceMatch = new ResourceMatch();
        resourceMatch.setId(1L);
        resourceMatch.setMatchDirection(MatchDirection.ORGANIZATION_CLAIMED);

        ResourceOffer resourceOffer = new ResourceOffer();
        resourceOffer.setAmount(new BigDecimal(amountOffer));

        ResourceRequirement resourceRequirement = new ResourceRequirement();
        resourceRequirement.setAmount(new BigDecimal(amountRequirement));

        resourceMatch.setResourceOffer(resourceOffer);
        resourceMatch.setResourceRequirement(resourceRequirement);

        Organization org = new Organization();
        org.setId(1L);
        org.setOwner(loggedInUser);

        Project project = new Project();
        project.setOrganization(org);
        resourceMatch.setProject(project);
        resourceMatch.setOrganization(org);

        return resourceMatch;
    }

    @Test
    public void testAnswerResourceRequest_requestAcceptedOfferGreaterRequestAmount() throws Exception {

        doAnswer(invocation -> {
            return prepareResourceMatch(10, 5);
        }).when(resourceMatchRepositoryMock).findOne(anyLong());

        doAnswer(invocation -> {
            Object args[] = invocation.getArguments();
            return (ResourceOffer) args[0];
        }).when(resourceOfferRepositoryMock).save(any(ResourceOffer.class));

        doAnswer(invocation -> {
            Object args[] = invocation.getArguments();
            return (ResourceMatch) args[0];
        }).when(resourceMatchRepositoryMock).save(any(ResourceMatch.class));

        doReturn(loggedInUser).when(userServiceMock).getUserWithAuthorities();

        ResourceMatch resourceMatch = resourceService.answerResourceRequest(1L, true);

        verify(resourceMatchRepositoryMock, times(1)).findOne(anyLong());
        verify(resourceOfferRepositoryMock, times(1)).save(any(ResourceOffer.class));
        verify(resourceMatchRepositoryMock, times(1)).save(any(ResourceMatch.class));

        assertEquals(resourceMatch.getAccepted(), true);
        assertEquals(resourceMatch.getAmount(), new BigDecimal(5));
        assertEquals(resourceMatch.getResourceOffer().getAmount(), new BigDecimal(5));
        assertEquals(resourceMatch.getResourceOffer().isActive(), true);
    }

    @Test
    public void testAnswerResourceRequest_requestAcceptedOfferSmallerRequestAmount() throws Exception {
        doAnswer(invocation -> {
            return prepareResourceMatch(5, 9);
        }).when(resourceMatchRepositoryMock).findOne(anyLong());

        doAnswer(invocation -> {
            Object args[] = invocation.getArguments();
            return (ResourceOffer) args[0];
        }).when(resourceOfferRepositoryMock).save(any(ResourceOffer.class));

        doAnswer(invocation -> {
            Object args[] = invocation.getArguments();
            return (ResourceMatch) args[0];
        }).when(resourceMatchRepositoryMock).save(any(ResourceMatch.class));

        doReturn(loggedInUser).when(userServiceMock).getUserWithAuthorities();

        ResourceMatch resourceMatch = resourceService.answerResourceRequest(1L, true);

        verify(resourceMatchRepositoryMock, times(1)).findOne(anyLong());
        verify(resourceOfferRepositoryMock, times(1)).save(any(ResourceOffer.class));
        verify(resourceMatchRepositoryMock, times(1)).save(any(ResourceMatch.class));

        assertEquals(resourceMatch.getAccepted(), true);
        assertEquals(resourceMatch.getAmount(), new BigDecimal(5));
        assertEquals(resourceMatch.getResourceRequirement().getAmount(), new BigDecimal(4));
        assertEquals(resourceMatch.getResourceOffer().isActive(), false);
    }

    @Test(expected = IllegalValueException.class)
    public void testAnswerResourceRequest_shouldThrowExceptionBecauseMatchIdIsNull() throws Exception {
        resourceService.answerResourceRequest(null, true);
    }

    @Test(expected = NoSuchEntityException.class)
    public void testAnswerResourceRequest_shouldThrowExceptionBecauseMatchIsNull() throws Exception {
        doAnswer(invocation -> {
            return null;
        }).when(resourceMatchRepositoryMock).findOne(anyLong());

        resourceService.answerResourceRequest(1L, true);
    }

    @Test(expected = OperationForbiddenException.class)
    public void testAnswerResourceRequest_shouldThrowExceptionBecauseNoUserIsLoggedIn() throws Exception {
        doAnswer(invocation -> {
            return prepareResourceMatch(5, 9);
        }).when(resourceMatchRepositoryMock).findOne(anyLong());

        doReturn(null).when(userServiceMock).getUserWithAuthorities();

        resourceService.answerResourceRequest(1L, true);
    }

    @Test(expected = OperationForbiddenException.class)
    public void testAnswerResourceRequest_shouldThrowExceptionBecauseUserHasNoOrganization() throws Exception {
        doAnswer(invocation -> {
            return prepareResourceMatch(5, 9);
        }).when(resourceMatchRepositoryMock).findOne(anyLong());

        User user = new User();

        doReturn(user).when(userServiceMock).getUserWithAuthorities();

        resourceService.answerResourceRequest(1L, true);
    }

    @Test(expected = OperationForbiddenException.class)
    public void testAnswerResourceRequest_shouldThrowExceptionBecauseUserHasNoAuthorization() throws Exception {
        doAnswer(invocation -> {
            return prepareResourceMatch(5, 9);
        }).when(resourceMatchRepositoryMock).findOne(anyLong());

        User user = new User();
        user.setId(2L);
        Organization org = new Organization();
        org.setOwner(user);
        user.setOrganization(org);

        doReturn(user).when(userServiceMock).getUserWithAuthorities();

        resourceService.answerResourceRequest(1L, true);
    }

    @Test
    public void testAnswerResourceRequest_shouldDeclineTheResourceRequest() throws Exception {
        doAnswer(invocation -> {
            return prepareResourceMatch(5, 5);
        }).when(resourceMatchRepositoryMock).findOne(anyLong());

        doAnswer(invocation -> {
            Object args[] = invocation.getArguments();
            return (ResourceMatch) args[0];
        }).when(resourceMatchRepositoryMock).save(any(ResourceMatch.class));

        doReturn(loggedInUser).when(userServiceMock).getUserWithAuthorities();

        ResourceMatch match = resourceService.answerResourceRequest(1L, false);

        verify(resourceMatchRepositoryMock, times(1)).findOne(anyLong());
        verify(resourceMatchRepositoryMock, times(1)).save(any(ResourceMatch.class));

        assertEquals(match.getResourceOffer().getAmount(), new BigDecimal(5));
        assertEquals(match.getAccepted(), false);
    }

    @Test
    public void testGetResourceRequestsForOrganization_shouldReturnResourceMatches() throws Exception {
        doAnswer(invocation -> {
            List<ResourceMatch> matches = new ArrayList<>();
            ResourceMatch match = new ResourceMatch();
            Organization org = new Organization();
            org.setId(1L);
            match.setOrganization(org);

            matches.add(match);

            Page page = new PageImpl(matches);

            return page;
        }).when(resourceMatchRepositoryMock).findAll(any(Predicate.class), any(PageRequest.class));

        List<ResourceMatch> resourceMatches = resourceService.getResourcesForOrganization(1L, new PageRequest(1, 20));
        ResourceMatch match = resourceMatches.get(0);

        verify(resourceMatchRepositoryMock, times(2)).findAll(any(Predicate.class), any(PageRequest.class));

        assertTrue(match.getOrganization().getId().equals(1L));
    }

    @Test
    public void testGetAllOffers_shouldReturnFindByActiveIsTrue() throws Exception {
        doAnswer(invocation -> {
            List<ResourceOffer> resourceOffers = new ArrayList<>();
            ResourceOffer offer = new ResourceOffer();
            offer.setId(1L);
            offer.setName("resource");
            resourceOffers.add(offer);
            offer.setId(2L);
            offer.setName("resource2");
            resourceOffers.add(offer);

            Page page = new PageImpl(resourceOffers);

            return page;
        }).when(resourceOfferRepositoryMock).findByActiveIsTrue(any(PageRequest.class));

        User user = new User();
        user.setId(2L);

        doReturn(user).when(userServiceMock).getUserWithAuthorities();

        Page<ResourceOffer> page = resourceService.getOffers("", null, new PageRequest(0, 5));
        assertEquals(page.getTotalPages(), 1);
        assertEquals(page.getTotalElements(), 2);
        assertEquals(page.getContent().size(), 2);
    }

    @Test
    public void testGetAllOffers_shouldReturnAllResourcesWithFilter() throws Exception {
        doAnswer(invocation -> {
            List<ResourceOffer> resourceOffers = new ArrayList<>();
            ResourceOffer offer = new ResourceOffer();
            offer.setId(1L);
            offer.setName("resource");
            resourceOffers.add(offer);
            offer.setId(2L);
            offer.setName("resource2");
            resourceOffers.add(offer);

            Page page = new PageImpl(resourceOffers);

            return page;
        }).when(resourceOfferRepositoryMock).findAll(any(Predicate.class), any(PageRequest.class));

        Page<ResourceOffer> page = resourceService.getOffers("resource", true, new PageRequest(0, 5));
        assertEquals(page.getTotalPages(), 1);
        assertEquals(page.getTotalElements(), 2);
        assertEquals(page.getContent().size(), 2);
    }

    @Test
    public void testGetDonatedResourcesForOrganization() throws Exception {
        doReturn(expOrg).when(organizationRepositoryMock).findOne(anyLong());

        List<ResourceMatch> resourceMatches = new ArrayList<>();
        ResourceMatch resourceMatch = new ResourceMatch();
        resourceMatch.setOrganization(expOrg);
        resourceMatch.setProject(expProject);
        resourceMatch.setId(1L);

        resourceMatches.add(resourceMatch);

        Page<ResourceMatch> resourceMatchPage = new PageImpl<>(resourceMatches);

        doReturn(resourceMatchPage).when(resourceMatchRepositoryMock).findByOrganizationAndAcceptedIsTrueAndActiveIsTrue(any(Organization.class), any(Pageable.class));

        Page<ResourceMatch> page = resourceService.getDonatedResourcesForOrganization(1L, new RestParameters(0, 20));

        assertEquals(page.getTotalElements(), 1L);
        assertEquals(page.getContent().get(0).getId().longValue(), 1L);
    }


}
