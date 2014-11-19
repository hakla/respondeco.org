package org.respondeco.respondeco.service.exception;

/**
 * Created by clemens on 02/11/14.
 */
public class OrganizationAlreadyExistsException extends Exception {

    public OrganizationAlreadyExistsException(String message) {
        super(message);
    }

    public OrganizationAlreadyExistsException(String message, Throwable t) {
        super(message, t);
    }

}
