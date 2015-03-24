package org.respondeco.respondeco.web.rest.mapper;

/**
 * Created by clemens on 24/03/15.
 */
public class MappingException extends Exception {

    public MappingException(String message, Throwable e) {
        super(message, e);
    }

    public MappingException(String message) {
        super(message);
    }

    public MappingException(Throwable e) {
        super(e);
    }

}
