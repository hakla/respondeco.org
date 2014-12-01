package org.respondeco.respondeco.repository;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.respondeco.respondeco.Application;
import org.respondeco.respondeco.domain.PropertyTag;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import org.springframework.test.context.support.DirtiesContextTestExecutionListener;
import org.springframework.test.context.transaction.TransactionalTestExecutionListener;
import org.springframework.test.context.web.WebAppConfiguration;

import javax.inject.Inject;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

/**
 * Created by Clemens Puehringer on 24/11/14.
 */

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Application.class)
@WebAppConfiguration
@TestExecutionListeners({ DependencyInjectionTestExecutionListener.class,
        DirtiesContextTestExecutionListener.class,
        TransactionalTestExecutionListener.class })
public class PropertyTagRepositoryTest {

    @Inject
    private PropertyTagRepository propertyTagRepository;

    @Before
    public void setUp() {
        propertyTagRepository.deleteAll();
        propertyTagRepository.flush();
    }

    @Test
    public void testGetNamesMatchingWithoutFilterAndLimit_shouldReturnAllTags() throws Exception {
        PropertyTag propertyTag;
        for(int i=0;i<30;i++) {
            propertyTag = new PropertyTag();
            propertyTag.setName("tag" + i);
            propertyTagRepository.save(propertyTag);
        }
        List<PropertyTag> tags = propertyTagRepository.findWhereNameLike("", null);
        System.err.println(tags);
        Set<String> namesSet = new HashSet<>();
        tags.forEach(t ->
            namesSet.add(t.getName())
        );
        for(int i=0;i<30;i++) {
            assertTrue(namesSet.contains("tag" + i));
        }
    }

    @Test
    public void testGetNamesMatchingWithLimit_shouldReturnOnlyLimitNames() throws Exception {
        PropertyTag propertyTag;
        for(int i=0;i<30;i++) {
            propertyTag = new PropertyTag();
            propertyTag.setName("tag" + i);
            propertyTagRepository.save(propertyTag);
        }
        PageRequest request = new PageRequest(0, 15);
        List<PropertyTag> tags = propertyTagRepository.findWhereNameLike("", request);
        assertEquals(tags.size(), 15);

    }

    @Test
    public void testGetNamesMatchingWithLimitAndReqex_shouldReturnOnlyLimitMatchingValues() throws Exception {
        PropertyTag propertyTag;
        for(int i=0;i<15;i++) {
            propertyTag = new PropertyTag();
            propertyTag.setName("tag" + i);
            propertyTagRepository.save(propertyTag);
        }
        for(int i=0;i<15;i++) {
            propertyTag = new PropertyTag();
            propertyTag.setName("blub" + i);
            propertyTagRepository.save(propertyTag);
        }
        PageRequest request = new PageRequest(0, 10);
        List<PropertyTag> tags = propertyTagRepository.findWhereNameLike("b", request);
        assertEquals(tags.size(), 10);
        for(PropertyTag t : tags) {
            assertTrue(t.getName().startsWith("blub"));
        }
    }

    @Test
    public void testFindByName_shouldReturnCorrectTag() throws Exception {
        PropertyTag propertyTag;
        for(int i=0;i<15;i++) {
            propertyTag = new PropertyTag();
            propertyTag.setName("tag" + i);
            propertyTagRepository.save(propertyTag);
        }
        PropertyTag pt = propertyTagRepository.findByName("tag10");
        assertNotNull(pt);
        assertEquals("tag10", pt.getName());
    }
}
