package org.respondeco.respondeco.web.rest.mapping;

import org.respondeco.respondeco.domain.AbstractAuditingEntity;
import org.respondeco.respondeco.web.rest.parsing.ExpressionParsingException;
import org.respondeco.respondeco.web.rest.parsing.FieldExpressionParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.*;
import java.util.List;

/**
 * Created by clemens on 24/03/15.
 */
public class ObjectMapperBuilder implements FieldExpressionParser.ParserListener {

    private static final Logger log = LoggerFactory.getLogger(ObjectMapperBuilder.class);

    private Class<?> clazz;
    private ObjectMapperImpl mapper;

    private ReflectionUtil util;
    private ReturnValueExtractor extractor;

    public ObjectMapperBuilder(Class<?> clazz) throws MappingException {
        this.util = new ReflectionUtil();
        this.extractor = new DefaultReturnValueExtractor();
        this.clazz = clazz;
        this.mapper = initializeDefaultMapper(clazz);
    }

    public ObjectMapper getMapper() {
        return this.mapper;
    }

    private ObjectMapperImpl initializeDefaultMapper(Class<?> clazz) throws MappingException {
        log.debug("initializing default mapper for class {}",  clazz);
        ObjectMapperImpl mapper = new ObjectMapperImpl();
        List<Field> defaultFields = extractor.extractFields(clazz);
        List<Method> defaultMethods = extractor.extractMethods(clazz);
        try {
            for (Field field : defaultFields) {
                mapper.addMapping(createSimpleMapping(clazz, field));
            }
            for(Method method : defaultMethods) {
                String fieldName = null;
                if(method.getName().startsWith("get")) {
                    fieldName = method.getName().substring(3);
                    fieldName = fieldName.substring(0,1).toLowerCase() + fieldName.substring(1);
                } else {
                    fieldName = method.getName();
                }
                mapper.addMapping(doCreateSimpleMapping(fieldName, null, method));
            }
        } catch(Exception e) {
            throw new MappingException(e);
        }
        return mapper;
    }

    private FieldMapping createSimpleMapping(Class<?> clazz, Field field)
            throws NoSuchFieldException, NoSuchMethodException {
        Method accessor = util.getAccessor(clazz, field.getName());
        return doCreateSimpleMapping(field.getName(), field, accessor);
    }

    private FieldMapping createSimpleMapping(String fieldName) throws MappingException,
            NoSuchFieldException, NoSuchMethodException {
        Field field = null;
        try {
            field = util.getField(clazz, fieldName);
        } catch (NoSuchFieldException e) {
            log.debug("no field {}.{} found, trying accessor", clazz, fieldName);
        }
        Method accessor = util.getAccessor(clazz, fieldName);
        return doCreateSimpleMapping(fieldName, field, accessor);
    }

    private FieldMapping doCreateSimpleMapping(String fieldName, Field field, Method accessor) {
        Class<?> returnType = accessor.getReturnType();

        if(AbstractAuditingEntity.class.isAssignableFrom(returnType)) {
            ObjectMapperImpl childMapper = initializeDefaultMapper(returnType);
            return new NestedFieldMapping(fieldName, field, accessor, childMapper);
        } else if (Iterable.class.isAssignableFrom(returnType)) {
            ParameterizedType genericReturnType = (ParameterizedType) accessor.getGenericReturnType();
            returnType = (Class) genericReturnType.getActualTypeArguments()[0];
            ObjectMapperImpl childMapper = initializeDefaultMapper(returnType);
            return new NestedFieldMapping(fieldName, field, accessor, childMapper);
        } else {
            return new FieldMapping(fieldName, field, accessor);
        }
    }

    @Override
    public void onNegatedExpression(String fieldName) throws ExpressionParsingException {
        mapper.removeMapping(fieldName);
    }

    @Override
    public void onSimpleExpression(String fieldName) throws ExpressionParsingException {
        try {
            mapper.removeMapping(fieldName);
            mapper.addMapping(createSimpleMapping(fieldName));
        } catch (Exception e) {
            throw new ExpressionParsingException(e);
        }
    }

    @Override
    public void onNestedExpression(String fieldName, FieldExpressionParser subParser) throws ExpressionParsingException {
        try {
            Field field = null;
            try {
                field = util.getField(clazz, fieldName);
            } catch(Exception e) {
                log.debug("no field {}.{} found, trying accessor", clazz, fieldName);
            }
            Method accessor = util.getAccessor(clazz, fieldName);
            Class<?> returnType = accessor.getReturnType();

            if (Iterable.class.isAssignableFrom(returnType)) {
                returnType = (Class) ((ParameterizedType) accessor.getGenericReturnType())
                    .getActualTypeArguments()[0];
            }

            ObjectMapperBuilder childBuilder = new ObjectMapperBuilder(returnType);
            subParser.setParserListener(childBuilder);
            subParser.parse();
            FieldMapping nestedMapping = new NestedFieldMapping(fieldName, field, accessor, childBuilder.getMapper());
            mapper.removeMapping(fieldName);
            mapper.addMapping(nestedMapping);
        } catch (Exception e) {
            throw new ExpressionParsingException(e);
        }
    }
}
