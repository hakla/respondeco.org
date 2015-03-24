package org.respondeco.respondeco.web.rest.mapper.parser;

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
            log.debug("parsing \"{}\"", currentString);
            Integer nextOpeningBracket = currentString.indexOf('(');
            Integer nextComma = currentString.indexOf(',');
            // because of the mandatory comma after ) e.g. "foo(bar),next"
            log.debug("next comma is at {}", nextComma);
            if(nextComma == 0) {
                currentString = currentString.substring(1);
                nextComma = currentString.indexOf(',');
                nextOpeningBracket = nextOpeningBracket - 1;
                log.debug("omitted comma, next comma is at {}", nextComma);
            }
            if(nextOpeningBracket >= 0) {
                if (nextComma >= 0 && nextComma < nextOpeningBracket) {
                    String simpleToken = currentString.substring(0, nextComma);
                    if(simpleToken.startsWith("-")) {
                        listener.onNegatedExpression(simpleToken.substring(1));
                    } else if(simpleToken.startsWith("+")) {
                        listener.onSimpleExpression(simpleToken.substring(1));
                    } else {
                        listener.onSimpleExpression(simpleToken);
                    }
                    currentString = currentString.substring(nextComma + 1);
                } else {
                    String fieldName = currentString.substring(0, nextOpeningBracket);
                    if(fieldName.startsWith("-")) {
                        throw new MalformedExpressionException("negations not supported on nested expressions: " +
                            currentString);
                    } else if(fieldName.startsWith("+")) {
                        fieldName = fieldName.substring(1);
                    }
                    Integer matchingBracket = getMatchingBracket(currentString, nextOpeningBracket);
                    failIfTooDeep();
                    String childExpression =
                        currentString.substring(nextOpeningBracket + 1, matchingBracket);
                    FieldExpressionParser childParser =
                        new FieldExpressionParser(maxLevel, (currentLevel + 1), childExpression);
                    listener.onNestedExpression(fieldName, childParser);
                    currentString = currentString.substring(matchingBracket + 1);
                }
            } else {
                //no more complex expressions
                log.debug("no complex expressions in \"{}\"", currentString);
                for(String simpleToken : currentString.split(",")) {
                    if(simpleToken.length() > 0) {
                        if (simpleToken.startsWith("-")) {
                            listener.onNegatedExpression(simpleToken.substring(1));
                        } else if (simpleToken.startsWith("+")) {
                            listener.onSimpleExpression(simpleToken.substring(1));
                        } else {
                            listener.onSimpleExpression(simpleToken);
                        }
                    }
                }
                break;
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
