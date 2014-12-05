package org.respondeco.respondeco.service.exception;

/**
 * Created by clemens on 02/11/14.
 */
public class NoSuchSupporterRatingException extends Exception {

    public NoSuchSupporterRatingException(String message) {
        super(message);
    }

    public NoSuchSupporterRatingException(String message, Throwable t) {
        super(message, t);
    }

}
