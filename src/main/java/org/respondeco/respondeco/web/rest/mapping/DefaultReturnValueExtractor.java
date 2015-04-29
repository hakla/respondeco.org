package org.respondeco.respondeco.web.rest.mapping;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by clemens on 22/03/15.
 */
public class DefaultReturnValueExtractor implements ReturnValueExtractor {

    @Override
    public List<Field> extractFields(Class<?> clazz) {
        List<Field> fields = new ArrayList<>();
        Class<?> currentClass = clazz;
        while(currentClass != Object.class && currentClass != null) {
            for(Field field : currentClass.getDeclaredFields()) {
                if(field.isAnnotationPresent(DefaultReturnValue.class)) {
                    fields.add(field);
                }
            }
            currentClass = currentClass.getSuperclass();
        }
        return fields;
    }

    @Override
    public List<Method> extractMethods(Class<?> clazz) {
        List<Method> methods = new ArrayList<>();
        Class<?> currentClass = clazz;
        while(currentClass != Object.class && currentClass != null) {
            for(Method method : currentClass.getDeclaredMethods()) {
                if(method.isAnnotationPresent(DefaultReturnValue.class)) {
                    methods.add(method);
                }
            }
            currentClass = currentClass.getSuperclass();
        }
        return methods;
    }

}
