package org.respondeco.respondeco.service.exception;

/**
 * If a Connection for the specified user and provider already exists
 */
public class ConnectionAlreadyExistsException extends Exception {

    public ConnectionAlreadyExistsException(String message) {
        super(message);
    }

    public ConnectionAlreadyExistsException(String message, Throwable t) {
        super(message, t);
    }

}
