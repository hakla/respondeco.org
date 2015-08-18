package org.respondeco.respondeco.service.exception;

/**
 * Created by clemens on 18/11/14.
 */
public class OperationForbiddenException extends IllegalValueException {

    public OperationForbiddenException(String key, String message) {
        super(key, message);
    }

    public OperationForbiddenException(String message) {
        super("operation.forbidden", message);
    }

    public OperationForbiddenException(String message, Throwable t) {
        super("operation.forbidden", message, t);
    }
}
