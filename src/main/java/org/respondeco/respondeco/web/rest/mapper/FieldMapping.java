package org.respondeco.respondeco.web.rest.mapper;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.ToString;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

/**
 * Created by Clemens Puehringer on 22/03/15.
 * Takes a field or field name of a class and maps it to its value for a specific object.
 * Uses ReflectionUtil#getAccessor(Class<T>,String) to get the accessor for the field and
 * returns the return value of the accessor as the result of map(Object)
 */
@ToString(exclude = { "util", "accessor" })
public class FieldMapping {

    private final Logger log = LoggerFactory.getLogger(FieldMapping.class);

    private ReflectionUtil util;
    private Class<?> clazz;
    private Field field;
    private Method accessor;
    private JsonSerializer serializer = null;

    public FieldMapping(Class<?> clazz, String fieldName) throws MappingException {
        this.clazz = clazz;
        this.util = new ReflectionUtil();
        initField(fieldName);
        checkAccess();
        initSerializer();
        initAccessor();
    }

    public FieldMapping(Class<?> clazz, Field field) throws MappingException {
        this.clazz = clazz;
        this.util = new ReflectionUtil();
        this.field = field;
        checkAccess();
        initSerializer();
        initAccessor();
    }

    public String getFieldName() {
        return field.getName();
    }

    public Object map(Object object) throws MappingException {
        try {
            return accessor.invoke(object);
        } catch (Exception e) {
            throw new MappingException(e);
        }
    }

    private void checkAccess() throws MappingException {
        try {
            if(util.hasAnnotation(field, JsonIgnore.class)) {
                throw new NoSuchFieldException(field.getName());
            }
        } catch (NoSuchFieldException e) {
            throw new MappingException(e);
        }
    }

    private void initField(String name) throws MappingException {
        try {
            this.field = util.getField(clazz, name);
        } catch (Exception e) {
            throw new MappingException(e);
        }
    }

    private void initAccessor() throws MappingException {
        try {
            this.accessor = util.getAccessor(clazz, field.getName());
        } catch (Exception e) {
            throw new MappingException(e);
        }
    }

    /**
     * not yet working
     * @throws MappingException
     */
    private void initSerializer() throws MappingException {
        try {
            Annotation annotation = util.getAnnotation(field, JsonSerialize.class);
            if(annotation != null) {
                JsonSerialize serialize = (JsonSerialize) annotation;
                serializer = serialize.using().newInstance();
            }
        } catch (Exception e) {
            throw new MappingException(e);
        }
    }

}
