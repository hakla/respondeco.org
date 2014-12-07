package org.respondeco.respondeco.service.exception;

/**
 * Created by clemens on 02/11/14.
 */
public class SupporterRatingException extends IllegalValueException {

    private static final String KEY = "rating.error";

    public SupporterRatingException(String key, String message) {
        super(KEY + key, message);
    }

    public SupporterRatingException(String key, String message, Throwable t) {
        super(KEY + key, message, t);
    }

}
