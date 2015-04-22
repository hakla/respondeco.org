package org.respondeco.respondeco.web.rest.mapping;

import org.respondeco.respondeco.web.rest.parsing.FieldExpressionParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;

import java.util.Optional;

/**
 * Created by clemens on 24/03/15.
 */
@Component
public class ObjectMapperFactory {

    private static final Logger log = LoggerFactory.getLogger(ObjectMapperFactory.class);

    public ObjectMapperFactory() {
    }

    @Cacheable(value = "objectMapper")
    public ObjectMapper createMapper(Class<?> clazz, String expression) throws MappingException {
        log.debug("entering createMapper with string: {}", expression);
        Optional expressionOptional = Optional.ofNullable(expression);
        final ObjectMapperBuilder builder = new ObjectMapperBuilder(clazz);
        try {
            expressionOptional.ifPresent(
                expr -> {
                    FieldExpressionParser parser = new FieldExpressionParser(expression);
                    parser.setParserListener(builder);
                    parser.parse();
                });
        } catch (Exception e) {
            throw new MappingException(e);
        }
        return builder.getMapper();
    }

}
