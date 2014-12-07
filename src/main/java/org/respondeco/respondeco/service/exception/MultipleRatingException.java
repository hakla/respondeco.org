package org.respondeco.respondeco.service.exception;

/**
 * Created by clemens on 02/11/14.
 */
public class MultipleRatingException extends Exception {

    public MultipleRatingException(String message) {
        super(message);
    }

    public MultipleRatingException(String message, Throwable t) {
        super(message, t);
    }

}
