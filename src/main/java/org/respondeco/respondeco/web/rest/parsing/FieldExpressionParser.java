package org.respondeco.respondeco.web.rest.parsing;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by clemens on 22/03/15.
 */
public class FieldExpressionParser {

    public static interface ParserListener {
        public void onNegatedExpression(String name) throws ExpressionParsingException;
        public void onSimpleExpression(String name) throws ExpressionParsingException;
        public void onNestedExpression(String name, FieldExpressionParser subParser) throws ExpressionParsingException;
    }

    private static final Logger log = LoggerFactory.getLogger(FieldExpressionParser.class);

    private static final Integer DEFAULT_MAX_LEVEL = 5;

    private ParserListener listener;
    private String expression;

    private Integer maxLevel = Integer.MAX_VALUE;
    private Integer currentLevel = 0;
    private Boolean canGoDeeper = true;

    public FieldExpressionParser(String expression) {
        this(DEFAULT_MAX_LEVEL, 0, expression);
    }

    public FieldExpressionParser(Integer maxLevel, Integer currentLevel, String expression) {
        this.maxLevel = maxLevel;
        this.currentLevel = currentLevel;
        this.canGoDeeper = currentLevel < maxLevel;
        this.expression = expression;
    }

    public void setParserListener(ParserListener listener) {
        this.listener = listener;
    }

    public void parse() throws ExpressionParsingException {
        String currentString = expression;
        while(currentString.length() > 0) {
            currentString = consumeToken(currentString);
            currentString = consumeMaybeComma(currentString);
        }
    }

    private String consumeToken(String string) throws ExpressionParsingException {
        Integer nextComma = string.indexOf(',');
        Integer nextOpeningBracket = string.indexOf('(');
        //last expression in the string
        if(nextComma == -1) {
            if(nextOpeningBracket == -1) {
                //last expression is a simple expression
                return consumeSimpleToken(string, string.length());
            } else {
                //last expression is a complex expression
                return consumeComplexToken(string, nextOpeningBracket);
            }
        } else if(nextOpeningBracket == -1 || nextComma < nextOpeningBracket) {
            //next expression is a simple one
            //if nextOpeningBracket == -1, there are no more complex expressions in the string
            return consumeSimpleToken(string, nextComma);
        } else {
            //expression has to be a complex one because the next "(" comes before the next ","
            return consumeComplexToken(string, nextOpeningBracket);
        }
    }

    private String consumeSimpleToken(String string, Integer end) throws ExpressionParsingException {
        String simpleToken = string.substring(0, end);
        if(simpleToken.startsWith("-")) {
            listener.onNegatedExpression(simpleToken.substring(1));
        } else if(simpleToken.startsWith("+")) {
            listener.onSimpleExpression(simpleToken.substring(1));
        } else {
            listener.onSimpleExpression(simpleToken);
        }
        return string.substring(end);
    }

    private String consumeComplexToken(String string, Integer openingBracketIndex) throws ExpressionParsingException {
        failIfTooDeep();
        if(string.startsWith("-")) {
            throw new MalformedExpressionException(
                "minus operator (-) not supported for complex expressions: " + string);
        }
        String fieldName = string.substring(0, openingBracketIndex);
        Integer matchingClosingBracket = getMatchingBracket(string, openingBracketIndex);
        String childExpression = string.substring(openingBracketIndex + 1, matchingClosingBracket);
        FieldExpressionParser childParser = new FieldExpressionParser(maxLevel, currentLevel + 1, childExpression);
        listener.onNestedExpression(fieldName, childParser);
        return string.substring(matchingClosingBracket + 1);
    }

    private String consumeMaybeComma(String string) throws ExpressionParsingException {
        if(string.length() <= 0) {
            return string;
        } else {
            if(string.charAt(0) == ',') {
                return string.substring(1);
            } else {
                throw new ExpressionParsingException("expected comma, but got {}" + string.charAt(0));
            }
        }
    }

    private Integer getMatchingBracket(String str, Integer bracketIndex) throws MalformedExpressionException {
        Integer bracketCount = 1;
        Integer currentStringIndex = bracketIndex + 1;
        while(currentStringIndex < str.length()) {
            if(str.charAt(currentStringIndex) == ')') {
                bracketCount = bracketCount - 1;
                if(bracketCount == 0) {
                    break;
                }
            } else if(str.charAt(currentStringIndex) == '(') {
                bracketCount = bracketCount + 1;
            }
            currentStringIndex = currentStringIndex + 1;
        }
        if(bracketCount != 0) {
            throw new MalformedExpressionException("brackets not balanced: " + str);
        }
        return currentStringIndex;
    }

    private void failIfTooDeep() throws ExpressionDepthException {
        if(!canGoDeeper) {
            throw new ExpressionDepthException("maximum depth of " + maxLevel + " reached");
        }
    }

}
