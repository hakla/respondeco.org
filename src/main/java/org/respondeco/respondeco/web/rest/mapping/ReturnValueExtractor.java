package org.respondeco.respondeco.web.rest.mapping;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.List;

/**
 * Created by clemens on 22/03/15.
 */
public interface ReturnValueExtractor {

    public List<Field> extractFields(Class<?> clazz);

    public List<Method> extractMethods(Class<?> clazz);

}
