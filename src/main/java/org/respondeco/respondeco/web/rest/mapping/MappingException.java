package org.respondeco.respondeco.web.rest.mapping;

/**
 * Created by clemens on 24/03/15.
 */
public class MappingException extends RuntimeException {

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
