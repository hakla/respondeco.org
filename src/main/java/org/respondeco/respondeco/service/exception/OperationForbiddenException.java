package org.respondeco.respondeco.service.exception;

/**
 * Created by clemens on 18/11/14.
 */
public class OperationForbiddenException extends Exception {

    public OperationForbiddenException(String message) {
        super(message);
    }

    public OperationForbiddenException(String message, Throwable t) {
        super(message, t);
    }
}
