package org.respondeco.respondeco.service.exception;

/**
 * Created by clemens on 02/11/14.
 */
public class NoSuchOrganizationException extends Exception {

    public NoSuchOrganizationException(String message) {
        super(message);
    }

    public NoSuchOrganizationException(String message, Throwable t) {
        super(message, t);
    }

}
