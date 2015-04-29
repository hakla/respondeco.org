package org.respondeco.respondeco.web.rest.mapping;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.List;

/**
 * Created by clemens on 22/03/15.
 */
public class NestedFieldMapping extends FieldMapping {

    private ObjectMapper childMapper;

    public NestedFieldMapping(String fieldName, Field field, Method accessor, ObjectMapper childMapper)
            throws MappingException {
        super(fieldName, field, accessor);
        this.childMapper = childMapper;
    }

    @Override
    public Object map(Object object) throws MappingException {
        Object child = super.map(object);
        if(child == null) {
            return null;
        }

        // if the returned value is an iterable type we need to check if the contained element is one of our own
        if (child instanceof List) {
            List list = (List) child;

            return list.stream().map(entry -> childMapper.map(entry)).toArray();
        }

        return childMapper.map(child);
    }

}
