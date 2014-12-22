package org.respondeco.respondeco.service.exception;

/**
 * Created by clemens on 02/11/14.
 */
public class PostingException extends IllegalValueException {

    private static final String KEY = "posting.error";

    public PostingException(String key, String message) {
        super(KEY + key, message);
    }

    public PostingException(String key, String message, Throwable t) {
        super(KEY + key, message, t);
    }

}
