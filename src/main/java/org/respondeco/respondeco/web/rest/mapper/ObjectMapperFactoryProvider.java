package org.respondeco.respondeco.web.rest.mapper;

/**
 * Created by clemens on 13/04/15.
 */
public class ObjectMapperFactoryProvider {

    public ObjectMapperFactory getMapperFactory(Class<?> clazz) {
        return new CachingObjectMapperFactory(clazz);
    }

}
