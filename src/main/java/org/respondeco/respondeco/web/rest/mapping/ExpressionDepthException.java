package org.respondeco.respondeco.web.rest.mapping;

/**
 * Created by clemens on 22/03/15.
 */
public class ExpressionDepthException extends Exception {

    public ExpressionDepthException(Throwable e, String message) {
        super(message, e);
    }

    public ExpressionDepthException(String message) {
        super(message);
    }

}
