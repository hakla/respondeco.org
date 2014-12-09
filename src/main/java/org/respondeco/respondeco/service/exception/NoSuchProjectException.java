package org.respondeco.respondeco.service.exception;

/**
 * Created by clemens on 28/11/14.
 */
public class NoSuchProjectException extends IllegalValueException {

    private static final String KEY = "project.error.nosuchproject";
    private static final String BASE_MESSAGE = "no such project: ";

    public NoSuchProjectException(Long id) {
        super(KEY, BASE_MESSAGE + id);
    }

    public NoSuchProjectException(Long id, Throwable t) {
        super(KEY, BASE_MESSAGE + id, t);
    }

    public NoSuchProjectException(String message) {
        super(KEY, message);
    }
}
