package org.respondeco.respondeco.service.exception;

/**
 * Created by clemens on 02/11/14.
 */
public class NoSuchOrganizationException extends IllegalValueException {

    private static final String KEY = "organization.error.nosuchorganization";
    private static final String BASE_MESSAGE = "no such organization: ";

    public NoSuchOrganizationException(Long id) {
        super(KEY, BASE_MESSAGE + id);
    }

    public NoSuchOrganizationException(Long id, Throwable t) {
        super(KEY, BASE_MESSAGE + id, t);
    }

    public NoSuchOrganizationException(String message) {
        super(KEY, message);
    }

}
