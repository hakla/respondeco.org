package org.respondeco.respondeco.web.rest.mapping;

import org.joda.time.LocalDate;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.junit.Before;
import org.junit.Test;
import org.respondeco.respondeco.domain.Project;

import static org.junit.Assert.assertEquals;

/**
 * Created by clemens on 22/03/15.
 */
public class FieldMappingTest {

    private static DateTimeFormatter formatter =
        DateTimeFormat.forPattern("yyyy-MM-dd");

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

    @Test(expected = MappingException.class)
    public void testInitNonExistingField() throws Exception {
        mapping = new FieldMapping(Project.class, "foo");
    }

    @Test
    public void testSimpleTypeMapping() throws Exception {
        mapping = new FieldMapping(Project.class, "name");
        Object value = mapping.map(project);
        assertEquals(project.getName(), value);
    }

    @Test
    public void testCustomSerializerMapping() throws Exception {
        LocalDate now = LocalDate.now();
        project.setStartDate(now);
        mapping = new FieldMapping(Project.class, "startDate");
        Object value = mapping.map(project);
        String expectedValue = formatter.print(project.getStartDate());
        assertEquals(expectedValue, value);
    }
}
