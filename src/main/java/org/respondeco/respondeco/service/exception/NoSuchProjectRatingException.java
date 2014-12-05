package org.respondeco.respondeco.service.exception;

/**
 * Created by clemens on 02/11/14.
 */
public class NoSuchProjectRatingException extends Exception {

    public NoSuchProjectRatingException(String message) {
        super(message);
    }

    public NoSuchProjectRatingException(String message, Throwable t) {
        super(message, t);
    }

}
