package org.respondeco.respondeco.service;

import org.junit.Before;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.respondeco.respondeco.Application;
import org.respondeco.respondeco.repository.*;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.transaction.annotation.Transactional;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Application.class)
@WebAppConfiguration
@Transactional
public class ResourcesServiceTest {

    @Mock
    private ResourceOfferRepository resourceOfferRepository;
    @Mock
    private ResourceRequirementRepository resourceRequirementRepository;
    @Mock
    private ResourceTagRepository resourceTagRepository;
    @Mock
    private ResourceOfferJoinResourceRequirementRepository resourceOfferJoinResourceRequirementRepository;
    @Mock
    private ResourceOfferJoinResourceTagRepository resourceOfferJoinResourceTagRepository;
    @Mock
    private ResourceRequirementJoinResourceTagRepository resourceRequirementJoinResourceTagRepository;

    private ResourcesService resourcesService;

    @Before
    public void setup() throws Exception {
        MockitoAnnotations.initMocks(this);
        this.resourcesService = new ResourcesService(
            resourceOfferRepository,
            resourceRequirementRepository,
            resourceTagRepository,
            resourceRequirementJoinResourceTagRepository,
            resourceOfferJoinResourceRequirementRepository,
            resourceOfferJoinResourceTagRepository
        );
    }
}
