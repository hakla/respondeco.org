package org.respondeco.respondeco.web.rest.mapping;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by clemens on 22/03/15.
 */
public class DefaultReturnValueExtractor implements ReturnValueExtractor {

    @Override
    public Map<Field, DefaultReturnValue> extractFields(Class<?> clazz) {
        Map<Field, DefaultReturnValue> fields = new HashMap<>();
        Class<?> currentClass = clazz;
        while(currentClass != Object.class && currentClass != null) {
            for(Field field : currentClass.getDeclaredFields()) {
                DefaultReturnValue annotation = field.getAnnotation(DefaultReturnValue.class);
                if(annotation != null) {
                    fields.put(field, annotation);
                }
            }
            currentClass = currentClass.getSuperclass();
        }
        return fields;
    }

    @Override
    public Map<Method, DefaultReturnValue> extractMethods(Class<?> clazz) {
        Map<Method, DefaultReturnValue> methods = new HashMap<>();
        Class<?> currentClass = clazz;
        while(currentClass != Object.class && currentClass != null) {
            for(Method method : currentClass.getDeclaredMethods()) {
                DefaultReturnValue annotation = method.getAnnotation(DefaultReturnValue.class);
                if(annotation != null) {
                    methods.put(method, annotation);
                }
            }
            currentClass = currentClass.getSuperclass();
        }
        return methods;
    }

}
