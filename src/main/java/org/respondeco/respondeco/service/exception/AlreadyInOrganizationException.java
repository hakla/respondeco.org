package org.respondeco.respondeco.service.exception;

/**
 * Created by clemens on 02/11/14.
 */
public class AlreadyInOrganizationException extends Exception {

    public AlreadyInOrganizationException(String message) {
        super(message);
    }

    public AlreadyInOrganizationException(String message, Throwable t) {
        super(message, t);
    }

}
