package org.respondeco.respondeco.service.exception;

/**
 * Created by clemens on 02/11/14.
 */
public class NoSuchEntityException extends IllegalValueException {

    private static final String KEY = "organization.error.nosuchentity";
    private static final String BASE_MESSAGE = "no such entity: ";

    public NoSuchEntityException(Long id) {
        super(KEY, BASE_MESSAGE + id);
    }

    public NoSuchEntityException(Long id, Throwable t) {
        super(KEY, BASE_MESSAGE + id, t);
    }

    public NoSuchEntityException(String message) {
        super(KEY, message);
    }

    public NoSuchEntityException(String key, String message) {
        super(key, message);
    }

}
