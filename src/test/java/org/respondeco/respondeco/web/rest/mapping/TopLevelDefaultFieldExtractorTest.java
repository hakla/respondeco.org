package org.respondeco.respondeco.web.rest.mapping;

import org.junit.Before;
import org.junit.Test;
import org.respondeco.respondeco.domain.Project;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

import static org.junit.Assert.assertTrue;

/**
 * Created by clemens on 11/03/15.
 */
public class TopLevelDefaultFieldExtractorTest {

    private final Logger log = LoggerFactory.getLogger(TopLevelDefaultFieldExtractorTest.class);

    private TopLevelDefaultFieldExtractor topLevelDefaultFieldExtractor;
    private Class<Project> clazz;

    @Before
    public void setup() {
        topLevelDefaultFieldExtractor = new TopLevelDefaultFieldExtractor();
        clazz = Project.class;
    }

    @Test
    public void testAnnotationBasedExtraction() throws Exception {
        List<String> fieldNames = topLevelDefaultFieldExtractor.getFieldNames(clazz);
        log.debug("{}", fieldNames);
        assertTrue(fieldNames.contains("id"));
    }
}
