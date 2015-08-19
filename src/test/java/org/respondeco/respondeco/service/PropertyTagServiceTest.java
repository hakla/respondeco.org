package org.respondeco.respondeco.service;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.respondeco.respondeco.Application;
import org.respondeco.respondeco.domain.PropertyTag;
import org.respondeco.respondeco.repository.PropertyTagRepository;
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
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

/**
 * Created by clemens on 24/01/15.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Application.class)
@WebAppConfiguration
public class PropertyTagServiceTest {

    @Mock
    private PropertyTagRepository propertyTagRepository;

    private PropertyTagService propertyTagService;
    private List<PropertyTag> tags;
    private ArgumentCaptor<PropertyTag> tagArgumentCaptor;

    @Before
    public void setup() throws Exception {
        MockitoAnnotations.initMocks(this);
        propertyTagService = new PropertyTagService(propertyTagRepository);
        tags = new ArrayList<>();
        PropertyTag propertyTag1 = new PropertyTag();
        propertyTag1.setId(1L);
        propertyTag1.setName("tag1");
        tags.add(propertyTag1);
        PropertyTag propertyTag2 = new PropertyTag();
        propertyTag2.setId(2L);
        propertyTag2.setName("tag2");
        tags.add(propertyTag2);

        tagArgumentCaptor = ArgumentCaptor.forType(PropertyTag.class, 0, false);
    }

    /**
     * test if the service calls the appropriate repository method
     * @throws Exception
     */
    @Test
    public void testGetPropertyTags_shouldCallRepository() throws Exception {
        doReturn(tags).when(propertyTagRepository)
            .findByNameContainingIgnoreCase(any(String.class), any(Pageable.class));

        propertyTagService.getPropertyTags("tag", null);

        verify(propertyTagRepository, times(1)).findByNameContainingIgnoreCase(any(String.class), any(Pageable.class));
    }

    /**
     * test if the service calls the appropriate repository methods
     * @throws Exception
     */
    @Test
    public void testGetOrCreateTags_shouldCallRepository() throws Exception {
        doReturn(tags.get(0)).when(propertyTagRepository)
            .findByName("tag1");
        doReturn(null).when(propertyTagRepository)
            .findByName("tag2");
        doAnswer(tagArgumentCaptor).when(propertyTagRepository)
            .save(any(PropertyTag.class));

        //propertyTagService.getOrCreateTags(Arrays.asList("tag1", "tag2"));

        verify(propertyTagRepository, times(2)).findByName(any(String.class));
        verify(propertyTagRepository, times(1)).save(any(PropertyTag.class));
        assertEquals("tag2", tagArgumentCaptor.getValue().getName());
    }

}
