package org.respondeco.respondeco.web.rest.mapping;

import org.joda.time.LocalDate;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.junit.Before;
import org.junit.Test;
import org.respondeco.respondeco.domain.Project;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import static org.junit.Assert.assertEquals;

/**
 * Created by clemens on 22/03/15.
 */
public class FieldMappingTest {

    private static DateTimeFormatter formatter =
        DateTimeFormat.forPattern("yyyy-MM-dd");

    private ReflectionUtil util;
    private Project project;
    private String fieldName;
    private Field field;
    private String ignoredFieldName;
    private Field ignoredField;
    private Method accessor;
    private Method ignoredFieldAccessor;
    private FieldMapping mapping;

    @Before
    public void setUp() throws Exception {
        util = new ReflectionUtil();
        fieldName = "name";
        field = util.getField(Project.class, fieldName);
        accessor = util.getAccessor(Project.class, fieldName);
        ignoredFieldName = "projectLogo";
        ignoredField = util.getField(Project.class, ignoredFieldName);
        ignoredFieldAccessor = util.getAccessor(Project.class, ignoredFieldName);
        project = new Project();
        project.setName("testproject");
    }

    @Test
    public void testExistingField() throws Exception {
        mapping = new FieldMapping(fieldName, field, accessor);
    }

    @Test(expected = MappingException.class)
    public void testIgnoredField() throws Exception {
        mapping = new FieldMapping(ignoredFieldName, ignoredField, ignoredFieldAccessor);
    }

    @Test
    public void testSimpleTypeMapping() throws Exception {
        mapping = new FieldMapping(fieldName, field, accessor);
        Object value = mapping.map(project);
        assertEquals(project.getName(), value);
    }

}
