package org.respondeco.respondeco.service.exception;

/**
 * Created by clemens on 02/11/14.
 */
public class PostingFeedException extends RuntimeException {

    private static final String KEY = "postingfeed.error";

    public PostingFeedException(String key, String message) {
        super(message);
    }

    public PostingFeedException(String key, String message, Throwable t) {
        super(message, t);
    }

}
