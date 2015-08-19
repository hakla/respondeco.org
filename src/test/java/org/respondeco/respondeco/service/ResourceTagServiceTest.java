package org.respondeco.respondeco.service;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.respondeco.respondeco.Application;
import org.respondeco.respondeco.domain.ResourceTag;
import org.respondeco.respondeco.repository.ResourceTagRepository;
import org.respondeco.respondeco.testutil.ArgumentCaptor;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

/**
 * Created by clemens on 24/01/15.
 */

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Application.class)
@WebAppConfiguration
public class ResourceTagServiceTest {

    @Mock
    private ResourceTagRepository resourceTagRepository;

    private ResourceTagService resourceTagService;
    private List<ResourceTag> tags;
    private ArgumentCaptor<ResourceTag> tagArgumentCaptor;

    @Before
    public void setup() throws Exception {
        MockitoAnnotations.initMocks(this);
        resourceTagService = new ResourceTagService(resourceTagRepository);
        tags = new ArrayList<>();
        ResourceTag resourceTag1 = new ResourceTag();
        resourceTag1.setId(1L);
        resourceTag1.setName("tag1");
        tags.add(resourceTag1);
        ResourceTag resourceTag2 = new ResourceTag();
        resourceTag2.setId(2L);
        resourceTag2.setName("tag2");
        tags.add(resourceTag2);

        tagArgumentCaptor = ArgumentCaptor.forType(ResourceTag.class, 0, false);
    }

    /**
     * test if the service calls the appropriate repository method
     * @throws Exception
     */
    @Test
    public void testGetResourceTags_shouldCallRepository() throws Exception {
        doReturn(tags).when(resourceTagRepository)
            .findByNameContainingIgnoreCase(any(String.class), any(Pageable.class));

        resourceTagService.getResourceTags("tag", null);

        verify(resourceTagRepository, times(1)).findByNameContainingIgnoreCase(any(String.class), any(Pageable.class));
    }

    /**
     * test if the service calls the appropriate repository methods
     * @throws Exception
     */
    @Test
    public void testGetOrCreateTags_shouldCallRepository() throws Exception {
        doReturn(tags.get(0)).when(resourceTagRepository)
            .findByName("tag1");
        doReturn(null).when(resourceTagRepository)
            .findByName("tag2");
        doAnswer(tagArgumentCaptor).when(resourceTagRepository)
            .save(any(ResourceTag.class));

        //resourceTagService.getOrCreateTags(Arrays.asList("tag1", "tag2"));

        verify(resourceTagRepository, times(2)).findByName(any(String.class));
        verify(resourceTagRepository, times(1)).save(any(ResourceTag.class));
        assertEquals("tag2", tagArgumentCaptor.getValue().getName());
    }
}
