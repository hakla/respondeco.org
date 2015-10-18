package org.respondeco.respondeco.service.exception;

/**
 * Created by clemens on 02/11/14.
 */
public class SupporterRatingException extends RuntimeException {

    private static final String KEY = "rating.error";

    public SupporterRatingException(String key, String message) {
        super(message);
    }

    public SupporterRatingException(String key, String message, Throwable t) {
        super(message, t);
    }

}
