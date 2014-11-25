package org.respondeco.respondeco.service.exception;

/**
 * Created by clemens on 02/11/14.
 */
public class NotOwnerOfOrganizationException extends Exception {

    public NotOwnerOfOrganizationException(String message) {
        super(message);
    }

    public NotOwnerOfOrganizationException(String message, Throwable t) {
        super(message, t);
    }

}
