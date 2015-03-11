package org.respondeco.respondeco.web.rest.mapping;

import org.respondeco.respondeco.domain.AbstractAuditingEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by clemens on 11/03/15.
 */
public class TopLevelDefaultFieldExtractor implements FieldExtractor {

    private final Logger log = LoggerFactory.getLogger(TopLevelDefaultFieldExtractor.class);

    public List<String> getFieldNames(Class<?> clazz) {
        List<Field> fields = getAnnotatedFields(clazz);
        return fields.stream().map(f -> f.getName()).collect(Collectors.toList());
    }

    private List<Field> getAnnotatedFields(Class<?> clazz) {
        List<Field> superFields = null;
        if(clazz.getSuperclass() != null) {
            log.debug("scanning superclass for fields: {}", clazz.getSuperclass());
            superFields = getAnnotatedFields(clazz.getSuperclass());
        }
        List<Field> annotatedFields = new ArrayList<>();
        if(superFields != null) {
            annotatedFields.addAll(superFields);
        }
        for(Field field : clazz.getDeclaredFields()) {
            if(field.getAnnotation(DefaultReturnValue.class) != null) {
                annotatedFields.add(field);
            }
        }
        return annotatedFields;
    }

}
