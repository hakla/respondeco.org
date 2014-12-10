package org.respondeco.respondeco.service.exception;

/**
 * Created by Benjamin Fraller on 09.12.2014.
 */
public class NoSuchResourceMatchException extends IllegalValueException {

    private static final String KEY = "resourcematch.error.nosuchmatch";
    private static final String BASE_MESSAGE = "no such resourcematch: ";

    public NoSuchResourceMatchException(Long id) {
        super(KEY, BASE_MESSAGE + id);
    }

    public NoSuchResourceMatchException(Long id, Throwable t) {
        super(KEY, BASE_MESSAGE + id, t);
    }
}
