package org.respondeco.respondeco.service.exception;

/**
 * Created by clemens on 02/11/14.
 */
public class AlreadyInOrganizationException extends IllegalValueException {

    private static final String KEY = "organization.error.allreadyinorg";

    public AlreadyInOrganizationException(String message) {
        super(KEY,message);
    }

    public AlreadyInOrganizationException(String message, Throwable t) {
        super(KEY, message, t);
    }

}
