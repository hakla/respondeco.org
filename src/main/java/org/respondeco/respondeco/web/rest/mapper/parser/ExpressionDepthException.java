package org.respondeco.respondeco.web.rest.mapper.parser;

/**
 * Created by clemens on 22/03/15.
 */
public class ExpressionDepthException extends ExpressionParsingException {

    public ExpressionDepthException(String message, Throwable e) {
        super(message, e);
    }

    public ExpressionDepthException(String message) {
        super(message);
    }

}
