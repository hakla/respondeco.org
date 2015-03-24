package org.respondeco.respondeco.web.rest.mapper;

import org.junit.Before;
import org.junit.Test;
import org.respondeco.respondeco.domain.Project;

import static org.junit.Assert.assertEquals;

/**
 * Created by clemens on 22/03/15.
 */
public class FieldMappingTest {

    private Project project;
    private FieldMapping mapping;

    @Before
    public void setUp() {
        project = new Project();
        project.setName("testproject");
    }

    @Test
    public void testInitExistingField() throws Exception {
        mapping = new FieldMapping(Project.class, "name");
    }

    @Test(expected = NoSuchFieldException.class)
    public void testInitNonexistingField() throws Exception {
        mapping = new FieldMapping(Project.class, "foo");
    }

    @Test
    public void testSimpleTypeMapping() throws Exception {
        mapping = new FieldMapping(Project.class, "name");
        Object value = mapping.map(project);
        assertEquals(project.getName(), value);
    }
}
