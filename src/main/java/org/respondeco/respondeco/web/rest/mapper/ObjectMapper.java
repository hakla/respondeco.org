package org.respondeco.respondeco.web.rest.mapper;

import java.util.Map;

/**
 * Created by clemens on 24/03/15.
 */
public interface ObjectMapper {

    public Map<String, Object> map(Object object) throws MappingException;

}
