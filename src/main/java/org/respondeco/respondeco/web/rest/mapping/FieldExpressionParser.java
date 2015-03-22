package org.respondeco.respondeco.web.rest.mapping;

import org.respondeco.respondeco.domain.AbstractAuditingEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Field;
import java.util.List;

/**
 * Created by clemens on 22/03/15.
 */
public class FieldExpressionParser {

    private final Logger log = LoggerFactory.getLogger(FieldExpressionParser.class);

    private ReflectionUtil util;
    private ReturnFieldExtractor extractor;

    private Integer maxLevel = Integer.MAX_VALUE;
    private Integer currentLevel = 0;
    private Boolean canGoDeeper = true;

    public FieldExpressionParser() {
        this(Integer.MAX_VALUE, 0, new DefaultReturnFieldExtractor());
    }

    public FieldExpressionParser(Integer maxLevel) {
        this(maxLevel, 0, new DefaultReturnFieldExtractor());
    }

    public FieldExpressionParser(Integer maxLevel, Integer currentLevel) {
        this(maxLevel, currentLevel, new DefaultReturnFieldExtractor());
    }

    public FieldExpressionParser(Integer maxLevel, Integer currentLevel, ReturnFieldExtractor extractor) {
        this.maxLevel = maxLevel;
        this.currentLevel = currentLevel;
        this.extractor = extractor;
        this.util = new ReflectionUtil();
        this.canGoDeeper = currentLevel < maxLevel;
    }

    public ObjectMapper parse(String fieldsString, Class<?> clazz)
        throws MalformedExpressionException, NoSuchFieldException, NoSuchMethodException, ExpressionDepthException {
        ObjectMapper mapper = initializeDefaultMapper(clazz);
        String currentString = fieldsString;
        while(currentString.length() > 0) {
            log.debug("parsing \"{}\"", currentString);
            Integer nextOpeningBracket = currentString.indexOf('(');
            Integer nextComma = currentString.indexOf(',');
            if(nextOpeningBracket >= 0) {
                if (nextComma >= 0 && nextComma < nextOpeningBracket) {
                    simpleExpression(mapper, currentString.substring(0, nextComma), clazz);
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
                    String subFieldExpression =
                        currentString.substring(nextOpeningBracket + 1, matchingBracket);
                    ObjectMapper childMapper = new FieldExpressionParser(maxLevel, currentLevel + 1)
                        .parse(subFieldExpression, util.getFieldClass(clazz, fieldName));
                    mapper.addMapping(new NestedFieldMapping(clazz, fieldName, childMapper));

                    currentString = currentString.substring(matchingBracket + 1);
                }
            } else {
                //no more complex expressions
                log.debug("no complex expressions in \"{}\"", currentString);
                for(String fieldName : currentString.split(",")) {
                    simpleExpression(mapper, fieldName, clazz);
                }
                break;
            }
        }
        return mapper;
    }

    private void simpleExpression(ObjectMapper mapper, String name, Class<?> clazz)
        throws NoSuchFieldException, MalformedExpressionException, NoSuchMethodException, ExpressionDepthException {
        if(name.startsWith("-")) {
            mapper.removeMapping(name.substring(1));
        } else if(name.startsWith("+")) {
            mapper.addMapping(createSimpleMapping(name.substring(1), clazz));
        } else {
            mapper.addMapping(createSimpleMapping(name, clazz));
        }
    }

    private FieldMapping createSimpleMapping(String name, Class<?> clazz)
        throws NoSuchFieldException, NoSuchMethodException, MalformedExpressionException, ExpressionDepthException {
        Class<?> fieldClass = util.getFieldClass(clazz, name);
        if(AbstractAuditingEntity.class.isAssignableFrom(fieldClass)) {
            ObjectMapper childMapper = new FieldExpressionParser().parse("id", clazz);
            return new NestedFieldMapping(clazz, name, childMapper);
        } else {
            return new FieldMapping(clazz, name);
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

    private ObjectMapper initializeDefaultMapper(Class<?> clazz)
        throws NoSuchFieldException, NoSuchMethodException, MalformedExpressionException, ExpressionDepthException {
        List<Field> defaultFields = extractor.extract(clazz);
        ObjectMapper mapper = new ObjectMapper();
        for(Field field : defaultFields) {
            if(AbstractAuditingEntity.class.isAssignableFrom(field.getType())) {
                ObjectMapper childMapper =
                    new FieldExpressionParser(maxLevel, currentLevel + 1, new IdReturnFieldExtractor())
                        .parse("id", clazz);
                mapper.addMapping(new NestedFieldMapping(clazz, field.getName(), childMapper));
            } else {
                mapper.addMapping(new FieldMapping(clazz, field.getName()));
            }
        }
        return mapper;
    }

    private void failIfTooDeep() throws ExpressionDepthException {
        if(!canGoDeeper) {
            throw new ExpressionDepthException("maximum depth of " + maxLevel + " reached");
        }
    }

}
