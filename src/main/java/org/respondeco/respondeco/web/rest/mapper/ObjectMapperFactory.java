package org.respondeco.respondeco.web.rest.mapper;

/**
 * Created by clemens on 13/04/15.
 */
public interface ObjectMapperFactory {

    public ObjectMapper createMapper(String expression) throws MappingException;

}
