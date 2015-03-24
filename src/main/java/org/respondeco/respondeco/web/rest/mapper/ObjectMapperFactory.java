package org.respondeco.respondeco.web.rest.mapper;

import org.respondeco.respondeco.web.rest.mapper.parser.FieldExpressionParser;

/**
 * Created by clemens on 24/03/15.
 */
public class ObjectMapperFactory {

    private Class<?> clazz;

    public ObjectMapperFactory(Class<?> clazz) {
        this.clazz = clazz;
    }

    public ObjectMapper createMapper(String expression) throws MappingException {
        ObjectMapperBuilder builder = null;
        try {
            builder = new ObjectMapperBuilder(clazz);
            FieldExpressionParser parser = new FieldExpressionParser(expression);
            parser.setParserListener(builder);
            parser.parse();
        } catch (Exception e) {
            throw new MappingException(e);
        }
        return builder.getMapper();
    }

}
