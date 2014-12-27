package org.respondeco.respondeco.service.exception;

/**
 * Created by clemens on 02/11/14.
 */
public class OrganizationAlreadyExistsException extends IllegalValueException {

    private static final String KEY = "organization.error.alreadyexists";

    public OrganizationAlreadyExistsException(String message) {
        super(KEY, message);
    }

    public OrganizationAlreadyExistsException(String message, Throwable t) {
        super(KEY, message, t);
    }

}
