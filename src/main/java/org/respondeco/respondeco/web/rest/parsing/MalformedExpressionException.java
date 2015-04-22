package org.respondeco.respondeco.web.rest.parsing;

/**
 * Created by clemens on 22/03/15.
 */
public class MalformedExpressionException extends ExpressionParsingException {

    public MalformedExpressionException(String message, Throwable e) {
        super(message, e);
    }

    public MalformedExpressionException(String message) {
        super(message);
    }

}
