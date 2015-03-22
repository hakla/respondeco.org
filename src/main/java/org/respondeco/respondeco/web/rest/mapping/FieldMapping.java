package org.respondeco.respondeco.web.rest.mapping;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.ToString;
import org.json4s.jackson.Json;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by clemens on 22/03/15.
 */
@ToString(exclude = { "util", "accessor" })
public class FieldMapping {

    private final Logger log = LoggerFactory.getLogger(FieldMapping.class);

    private ReflectionUtil util;
    private Field field;
    private Method accessor;
    private JsonSerializer serializer = null;

    public FieldMapping(Class<?> clazz, String fieldName)
        throws NoSuchFieldException, NoSuchMethodException, InstantiationException, IllegalAccessException {
        this.util = new ReflectionUtil();
        this.field = util.getField(clazz, fieldName);
        checkAccess();
        initSerializer();
        this.accessor = util.getAccessor(clazz, fieldName);
    }

    public String getFieldName() {
        return field.getName();
    }

    public Object map(Object object) throws InvocationTargetException, IllegalAccessException {
        return accessor.invoke(object);
    }

    private void checkAccess() throws NoSuchFieldException {
        if(util.hasAnnotation(field, JsonIgnore.class)) {
            throw new NoSuchFieldException(field.getName());
        }
    }

    private void initSerializer() throws NoSuchFieldException, IllegalAccessException, InstantiationException {
        Annotation annotation = util.getAnnotation(field, JsonSerialize.class);
        if(annotation != null) {
            JsonSerialize serialize = (JsonSerialize) annotation;
            serializer = serialize.using().newInstance();
        }
    }

    private Object serialize(Object object) {
        if(serializer != null) {
            //return
        }
        return object;
    }

}
