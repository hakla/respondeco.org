package org.respondeco.respondeco.web.rest.mapping;

import java.util.List;

/**
 * Created by clemens on 11/03/15.
 */
public interface FieldExtractor {

    public List<String> getFieldNames(Class<?> clazz);

}
