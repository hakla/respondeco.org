package org.respondeco.respondeco.web.rest.dto.util;

import java.lang.reflect.InvocationTargetException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Clemens Puehringer on 04/12/14.
 */
public class ReflectiveDTOMapper<O, T> {

    private static final Map<Class<?>, Class<?>> DTOsForClass;

    static {
        DTOsForClass = new HashMap<>();
    }

    private Class<T> targetClass;

    public ReflectiveDTOMapper(Class<T> targetClass) {
        this.targetClass = targetClass;
    }

    public T map(O entity, Collection<String> fields) throws IllegalAccessException,
        InstantiationException, NoSuchMethodException, InvocationTargetException {
        T target = targetClass.newInstance();
        for(String field : fields) {
            String[] path = field.split("#");
            Object value = getProperty(entity, path, 0);
            String targetSetter = getTargetSetter(path);
            targetClass.getMethod(targetSetter, null).invoke(target, value);
        }
        return target;
    }

    private Object resolveTargetField(O entity, String fieldName) throws NoSuchMethodException {
        String[] path = fieldName.split("#");
        return getProperty(entity, path, 0);
    }

    private Object getProperty(Object object, String[] path, int pathIndex) throws NoSuchMethodException {
        String getter = "get" + String.valueOf(path[pathIndex].charAt(0)).toUpperCase() + path[pathIndex].substring(1);
        Object property = object.getClass().getMethod(getter, null);
        if(pathIndex < (path.length - 1)) {
            return getProperty(property, path, (pathIndex + 1));
        }
        return property;
    }

    private String getTargetSetter(String[] path) {
        StringBuilder targetSetter = new StringBuilder("set");
        for(String s : path) {
            s = s.toLowerCase();
            char firstChar = Character.toUpperCase(s.charAt(0));
            targetSetter.append(firstChar);
            targetSetter.append(s.substring(1));
        }
        return targetSetter.toString();
    }

}
