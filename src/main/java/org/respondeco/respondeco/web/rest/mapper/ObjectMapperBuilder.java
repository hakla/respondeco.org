package org.respondeco.respondeco.web.rest.mapper;

import org.respondeco.respondeco.domain.AbstractAuditingEntity;
import org.respondeco.respondeco.web.rest.mapper.parser.ExpressionParsingException;
import org.respondeco.respondeco.web.rest.mapper.parser.FieldExpressionParser;

import java.lang.reflect.Field;
import java.util.List;

/**
 * Created by clemens on 24/03/15.
 */
public class ObjectMapperBuilder implements FieldExpressionParser.ParserListener {

    private Class<?> clazz;
    private ObjectMapperImpl mapper;

    private ReflectionUtil util;
    private ReturnFieldExtractor extractor;

    public ObjectMapperBuilder(Class<?> clazz) throws MappingException {
        this.util = new ReflectionUtil();
        this.extractor = new DefaultReturnFieldExtractor();
        this.clazz = clazz;
        this.mapper = initializeDefaultMapper(clazz);
    }

    public ObjectMapper getMapper() {
        return this.mapper;
    }

    private ObjectMapperImpl initializeDefaultMapper(Class<?> clazz)throws MappingException {
        List<Field> defaultFields = extractor.extract(clazz);
        ObjectMapperImpl mapper = new ObjectMapperImpl();
        for(Field field : defaultFields) {
            mapper.addMapping(createSimpleMapping(field));
        }
        return mapper;
    }

        private FieldMapping createSimpleMapping(Field field) throws MappingException {
        Class<?> fieldClass = field.getType();
        if(AbstractAuditingEntity.class.isAssignableFrom(fieldClass)) {
            ObjectMapperImpl childMapper = new ObjectMapperImpl();
            childMapper.addMapping(new FieldMapping(fieldClass, "id"));
            return new NestedFieldMapping(clazz, field, childMapper);
        } else {
            return new FieldMapping(clazz, field);
        }
    }

    @Override
    public void onNegatedExpression(String name) throws ExpressionParsingException {
        mapper.removeMapping(name);
    }

    @Override
    public void onSimpleExpression(String name) throws ExpressionParsingException {
        Field field = null;
        try {
            field = util.getField(clazz, name);
            mapper.addMapping(createSimpleMapping(field));
        } catch (Exception e) {
            throw new ExpressionParsingException(e);
        }
    }

    @Override
    public void onNestedExpression(String name, FieldExpressionParser subParser) throws ExpressionParsingException {
        try {
            Class<?> fieldClass = util.getFieldClass(clazz, name);
            ObjectMapperBuilder childBuilder = new ObjectMapperBuilder(fieldClass);
            subParser.setParserListener(childBuilder);
            subParser.parse();
            FieldMapping nestedMapping = new NestedFieldMapping(clazz, name, childBuilder.getMapper());
            mapper.addMapping(nestedMapping);
        } catch (Exception e) {
            throw new ExpressionParsingException(e);
        }
    }
}
