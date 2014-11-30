package org.respondeco.respondeco.service.exception;

/**
 * Created by clemens on 02/11/14.
 */
public class NoSuchUserException extends IllegalValueException {

    private static final String KEY = "error.user.nosuchuser";

    public NoSuchUserException(String message) {
        super(KEY, message);
    }

    public NoSuchUserException(String message, Throwable t) {
        super(KEY, message, t);
    }

}
