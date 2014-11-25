package org.respondeco.respondeco.service.exception;

/**
 * Created by clemens on 02/11/14.
 */
public class NoSuchOrgJoinRequestException extends Exception {

    public NoSuchOrgJoinRequestException(String message) {
        super(message);
    }

    public NoSuchOrgJoinRequestException(String message, Throwable t) {
        super(message, t);
    }

}
