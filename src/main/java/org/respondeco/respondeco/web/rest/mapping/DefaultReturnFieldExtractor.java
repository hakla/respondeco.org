package org.respondeco.respondeco.web.rest.mapping;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by clemens on 22/03/15.
 */
public class DefaultReturnFieldExtractor implements ReturnFieldExtractor {

    @Override
    public List<Field> extract(Class<?> clazz) {
        List<Field> fields = new ArrayList<>();
        Class<?> currentClass = clazz;
        while(currentClass != Object.class && currentClass != null) {
            for(Field field : currentClass.getDeclaredFields()) {
                if(field.getAnnotation(DefaultReturnField.class) != null) {
                    fields.add(field);
                }
            }
            currentClass = currentClass.getSuperclass();
        }
        return fields;
    }

}
