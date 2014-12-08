package org.respondeco.respondeco.service.exception;

/**
 * Created by clemens on 02/11/14.
 */
public class NoSuchResourceMatchException extends Exception {

    public NoSuchResourceMatchException(String message) {
        super(message);
    }

    public NoSuchResourceMatchException(String message, Throwable t) {
        super(message, t);
    }

}
