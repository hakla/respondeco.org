package org.respondeco.respondeco.testutil.domain;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;

/**
 * Created by clemens on 18/08/15.
 */
public class EntityFactory<E> {

    protected E setNull(E entity, String[] fields) throws InvocationTargetException, IllegalAccessException {
        for(String name : fields) {
            String setterName = "set" + name.substring(0,1).toUpperCase() + name.substring(1);
            for(Method method : entity.getClass().getMethods()) {
                if(method.getName().equals(setterName)) {
                    method.invoke(entity, null);
                    break;
                }
            }
        }
        return entity;
    }

}
