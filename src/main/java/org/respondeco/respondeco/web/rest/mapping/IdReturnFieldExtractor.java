package org.respondeco.respondeco.web.rest.mapping;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by clemens on 22/03/15.
 */
public class IdReturnFieldExtractor implements ReturnFieldExtractor {

    @Override
    public List<Field> extract(Class<?> clazz) {
        List<Field> fields = new ArrayList<>();
        Class<?> currentClass = clazz;
        while(currentClass != Object.class) {
            for(Field field : currentClass.getDeclaredFields()) {
                if(field.getName().equals("id") && field.getAnnotation(DefaultReturnField.class) != null) {
                    fields.add(field);
                    return fields;
                }
            }
            currentClass = currentClass.getSuperclass();
        }
        return fields;
    }

}
