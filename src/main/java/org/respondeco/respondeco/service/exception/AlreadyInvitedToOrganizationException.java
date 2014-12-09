package org.respondeco.respondeco.service.exception;

/**
 * Created by clemens on 02/11/14.
 */
public class AlreadyInvitedToOrganizationException extends Exception {

    public AlreadyInvitedToOrganizationException(String message) {
        super(message);
    }

    public AlreadyInvitedToOrganizationException(String message, Throwable t) {
        super(message, t);
    }

}
