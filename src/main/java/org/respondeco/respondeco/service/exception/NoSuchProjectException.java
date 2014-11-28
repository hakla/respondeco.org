package org.respondeco.respondeco.service.exception;

/**
 * Created by clemens on 28/11/14.
 */
public class NoSuchProjectException extends Exception {

    public NoSuchProjectException(String message) {
        super(message);
    }

    public NoSuchProjectException(String message, Throwable t) {
        super(message, t);
    }
}
