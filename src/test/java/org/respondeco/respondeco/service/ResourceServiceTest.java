package org.respondeco.respondeco.service;

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
import org.respondeco.respondeco.service.exception.ResourceException;
import org.respondeco.respondeco.service.exception.ResourceJoinTagException;
import org.respondeco.respondeco.service.exception.ResourceTagException;
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
import java.util.List;

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
    private ResourceTagService resourceTagServiceMock;

    private ResourceService resourceService;
    private ResourceOffer expOffer = null;
    private ResourceTag expResourceTag = null;
    private ResourceRequirement expectedReq = new ResourceRequirement();
    private Long newTagId = 1L;

    private static Class<Long> longCl = Long.class;
    private static Class<String> stringCl = String.class;
    private static Class<ResourceRequirement> reqCl = ResourceRequirement.class;
    private static Class<ResourceOffer> offerCl = ResourceOffer.class;
    private static Class<ResourceTag> tagCl = ResourceTag.class;

    /*
    @Before
    public void setup() throws Exception {
        MockitoAnnotations.initMocks(this);
        this.resourceService = new ResourceService(
            resourceOfferRepositoryMock,
            resourceRequirementRepositoryMock,
            resourceTagServiceMock,
            organizationRepository,
            projectRepository
        );
    }

    @After
    public void reset(){
        expOffer = null;
        expResourceTag = null;
        expectedReq = null;
        newTagId = 1L;
    }

    private String[] prepareCreateOffer(){
        Organization organisation = new Organization();
        organisation.setId(1L);
        expOffer = new ResourceOffer();
        expOffer.setDescription(" Here is my test Requirement... bla bla. ");
        expOffer.setAmount(new BigDecimal(10));
        expOffer.setOrganization(organisation);
        expOffer.setIsCommercial(true);
        expOffer.setIsRecurrent(false);
        expOffer.setStartDate(LocalDate.now());
        expOffer.setId(1L);
        expOffer.setName(" TEST ");
        expResourceTag = new ResourceTag(1L, "test ");
        expResourceTag.setId(1L);
        expOffer.addResourceTag(expResourceTag);
        String[] tags = new String[1];
        tags[0] = expResourceTag.getName();

        List<ResourceTag> resourceTags = new ArrayList<ResourceTag>();
        List<ResourceOffer> resourceOffers = new ArrayList<ResourceOffer>();

        //Step 1 Check if the same ResourceRequirement exists, by Project ID and Description
        when(resourceOfferRepositoryMock.findByNameAndOrganization(expOffer.getName(), expOffer.getOrganization().getId())).thenReturn(resourceOffers);
        //Assign all variables to new Resource Requirement Objekt and execute Save
        doAnswer(invocation -> {
            Object[] args = invocation.getArguments();
            ResourceOffer result = (ResourceOffer) args[0];
            result.setId(1L);
            Console.out().print(args[0]);
            return result;
        }).when(resourceOfferRepositoryMock).save(isA(offerCl));
        this.prepareTagRepository(tags[0], resourceTags);

        return tags;
    }

    private List<String> prepareCreateRequirement(){
        Project project = new Project();
        project.setId(1L);
        //region Test data
        expectedReq = new ResourceRequirement();
        expectedReq.setDescription(" Here is my test Requirement... bla bla. ");
        expectedReq.setAmount(new BigDecimal(10));
        expectedReq.setProject(project);
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

        //resourceTags.add(testResourceTag);
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
        when(resourceTagRepositoryMock.findByName(tagName)).thenReturn(resourceTags.get(0));
        // Step 4: Save Resource Tag
        doAnswer(invocation -> {
            Object[] args = invocation.getArguments();
            ResourceTag tag = (ResourceTag) args[0];
            tag.setId(newTagId);
            Console.out().print(args[0]);
            return tag;
        }).when(resourceTagRepositoryMock).save(isA(tagCl));
    }

    @Test
    public void testCreateRequirement_OK() throws Exception {

        List<String> tags = this.prepareCreateRequirement();

        //save without any tags
        ResourceRequirement actual = this.resourceService
            .createRequirement(expectedReq.getName(), expectedReq.getAmount(),
                expectedReq.getDescription(), expectedReq.getProject(), expectedReq.getIsEssential(), tags);

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

        verify(resourceRequirementRepositoryMock, times(1)).save(isA(reqCl));
        //No tags has been added
        verify(resourceTagRepositoryMock, times(1)).findByName(isA(stringCl));
        verify(resourceTagRepositoryMock, times(1)).save(isA(tagCl));
    }

    @Test(expected = ResourceException.class)
    public void testUpdateRequirement_Fail() throws Exception {
        List<String> tags = this.prepareCreateRequirement();
        //save without any tags
        ResourceRequirement actual = this.resourceService.updateRequirement(expectedReq.getId(),
            expectedReq.getName(), expectedReq.getAmount(), expectedReq.getDescription(), 0L, //just to make the project run
            expectedReq.getIsEssential(), tags);
    }

    @Test
    public void testUpdateRequirement() throws Exception{
        List<String> tags = this.prepareCreateRequirement();

        when(resourceRequirementRepositoryMock.findOne(expectedReq.getId())).thenReturn(expectedReq);
        //save without any tags
        ResourceRequirement actual = this.resourceService.updateRequirement(expectedReq.getId(),
            expectedReq.getName(), expectedReq.getAmount(), expectedReq.getDescription(), 0L, //just to make the project run
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

        verify(resourceRequirementRepositoryMock, times(1)).save(isA(reqCl));
        //No tags has been added
        verify(resourceTagRepositoryMock, times(1)).findByName(isA(stringCl));
        verify(resourceTagRepositoryMock, times(1)).save(isA(tagCl));
        //No Tag joins added
    }

    @Test(expected = ResourceTagException.class)
    public void testCreateRequirement_TagFailed() throws Exception {

        List<String> tags = this.prepareCreateRequirement();

        //save without any tags
        ResourceRequirement actual = this.resourceService.createRequirement(expectedReq.getName(), expectedReq.getAmount(), expectedReq.getDescription(), expectedReq.getProject().getId(), expectedReq.getIsEssential(), tags);
    }

    @Test(expected = ResourceJoinTagException.class)
    public void testCreateRequirement_ReqJoinTagFailed() throws Exception {
        this.newTagId = null;
        List<String> tags = this.prepareCreateRequirement();


        //save without any tags
        ResourceRequirement actual = this.resourceService.createRequirement(expectedReq.getName(), expectedReq.getAmount(), expectedReq.getDescription(), expectedReq.getProject().getId(), expectedReq.getIsEssential(), tags);
    }

    @Test
    public void testDeleteRequirement() throws Exception {

        List<String> tags = this.prepareCreateRequirement();
        when(resourceRequirementRepositoryMock.findOne(expectedReq.getId())).thenReturn(expectedReq);
        this.resourceService.deleteRequirement(expectedReq.getId());

        verify(resourceRequirementRepositoryMock, times(1)).findOne(isA(longCl));
        verify(resourceRequirementRepositoryMock, times(1)).delete(isA(longCl));
    }

    @Test
    public void testGetAllRequirements_2Entries() throws Exception {
        doAnswer(invo ->{
            List<ResourceRequirement> items = new ArrayList<ResourceRequirement>(2);
            ResourceRequirement item1 = new ResourceRequirement();
            item1.setId(1L);
            items.add(item1);
            ResourceRequirement item2 = new ResourceRequirement();
            item2.setId(10L);
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


        List<String> tags = this.prepareCreateRequirement();

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
        //No tags has been added
        verify(resourceTagRepositoryMock, times(1)).findByName(isA(stringCl));
        verify(resourceTagRepositoryMock, times(1)).save(isA(tagCl));
    }

    @Test(expected = ResourceTagException.class)
    public void testCreateOffer_TagFailed() throws Exception {


        List<String> tags = this.prepareCreateRequirement();
        //tags[0] = null;
        //save without any tags
        ResourceOffer actual = this.resourceService.createOffer(expOffer.getName(), expOffer.getAmount(), expOffer.getDescription(), expOffer.getOrganization().getId(), expOffer.getIsCommercial(), expOffer.getIsRecurrent(), expOffer.getStartDate(), expOffer.getEndDate(), tags);
    }

    @Test(expected = ResourceJoinTagException.class)
    public void testCreateOffer_ReqJoinTagFailed() throws Exception {

        this.newTagId = null;
        List<String> tags = this.prepareCreateRequirement();
        //save without any tags
        ResourceOffer actual = this.resourceService.createOffer(expOffer.getName(), expOffer.getAmount(), expOffer.getDescription(), expOffer.getOrganization().getId(), expOffer.getIsCommercial(), expOffer.getIsRecurrent(), expOffer.getStartDate(), expOffer.getEndDate(), tags);
    }

    @Test(expected = ResourceException.class)
    public void testUpdateOffer_Fail() throws Exception {

        List<String> tags = this.prepareCreateRequirement();
        ResourceOffer actual = this.resourceService.updateOffer(expOffer.getId(), expOffer.getOrganization().getId(), expOffer.getName(), expOffer.getAmount(), expOffer.getDescription(), expOffer.getIsCommercial(), expOffer.getIsRecurrent(), expOffer.getStartDate(), expOffer.getEndDate(), tags);

    }
    @Test
    public void testUpdateOffer() throws Exception {

        List<String> tags = this.prepareCreateRequirement();
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
        verify(resourceTagRepositoryMock, times(1)).findByName(isA(stringCl));
        verify(resourceTagRepositoryMock, times(1)).save(isA(tagCl));
    }
    @Test
    public void testDeleteOffer() throws Exception {

        String[] tags = this.prepareCreateOffer();
        when(resourceOfferRepositoryMock.findOne(expOffer.getId())).thenReturn(expOffer);
        this.resourceService.deleteOffer(expOffer.getId());

        verify(resourceOfferRepositoryMock, times(1)).findOne(isA(longCl));
        verify(resourceOfferRepositoryMock, times(1)).delete(isA(longCl));
    }

    @Test
    public void testGetAllOffers_2Entries() throws Exception {
        doAnswer(invo ->{
            List<ResourceOffer> items = new ArrayList<ResourceOffer>(2);
            ResourceOffer item1 = new ResourceOffer();
            item1.setId(1L);
            items.add(item1);
            ResourceOffer item2 = new ResourceOffer();
            item2.setId(10L);
            items.add(item2);
            return items;
        }).when(resourceOfferRepositoryMock).findAll();
        Long expected = 1L;
        int listSize = 2;
        List<ResourceOfferDTO> items = this.resourceService.getAllOffers();
        assertEquals(items.size(), listSize);
        for(int i = 0; i < items.size(); i++){
            ResourceOfferDTO current = items.get(i);
            assertEquals(current.getId(), expected);
            expected += 9L;
        }

        verify(this.resourceOfferRepositoryMock, times(1)).findAll();
    }

    /*
    @Test
    public void testGetAllOffers_1EntryForOrganisation2() throws Exception {
        final Long organisationID = 2L;
        final Long expectedOfferID = 10L;
        final Organization organisation1 = new Organization();
        organisation1.setId(1L);
        final Organization organisation2 = new Organization();
        organisation1.setId(2L);

        final List<ResourceOffer> items = new ArrayList<ResourceOffer>();
        ResourceOffer item1 = new ResourceOffer();
        item1.setId(1L);
        item1.setOrganisation(organisation1);
        items.add(item1);
        ResourceOffer item2 = new ResourceOffer();
        item2.setOrganisation(organisation2);
        item2.setId(expectedOfferID);
        items.add(item2);

        doAnswer(invo ->{
            List<ResourceOffer> internalItems = new ArrayList<ResourceOffer>();
            for(ResourceOffer item: items){
                if(item.getOrganisation() == organisation2){
                    internalItems.add(item);
                }
            }
            return internalItems;
        }).when(resourceOfferRepositoryMock).findByOrganization(isA(longCl));
        int listSize = 1;
        List<ResourceOfferDTO> expectedItem = this.resourceService.getAllOffers(organisationID);
        assertEquals(expectedItem.size(), listSize);
        assertEquals(expectedItem.get(0).getOrganizationId(), ogranisationID);
        assertEquals(expectedItem.get(0).getId(), expectedOfferID);
        verify(this.resourceOfferRepositoryMock, times(1)).findByOrganization(isA(longCl));

    }*/
}
