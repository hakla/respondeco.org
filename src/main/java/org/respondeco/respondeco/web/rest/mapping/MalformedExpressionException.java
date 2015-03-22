package org.respondeco.respondeco.web.rest.mapping;

/**
 * Created by clemens on 22/03/15.
 */
public class MalformedExpressionException extends Exception {

    public MalformedExpressionException(Throwable e, String message) {
        super(message, e);
    }

    public MalformedExpressionException(String message) {
        super(message);
    }

}
