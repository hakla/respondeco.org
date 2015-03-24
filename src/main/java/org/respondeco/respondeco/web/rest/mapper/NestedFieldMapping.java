package org.respondeco.respondeco.web.rest.mapper;

import java.lang.reflect.Field;

/**
 * Created by clemens on 22/03/15.
 */
public class NestedFieldMapping extends FieldMapping {

    private ObjectMapper childMapper;

    public NestedFieldMapping(Class<?> clazz, String fieldName, ObjectMapper childMapper) throws MappingException {
        super(clazz, fieldName);
        this.childMapper = childMapper;
    }

    public NestedFieldMapping(Class<?> clazz, Field field, ObjectMapper childMapper) throws MappingException {
        super(clazz, field);
        this.childMapper = childMapper;
    }

    @Override
    public Object map(Object object) throws MappingException {
        Object child = super.map(object);
        if(child == null) {
            return null;
        }
        return childMapper.map(child);
    }

}
