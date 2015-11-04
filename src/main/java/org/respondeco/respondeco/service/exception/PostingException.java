package org.respondeco.respondeco.service.exception;

/**
 * Created by clemens on 02/11/14.
 */
public class PostingException extends RuntimeException {

    private static final String KEY = "posting.error";

    public PostingException(String key, String message) {
        super(message);
    }

    public PostingException(String key, String message, Throwable t) {
        super(message, t);
    }

}
