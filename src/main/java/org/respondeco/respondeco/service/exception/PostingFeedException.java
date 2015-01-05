package org.respondeco.respondeco.service.exception;

/**
 * Created by clemens on 02/11/14.
 */
public class PostingFeedException extends IllegalValueException {

    private static final String KEY = "postingfeed.error";

    public PostingFeedException(String key, String message) {
        super(KEY + key, message);
    }

    public PostingFeedException(String key, String message, Throwable t) {
        super(KEY + key, message, t);
    }

}
