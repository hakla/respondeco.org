package org.respondeco.respondeco.service.exception;

/**
 * Created by clemens on 23/12/14.
 */
public class OrganizationNotVerifiedException extends IllegalValueException {

    private static final String KEY = "organization.error.notverified";
    private static final String MESSAGE = "The authenticity of your organization (id: %d) is not verified yet." +
        " Please wait for the respondeco.org team to verify you organization";

    public OrganizationNotVerifiedException(Long id) {
        super(KEY, String.format(MESSAGE, id));
    }

    public OrganizationNotVerifiedException(Long id, Throwable t) {
        super(KEY, String.format(MESSAGE, id), t);
    }
}
