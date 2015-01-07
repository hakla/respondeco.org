package org.respondeco.respondeco.service.exception;

/**
 * Created by clemens on 18/11/14.
 */
public class OperationForbiddenException extends IllegalValueException {

    //TODO CHANGE HERE!
    public OperationForbiddenException(String message) {
        super("operation.forbidden", message);
    }

    public OperationForbiddenException(String message, Throwable t) {
        super("operation.forbidden", message, t);
    }
}
