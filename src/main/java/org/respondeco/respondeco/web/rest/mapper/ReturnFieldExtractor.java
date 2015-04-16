package org.respondeco.respondeco.web.rest.mapper;

import java.lang.reflect.Field;
import java.util.List;

/**
 * Created by clemens on 22/03/15.
 */
public interface ReturnFieldExtractor {

    public List<Field> extract(Class<?> clazz);

}
