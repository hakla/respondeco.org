package org.respondeco.respondeco.web.rest.mapping;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;

/**
 * Created by clemens on 22/03/15.
 */
public interface ReturnValueExtractor {

    public Map<Field, DefaultReturnValue> extractFields(Class<?> clazz);

    public Map<Method, DefaultReturnValue> extractMethods(Class<?> clazz);

}
