package org.respondeco.respondeco.service.exception;

/**
 * Created by clemens on 02/11/14.
 */
public class NoSuchResourceMatchException extends IllegalValueException {

    private static final String KEY = "resourcematch.error.nosuchmatch";
    private static final String BASE_MESSAGE = "no such match: ";

    public NoSuchResourceMatchException(String message) {
        super(KEY, message);
    }

    public NoSuchResourceMatchException(String message, Throwable t) {
        super(KEY, message, t);
    }

}
