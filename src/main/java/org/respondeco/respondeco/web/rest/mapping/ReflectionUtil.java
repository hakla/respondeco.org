package org.respondeco.respondeco.web.rest.mapping;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

/**
 * Created by clemens on 22/03/15.
 */
public class ReflectionUtil {

    private final Logger log = LoggerFactory.getLogger(ReflectionUtil.class);

    public Method getAccessor(Class<?> clazz, String fieldName) throws NoSuchFieldException, NoSuchMethodException {
        try {
            return getStandardAccessor(clazz, fieldName);
        } catch (NoSuchMethodException e) {
            log.info("no standard accessor (get) for {}.{} found, trying boolean (is) accessor",
                clazz.getSimpleName(), fieldName);
        }
        return getBooleanAccessor(clazz, fieldName);
    }

    public Method getStandardAccessor(Class<?> clazz, String fieldName) throws NoSuchMethodException {
        String accessorName = "get" + fieldName.substring(0,1).toUpperCase() + fieldName.substring(1);
        return getMethod(clazz, accessorName);
    }

    public Method getBooleanAccessor(Class<?> clazz, String fieldName) throws NoSuchMethodException {
        String accessorName = "is" + fieldName.substring(0,1).toUpperCase() + fieldName.substring(1);
        return getMethod(clazz, accessorName);
    }

    public Field getField(Class<?> clazz, String fieldName) throws NoSuchFieldException {
        Class<?> currentClass = clazz;
        Field field;
        while(currentClass != Object.class) {
            try {
                field = currentClass.getDeclaredField(fieldName);
                return field;
            } catch (NoSuchFieldException e) {
                currentClass = currentClass.getSuperclass();
            }
        }
        throw new NoSuchFieldException(clazz.getSimpleName() + "." + fieldName);
    }

    public Class<?> getFieldClass(Class<?> clazz, String fieldName) throws NoSuchFieldException {
        return getField(clazz, fieldName).getType();
    }

    public Method getMethod(Class<?> clazz, String methodName) throws NoSuchMethodException {
        Class<?> currentClass = clazz;
        while(currentClass != Object.class) {
            try {
                return currentClass.getMethod(methodName);
            }catch (NoSuchMethodException e) {
                currentClass = currentClass.getSuperclass();
            }
        }
        throw new NoSuchMethodException(methodName);
    }

    public Boolean hasAnnotation(Field field, Class<? extends Annotation> annotationClass) {
        return field.isAnnotationPresent(annotationClass);
    }

    public Boolean hasAnnotation(Method method, Class<? extends Annotation> annotationClass) {
        return method.isAnnotationPresent(annotationClass);
    }

    public Annotation getAnnotation(Field field, Class<? extends Annotation> annotationClass)
        throws NoSuchFieldException {
        return field.getAnnotation(annotationClass);
    }

}
