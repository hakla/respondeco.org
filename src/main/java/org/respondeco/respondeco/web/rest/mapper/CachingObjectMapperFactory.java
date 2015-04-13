package org.respondeco.respondeco.web.rest.mapper;

import org.respondeco.respondeco.web.rest.mapper.parser.FieldExpressionParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.Cacheable;

/**
 * Created by clemens on 24/03/15.
 */
public class CachingObjectMapperFactory implements ObjectMapperFactory {

    private static final Logger log = LoggerFactory.getLogger(CachingObjectMapperFactory.class);

    private Class<?> clazz;

    public CachingObjectMapperFactory(Class<?> clazz) {
        this.clazz = clazz;
    }

    public Class<?> getMappedClass() {
        return clazz;
    }

    @Override
    @Cacheable(value = "objectMapper", key = "#root.target.getMappedClass() + expression")
    public ObjectMapper createMapper(String expression) throws MappingException {
        log.debug("entering createMapper with string: {}", expression);
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
