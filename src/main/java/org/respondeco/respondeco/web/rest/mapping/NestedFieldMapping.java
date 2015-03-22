package org.respondeco.respondeco.web.rest.mapping;

import java.lang.reflect.InvocationTargetException;

/**
 * Created by clemens on 22/03/15.
 */
public class NestedFieldMapping extends FieldMapping {

    private ObjectMapper childMapper;

    public NestedFieldMapping(Class<?> clazz, String fieldName, ObjectMapper childMapper)
        throws NoSuchFieldException, NoSuchMethodException {
        super(clazz, fieldName);
        this.childMapper = childMapper;
    }

    @Override
    public Object map(Object object) throws InvocationTargetException, IllegalAccessException {
        Object child = super.map(object);
        return childMapper.map(child);
    }

}
