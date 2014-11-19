package org.respondeco.respondeco.service;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.respondeco.respondeco.Application;
import org.respondeco.respondeco.domain.ResourceRequirement;
import org.respondeco.respondeco.domain.ResourceRequirementJoinResourceTag;
import org.respondeco.respondeco.domain.ResourceTag;
import org.respondeco.respondeco.repository.*;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.transaction.annotation.Transactional;

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
public class ResourcesServiceTest {

    @Mock
    private ResourceOfferRepository resourceOfferRepository;
    @Mock
    private ResourceRequirementRepository resourceRequirementRepositoryMock;
    @Mock
    private ResourceTagRepository resourceTagRepositoryMock;
    @Mock
    private ResourceOfferJoinResourceRequirementRepository resourceOfferJoinResourceRequirementRepositoryMock;
    @Mock
    private ResourceOfferJoinResourceTagRepository resourceOfferJoinResourceTagRepositoryMock;
    @Mock
    private ResourceRequirementJoinResourceTagRepository resourceRequirementJoinResourceTagRepositoryMock;

    private ResourcesService resourcesService;
    private ResourceRequirement savedRequirement;

    private static Class<Long> longCl = Long.class;
    private static Class<String> stringCl = String.class;
    private static Class<ResourceRequirement> reqCl = ResourceRequirement.class;
    private static Class<ResourceRequirementJoinResourceTag> reqJoinTagCl = ResourceRequirementJoinResourceTag.class;
    private static Class<ResourceTag> tagCl = ResourceTag.class;

    @Before
    public void setup() throws Exception {
        MockitoAnnotations.initMocks(this);
        this.resourcesService = new ResourcesService(
            resourceOfferRepository,
            resourceRequirementRepositoryMock,
            resourceTagRepositoryMock,
            resourceRequirementJoinResourceTagRepositoryMock,
            resourceOfferJoinResourceRequirementRepositoryMock,
            resourceOfferJoinResourceTagRepositoryMock
        );
    }

    @Test
    public void testCreateRequirement() throws Exception {
        ResourceRequirement req = new ResourceRequirement();
        BigDecimal amount = new BigDecimal(10);
        String description = "Here is my test Requirement... bla bla.";
        Long projectId = 1L;
        Boolean isEssential = true;
        String[] tags = new String[1];
        tags[0] = "test";
        Long requirementId = 1L;

        ResourceTag testResourceTag = new ResourceTag(1L, tags[0]);
        List<ResourceTag> resourceTags = new ArrayList<ResourceTag>();
        resourceTags.add(testResourceTag);

        when(resourceRequirementRepositoryMock.findByDescriptionAndProjectId(description, projectId)).thenReturn(null);
        when(resourceTagRepositoryMock.findByName(tags[0])).thenReturn(resourceTags);
        when(resourceRequirementJoinResourceTagRepositoryMock.countByResourceRequirementIdAndResourceTagIdCount(requirementId, testResourceTag.getId())).thenReturn(null);
        //redirect output to the variable of the test class
        doAnswer(invocation -> {
            Object[] args = invocation.getArguments();
            savedRequirement = (ResourceRequirement) args[0];
            return null;
        }).when(resourceRequirementRepositoryMock).save(isA(reqCl));

        //save without any tags
        this.resourcesService.createRequirement(amount, description, projectId, isEssential, null);

        assertEquals(savedRequirement.getAmount(), amount);
        assertEquals(savedRequirement.getDescription(), description);
        assertEquals(savedRequirement.getProjectId(), projectId);
        assertEquals(savedRequirement.getIsEssential(), isEssential);
        assertEquals(savedRequirement.getResourceTags().size(), 0);

        verify(resourceRequirementRepositoryMock, times(1)).save(isA(reqCl));
        //No tags has been added
        verify(resourceTagRepositoryMock, times(1)).findByName(isA(stringCl));
        verify(resourceTagRepositoryMock, times(1)).save(isA(tagCl));
        //No Tag joins added
        verify(resourceRequirementJoinResourceTagRepositoryMock, times(1)).countByResourceRequirementIdAndResourceTagIdCount(isA(longCl), isA(longCl));
        verify(resourceRequirementJoinResourceTagRepositoryMock, times(1)).save(isA(reqJoinTagCl));

    }

    public void testDeleteRequirement() throws Exception {

    }

    public void testGetAllRequirements() throws Exception {

    }

    public void testGetAllRequirements1() throws Exception {

    }

    public void testCreateOffer() throws Exception {

    }

    public void testUpdateOffer() throws Exception {

    }

    public void testDeleteOffer() throws Exception {

    }

    public void testGetAllOffers() throws Exception {

    }

    public void testGetAllOffers1() throws Exception {

    }
}
