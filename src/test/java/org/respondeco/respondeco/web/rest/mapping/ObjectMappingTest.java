package org.respondeco.respondeco.web.rest.mapping;

import org.junit.Before;
import org.junit.Test;
import org.respondeco.respondeco.domain.Organization;
import org.respondeco.respondeco.domain.Project;
import org.respondeco.respondeco.domain.ResourceMatch;
import org.respondeco.respondeco.domain.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;

import static org.junit.Assert.*;

/**
 * Created by clemens on 24/03/15.
 */
public class ObjectMappingTest {

    private final Logger log = LoggerFactory.getLogger(ObjectMappingTest.class);

    private ObjectMapperFactory mapperFactory;
    private Project project;

    @Before
    public void setUp() {
        mapperFactory = new ObjectMapperFactory();
        project = new Project();
        project.setId(3L);
        project.setName("foobar");
        project.setConcrete(true);
        project.setResourceRequirements(new ArrayList<>());
        project.setResourceMatches(Arrays.asList(
            new ResourceMatch() {{
                setId(100L);
            }}
        ));
        project.setOrganization(new Organization() {{
            setName("orgName");
            setId(2L);
            setIsoCategories(new ArrayList<>());
            setOwner(new User() {{
                setId(1L);
                setFirstName("orgOwnerFirstName");
                setLastName("orgOwnerLastName");
            }});
        }});
        project.setManager(new User() {{
            setId(5L);
            setFirstName("managerFirstName");
            setLastName("managerLastName");
        }});

    }

    @Test
    public void testSimpleExpression() throws Exception {
        ObjectMapper mapper = mapperFactory.createMapper(Project.class, "concrete");
        Map<String, Object> mapping = mapper.map(project);
        log.debug("{}", mapping);
        assertTrue(mapping.containsKey("id"));
        assertTrue(mapping.containsKey("concrete"));
        assertTrue(mapping.containsKey("purpose"));
        assertEquals(true, mapping.get("concrete"));
    }

    @Test
    public void testSimpleNegatedExpression() throws Exception {
        ObjectMapper mapper = mapperFactory.createMapper(Project.class, "-id");
        Map<String, Object> mapping = mapper.map(project);
        log.debug("{}", mapping);
        assertFalse(mapping.containsKey("id"));
        assertTrue(mapping.containsKey("purpose"));
    }

    @Test
    public void testSimplePostiveExpression() throws Exception {
        ObjectMapper mapper = mapperFactory.createMapper(Project.class, "+concrete");
        Map<String, Object> mapping = mapper.map(project);
        log.debug("{}", mapping);
        assertTrue(mapping.containsKey("concrete"));
        assertEquals(true, mapping.get("concrete"));
    }

    @Test
    public void testManySimpleExpressions() throws Exception {
        ObjectMapper mapper = mapperFactory.createMapper(Project.class, "+concrete,manager,startDate");
        Map<String, Object> mapping = mapper.map(project);
        log.debug("{}", mapping);
        assertTrue(mapping.containsKey("concrete"));
        assertTrue(mapping.containsKey("manager"));
        assertTrue(mapping.containsKey("startDate"));
        assertEquals(true, mapping.get("concrete"));
        assertEquals(5L, ((Map<String,Object>) mapping.get("manager")).get("id"));
        assertNull(mapping.get("startDate"));
    }

    @Test
    public void testNestedExpression() throws Exception {
        ObjectMapper mapper = mapperFactory.createMapper(Project.class,
            "+concrete,manager(-id,firstName),organization(owner(firstName))");
        Map<String, Object> mapping = mapper.map(project);
        log.debug("{}", mapping);
        assertTrue(mapping.containsKey("concrete"));
        assertTrue(mapping.containsKey("manager"));
        assertTrue(mapping.containsKey("organization"));

        Map<String,Object> managerMapping = (Map<String,Object>) mapping.get("manager");
        assertFalse(managerMapping.containsKey("id"));
        assertEquals("managerFirstName", managerMapping.get("firstName"));

        Map<String,Object> orgMapping = (Map<String,Object>) mapping.get("organization");
        assertEquals(2L, orgMapping.get("id"));
        assertTrue(orgMapping.containsKey("owner"));
        assertTrue(orgMapping.containsKey("name"));

        Map<String,Object> ownerMapping = (Map<String,Object>) orgMapping.get("owner");
        assertEquals(1L, ownerMapping.get("id"));
        assertTrue(ownerMapping.containsKey("firstName"));
        assertTrue(ownerMapping.containsKey("login"));

    }

}
