package org.respondeco.respondeco.web.rest.mapping;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.core.JsonEncoding;
import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.ToString;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayOutputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Created by Clemens Puehringer on 22/03/15.
 * Takes a field or field name of a class and maps it to its value for a specific object.
 * Uses ReflectionUtil#getAccessor(Class<T>,String) to get the accessor for the field and
 * returns the return value of the accessor as the result of map(Object)
 */
@ToString(exclude = {"util", "accessor"})
public class FieldMapping {

    private final Logger log = LoggerFactory.getLogger(FieldMapping.class);

    private ReflectionUtil util;
    private String returnFieldName;
    private String fieldName;
    private Field field;
    private Method accessor;

    public FieldMapping(String fieldName, Field field, Method accessor, String returnFieldName) throws MappingException {
        this.util = new ReflectionUtil();
        this.returnFieldName = returnFieldName;
        this.fieldName = fieldName;
        this.field = field;
        this.accessor = accessor;
        checkAccess();
    }

    public FieldMapping(String fieldName, Field field, Method accessor) throws MappingException {
        this(fieldName, field, accessor, fieldName);
    }

    public String getReturnFieldName() {
        return returnFieldName;
    }

    public String getFieldName() {
        return fieldName;
    }

    public Object map(Object object) throws MappingException {
        try {
            log.debug("field name: {}, field: {}, accessor: {}", fieldName, field, accessor);
            return accessor.invoke(object);
        } catch (Exception e) {
            throw new MappingException(e);
        }
    }

    private void checkAccess() throws MappingException {
        try {
            if(field != null) {
                if(util.hasAnnotation(field, JsonIgnore.class)) {
                    throw new NoSuchFieldException(field.getName());
                }
            }
            if(util.hasAnnotation(accessor, JsonIgnore.class)) {
                throw new NoSuchMethodException(accessor.getName());
            }
        } catch (Exception e) {
            throw new MappingException(e);
        }
    }

}
