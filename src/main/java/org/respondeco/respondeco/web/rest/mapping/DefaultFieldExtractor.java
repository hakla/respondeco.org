package org.respondeco.respondeco.web.rest.mapping;

import org.respondeco.respondeco.domain.AbstractAuditingEntity;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by clemens on 11/03/15.
 */
public class DefaultFieldExtractor implements FieldExtractor {

    private static final List<String> DEFAULT_FIELDS = Arrays.asList("id");

    public List<String> getFieldNames(Class<?> clazz) {
        return DEFAULT_FIELDS;
    }

}
