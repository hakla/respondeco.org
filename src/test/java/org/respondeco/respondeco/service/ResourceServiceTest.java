package org.respondeco.respondeco.service;

import net.sf.ehcache.util.SetAsList;
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
import org.respondeco.respondeco.web.rest.dto.ResourceOfferDTO;
import org.respondeco.respondeco.web.rest.dto.ResourceRequirementRequestDTO;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.transaction.annotation.Transactional;
import scala.Console;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.isA;
import static org.mockito.Mockito.*;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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
    private OrganizationRepository organizationRepository;
    @Mock
    private ProjectRepository projectRepository;
    @Mock
    private ImageRepository imageRepository;
    @Mock
    private UserService userService;
    @Mock
    private ResourceTagService resourceTagServiceMock;
    @Mock
    private ResourceMatchRepository resourceMatchRepository;

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
            organizationRepository,
            projectRepository,
            imageRepository,
            userService,
            resourceMatchRepository
        );
    }

    @After
    public void reset(){
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

    private List<String> prepareCreateOffer(){
        Organization organization = new Organization();
        organization.setId(1L);
        expOffer = new ResourceOffer();
        expOffer.setDescription(" Here is my test Requirement... bla bla. ");
        expOffer.setAmount(new BigDecimal(10));
        expOffer.setOrganization(organization);
        expOffer.setIsCommercial(true);
        expOffer.setIsRecurrent(false);
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

        //Step 1 Check if the same ResourceRequirement exists, by Project ID and Description
        when(resourceOfferRepositoryMock.findByName(expOffer.getName(), null)).thenReturn(resourceOffers);
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

    private void prepareUser(){
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
        loggedInUser.setOrganization(expOrg);
        when(this.userService.getUserWithAuthorities()).thenReturn(loggedInUser);
        when(this.organizationRepository.findOne(isA(longCl))).thenReturn(expOrg);
    }

    private void prepareProject(Long projectId){
        expProject = new Project();
        expProject.setId(projectId);
        expectedReq.setProject(expProject);
        expProject.setOrganization(expOrg);
        when(this.projectRepository.findOne(expProject.getId())).thenReturn(expProject);
    }

    private List<String> prepareCreateRequirement(){
        this.prepareProject(1L);
        //region Test data
        expectedReq = new ResourceRequirement();
        expectedReq.setDescription(" Here is my test Requirement... bla bla. ");
        expectedReq.setOriginalAmount(new BigDecimal(10));
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
        when(resourceRequirementRepositoryMock.findByNameAndProject(expectedReq.getName(),
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

    private void prepareTagRepository(String tagName, List<ResourceTag> resourceTags){
        // -> saveResourceTag method
        // Step 3: Return Empty list for our search case
        when(resourceTagRepositoryMock.findByName(tagName)).thenReturn(null);
        when(resourceTagServiceMock.getOrCreateTags(any(List.class))).thenReturn(resourceTags);
        // Step 4: Save Resource Tag
        if(newTagId != null) {
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

        //save without any tags
        ResourceRequirement actual = this.resourceService
            .createRequirement(expectedReq.getName(), expectedReq.getOriginalAmount(),
                expectedReq.getDescription(), expectedReq.getProject(), expectedReq.getIsEssential(), tags);

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

        for(ResourceTag actTag: expectedReq.getResourceTags()) {
            assertEquals(actTag.getId(), expResourceTag.getId());
            assertEquals(actTag.getName(), expResourceTag.getName());
        }
        //verifying user authorities has been called.
        verify(userService, times(1)).getUserWithAuthorities();
        verify(organizationRepository, times(0)).findOne(isA(longCl));

        verify(resourceRequirementRepositoryMock, times(1)).save(isA(reqCl));
    }

    @Test(expected = NoSuchProjectException.class)
    public void testUpdateRequirement_Fail() throws Exception {
        List<String> tags = this.prepareCreateRequirement();
        //save without any tags
        ResourceRequirement actual = this.resourceService.updateRequirement(expectedReq.getId(),
            expectedReq.getName(), expectedReq.getAmount(), expectedReq.getDescription(), 0L, //just to make the project run
            expectedReq.getIsEssential(), tags);
    }

    @Test
    public void testUpdateRequirement() throws Exception{
        this.prepareUser();
        List<String> tags = this.prepareCreateRequirement();

        when(resourceRequirementRepositoryMock.findOne(expectedReq.getId())).thenReturn(expectedReq);
        //save without any tags
        ResourceRequirement actual = this.resourceService.updateRequirement(expectedReq.getId(),
            expectedReq.getName(), expectedReq.getAmount(), expectedReq.getDescription(), expectedReq.getProject(), //just to make the project run
            expectedReq.getIsEssential(), tags);

        assertEquals(expectedReq.getId(), actual.getId());
        assertEquals(expectedReq.getAmount(), actual.getAmount());
        assertEquals(expectedReq.getName(), actual.getName());
        assertEquals(expectedReq.getDescription(), actual.getDescription());
        assertEquals(expectedReq.getProject(), actual.getProject());
        assertEquals(expectedReq.getIsEssential(), actual.getIsEssential());
        assertEquals(expectedReq.getResourceTags().size(), actual.getResourceTags().size());

        for(ResourceTag actTag: expectedReq.getResourceTags()) {
            assertEquals(actTag.getId(), expResourceTag.getId());
            assertEquals(actTag.getName(), expResourceTag.getName());
        }
        //verifying user authorities has been called.
        verify(userService, times(1)).getUserWithAuthorities();
        verify(organizationRepository, times(0)).findOne(isA(longCl));

        verify(resourceRequirementRepositoryMock, times(1)).save(isA(reqCl));
    }

    @Test
    public void testDeleteRequirement() throws Exception {
        this.prepareUser();
        List<String> tags = this.prepareCreateRequirement();

        when(resourceRequirementRepositoryMock.findOne(expectedReq.getId())).thenReturn(expectedReq);
        this.resourceService.deleteRequirement(expectedReq.getId());

        verify(resourceRequirementRepositoryMock, times(1)).findOne(isA(longCl));
        verify(resourceRequirementRepositoryMock, times(1)).delete(isA(longCl));
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
        }).when(resourceRequirementRepositoryMock).findAll();
        Long expected = 1L;
        int listSize = 2;
        List<ResourceRequirement> items = this.resourceService.getAllRequirements();
        assertEquals(items.size(), listSize);
        for(int i = 0; i < items.size(); i++){
            ResourceRequirement current = items.get(i);
            assertEquals(current.getId(), expected);
            assertEquals(current.getProject().getId(), expProject.getId());
            expected += 9L;
        }

        verify(this.resourceRequirementRepositoryMock, times(1)).findAll();
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

        doAnswer(invo ->{
            List<ResourceRequirement> internalItems = new ArrayList<ResourceRequirement>();
            for(ResourceRequirement item: items){
                if(item.getProject() == project2){
                    internalItems.add(item);
                }
            }
            return internalItems;
        }).when(resourceRequirementRepositoryMock).findByProjectId(isA(longCl));
        int listSize = 1;
        List<ResourceRequirementRequestDTO> expectedItem = this.resourceService.getAllRequirements(projectID);
        assertEquals(expectedItem.size(), listSize);
        assertEquals(expectedItem.get(0).getProjectId(), projectID);
        assertEquals(expectedItem.get(0).getId(), expectedRequirementID);
        verify(this.resourceRequirementRepositoryMock, times(1)).findByProjectId(isA(longCl));
    }

    @Test
    public void testCreateOffer_OK() throws Exception {
        this.prepareUser();
        List<String> tags = this.prepareCreateOffer();

        //save without any tags
        ResourceOffer actual = this.resourceService.createOffer(expOffer.getName(), expOffer.getAmount(), expOffer.getDescription(), expOffer.getOrganization().getId(), expOffer.getIsCommercial(), expOffer.getIsRecurrent(), expOffer.getStartDate(), expOffer.getEndDate(), tags);

        assertEquals(expOffer.getId(), actual.getId());
        assertEquals(expOffer.getAmount(), actual.getAmount());
        assertEquals(expOffer.getName(), actual.getName());
        assertEquals(expOffer.getDescription(), actual.getDescription());
        assertEquals(expOffer.getOrganization(), actual.getOrganization());
        assertEquals(expOffer.getIsCommercial(), actual.getIsCommercial());
        assertEquals(expOffer.getIsRecurrent(), actual.getIsRecurrent());
        assertEquals(expOffer.getStartDate(), actual.getStartDate());
        assertEquals(expOffer.getEndDate(), actual.getEndDate());
        assertEquals(expOffer.getResourceTags().size(), actual.getResourceTags().size());

        for(ResourceTag actTag: expOffer.getResourceTags()) {
            assertEquals(actTag.getId(), expResourceTag.getId());
            assertEquals(actTag.getName(), expResourceTag.getName());
        }

        verify(resourceOfferRepositoryMock, times(1)).save(isA(offerCl));
    }

    @Test(expected = ResourceException.class)
    public void testUpdateOffer_Fail() throws Exception {
        this.prepareUser();
        List<String> tags = this.prepareCreateOffer();
        this.resourceService.updateOffer(expOffer.getId(), expOffer.getOrganization().getId(), expOffer.getName(), expOffer.getAmount(), expOffer.getDescription(), expOffer.getIsCommercial(), expOffer.getIsRecurrent(), expOffer.getStartDate(), expOffer.getEndDate(), tags);
    }
    @Test
    public void testUpdateOffer() throws Exception {
        this.prepareUser();
        List<String> tags = this.prepareCreateOffer();
        when(resourceOfferRepositoryMock.findOne(expOffer.getId())).thenReturn(expOffer);
        ResourceOffer actual = this.resourceService.updateOffer(expOffer.getId(), expOffer.getOrganization().getId(),
            expOffer.getName(), expOffer.getAmount(), expOffer.getDescription(), expOffer.getIsCommercial(),
            expOffer.getIsRecurrent(), expOffer.getStartDate(), expOffer.getEndDate(), tags);

        assertEquals(expOffer.getId(), actual.getId());
        assertEquals(expOffer.getAmount(), actual.getAmount());
        assertEquals(expOffer.getName(), actual.getName());
        assertEquals(expOffer.getDescription(), actual.getDescription());
        assertEquals(expOffer.getOrganization(), actual.getOrganization());
        assertEquals(expOffer.getIsCommercial(), actual.getIsCommercial());
        assertEquals(expOffer.getIsRecurrent(), actual.getIsRecurrent());
        assertEquals(expOffer.getStartDate(), actual.getStartDate());
        assertEquals(expOffer.getEndDate(), actual.getEndDate());
        assertEquals(expOffer.getResourceTags().size(), actual.getResourceTags().size());

        for(ResourceTag actTag: expOffer.getResourceTags()) {
            assertEquals(actTag.getId(), expResourceTag.getId());
            assertEquals(actTag.getName(), expResourceTag.getName());
        }

        verify(resourceOfferRepositoryMock, times(1)).save(isA(offerCl));
        //No tags has been added
        verify(resourceTagServiceMock, times(1)).getOrCreateTags(any());
    }
    @Test
    public void testDeleteOffer() throws Exception {
        this.prepareUser();
        List<String> tags = this.prepareCreateOffer();
        when(resourceOfferRepositoryMock.findOne(expOffer.getId())).thenReturn(expOffer);
        this.resourceService.deleteOffer(expOffer.getId());

        verify(resourceOfferRepositoryMock, times(1)).findOne(isA(longCl));
        verify(resourceOfferRepositoryMock, times(1)).delete(isA(longCl));
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
        List<ResourceOfferDTO> list = this.resourceService.getAllOffers(expOrg.getId());
        assertEquals(listSize, list.size());
        for(int i = 0; i < list.size(); i++){
            ResourceOfferDTO current = list.get(i);
            assertEquals(current.getId(), expected);
            assertEquals(current.getOrganizationId(), expOrg.getId());
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
        list = this.resourceService.getAllOffers(99L);

        assertEquals(list.size(), listSize);
        for(int i = 0; i < list.size(); i++){
            ResourceOfferDTO current = list.get(i);
            assertEquals(current.getId(), expected);
            assertEquals(current.getOrganizationId(), alternative.getId());
        }

        verify(this.resourceOfferRepositoryMock, times(2)).findByOrganizationIdAndActiveIsTrue(isA(longCl));
    }

    /**
     * Prepare for Project Apply Tests
     * @param offer, that become our donation
     * @param req, that need our donation
     * @param org, organisation that apply the offer (donation)
     * @param project, that need our offer (donation)
     */
    private void prepareProjectApply(ResourceOffer offer, ResourceRequirement req, Organization org, Project project){
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
        when(resourceOfferRepositoryMock.findOne(offerId)).thenReturn(offer);
        when(resourceRequirementRepositoryMock.findOne(requirementId)).thenReturn(req);
        when(organizationRepository.findOne(organizationId)).thenReturn(org);
        when(projectRepository.findOne(projectId)).thenReturn(project);
        when(userService.getUserWithAuthorities()).thenReturn(orgUser);


    }

    /**
     * Test for successful project apply.
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
        }).when(resourceMatchRepository).save(isA(ResourceMatch.class));
        when(resourceMatchRepository.findByResourceOfferAndResourceRequirementAndOrganizationAndProjectAndActiveIsTrue(
            isA(ResourceOffer.class), isA(ResourceRequirement.class), isA(Organization.class), isA((Project.class)))).
            thenReturn(new ArrayList<>());

        ResourceMatch actual = resourceService.createProjectApplyOffer(offerId, requirementId, organizationId, projectId);
        assertEquals(expected.getId(), actual.getId());
        assertEquals(expected.getAmount(), actual.getAmount());

        verify(resourceOfferRepositoryMock, times(1)).findOne(offerId);
        verify(resourceRequirementRepositoryMock, times(1)).findOne(requirementId);
        verify(organizationRepository, times(1)).findOne(organizationId);
        verify(projectRepository, times(1)).findOne(projectId);
        verify(userService, times(1)).getUserWithAuthorities();
    }

    /**
     * Test for successful project apply.
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
        }).when(resourceMatchRepository).save(isA(ResourceMatch.class));
        when(resourceMatchRepository.findByResourceOfferAndResourceRequirementAndOrganizationAndProjectAndActiveIsTrue(
            isA(ResourceOffer.class), isA(ResourceRequirement.class), isA(Organization.class), isA((Project.class)))).
            thenReturn(new ArrayList<>());

        ResourceMatch actual = resourceService.createProjectApplyOffer(offerId, requirementId, organizationId, projectId);
        assertEquals(expected.getId(), actual.getId());
        assertEquals(expected.getAmount(), actual.getAmount());

        verify(resourceOfferRepositoryMock, times(1)).findOne(offerId);
        verify(resourceRequirementRepositoryMock, times(1)).findOne(requirementId);
        verify(organizationRepository, times(1)).findOne(organizationId);
        verify(projectRepository, times(1)).findOne(projectId);
        verify(userService, times(1)).getUserWithAuthorities();
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
        when(resourceMatchRepository.findByResourceOfferAndResourceRequirementAndOrganizationAndProjectAndActiveIsTrue(
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
        when(resourceMatchRepository.findByResourceOfferAndResourceRequirementAndOrganizationAndProjectAndActiveIsTrue(
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
        when(resourceMatchRepository.findByResourceOfferAndResourceRequirementAndOrganizationAndProjectAndActiveIsTrue(
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
        when(resourceMatchRepository.findByResourceOfferAndResourceRequirementAndOrganizationAndProjectAndActiveIsTrue(
            isA(ResourceOffer.class), isA(ResourceRequirement.class), isA(Organization.class), isA((Project.class)))).
            thenReturn(a);

        resourceService.createProjectApplyOffer(offerId, requirementId, organizationId, projectId);
    }
}
