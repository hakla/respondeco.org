package org.respondeco.respondeco.web.rest.mapper.parser;

/**
 * Created by clemens on 24/03/15.
 */
public class ExpressionParsingException extends RuntimeException {

    public ExpressionParsingException(String message, Throwable e) {
        super(message, e);
    }

    public ExpressionParsingException(String message) {
        super(message);
    }

    public ExpressionParsingException(Throwable e) {
        super(e);
    }

}
