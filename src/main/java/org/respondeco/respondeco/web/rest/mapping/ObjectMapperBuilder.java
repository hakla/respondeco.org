package org.respondeco.respondeco.web.rest.mapping;

import org.respondeco.respondeco.domain.AbstractAuditingEntity;
import org.respondeco.respondeco.web.rest.parsing.ExpressionParsingException;
import org.respondeco.respondeco.web.rest.parsing.FieldExpressionParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.*;
import java.util.List;
import java.util.Map;

/**
 * Created by clemens on 24/03/15.
 */
public class ObjectMapperBuilder implements FieldExpressionParser.ParserListener {

    private static final Logger log = LoggerFactory.getLogger(ObjectMapperBuilder.class);

    private static final Integer MAX_DEPTH = 5;

    private Class<?> clazz;
    private ObjectMapperImpl mapper;

    private ReflectionUtil util;
    private ReturnValueExtractor extractor;
    private Integer restDepth;

    public ObjectMapperBuilder(Class<?> clazz) throws MappingException {
        this(clazz, MAX_DEPTH);
    }

    public ObjectMapperBuilder(Class<?> clazz, Integer restDepth) throws MappingException {
        this.util = new ReflectionUtil();
        this.extractor = new DefaultReturnValueExtractor();
        this.clazz = clazz;
        this.restDepth = restDepth;
        this.mapper = initializeDefaultMapper(clazz, restDepth);
    }

    public ObjectMapper getMapper() {
        return this.mapper;
    }

    private ObjectMapperImpl initializeDefaultMapper(Class<?> clazz, Integer restDepth) throws MappingException {
        log.debug("initializing default mapper for class {}",  clazz);
        ObjectMapperImpl mapper = new ObjectMapperImpl();
        Map<Field, DefaultReturnValue> defaultFields = extractor.extractFields(clazz);
        Map<Method, DefaultReturnValue> defaultMethods = extractor.extractMethods(clazz);
        try {
            for(Map.Entry<Field, DefaultReturnValue> entry : defaultFields.entrySet()) {
                Integer submappingRestDepth = Math.min(restDepth - 1, entry.getValue().maxDepth());
                mapper.addMapping(createMapping(clazz, entry.getKey(), submappingRestDepth));
            }
            for(Map.Entry<Method, DefaultReturnValue> entry : defaultMethods.entrySet()) {
                Integer submappingRestDepth = Math.min(restDepth - 1, entry.getValue().maxDepth());
                Method method = entry.getKey();
                String fieldName = null;
                if (method.getName().startsWith("get")) {
                    fieldName = method.getName().substring(3);
                    fieldName = fieldName.substring(0, 1).toLowerCase() + fieldName.substring(1);
                } else {
                    fieldName = method.getName();
                }
                mapper.addMapping(doCreateMapping(fieldName, null, method, submappingRestDepth));
            }
        } catch(Exception e) {
            throw new MappingException(e);
        }
        return mapper;
    }

    private FieldMapping createMapping(Class<?> clazz, Field field, Integer restDepth)
            throws NoSuchFieldException, NoSuchMethodException {
        Method accessor = util.getAccessor(clazz, field.getName());
        return doCreateMapping(field.getName(), field, accessor, restDepth);
    }

    private FieldMapping createMapping(String fieldName, Integer restDepth) throws MappingException,
            NoSuchFieldException, NoSuchMethodException {
        Field field = null;
        try {
            field = util.getField(clazz, fieldName);
        } catch (NoSuchFieldException e) {
            log.debug("no field {}.{} found, trying accessor", clazz, fieldName);
        }
        Method accessor = util.getAccessor(clazz, fieldName);
        return doCreateMapping(fieldName, field, accessor, restDepth);
    }

    private FieldMapping doCreateMapping(String fieldName, Field field, Method accessor, Integer restDepth)
            throws NoSuchFieldException, NoSuchMethodException {
        Class<?> returnType = accessor.getReturnType();
        String returnFieldName = fieldName;
        if(field != null) {
            DefaultReturnValue annotation = field.getAnnotation(DefaultReturnValue.class);
            if(annotation != null) {
                if(annotation.useName().length() > 0) {
                    returnFieldName = annotation.useName();
                }
            }
        } else if(accessor != null) {
            DefaultReturnValue annotation = accessor.getAnnotation(DefaultReturnValue.class);
            if(annotation != null) {
                if(annotation.useName().length() > 0) {
                    returnFieldName = annotation.useName();
                }
            }
        }

        if(AbstractAuditingEntity.class.isAssignableFrom(returnType)) {
            if(restDepth == 0) {
                return util.getIdMapping((Class<AbstractAuditingEntity>) returnType);
            } else {
                ObjectMapperImpl childMapper = initializeDefaultMapper(returnType, restDepth);
                return new NestedFieldMapping(fieldName, field, accessor, childMapper, returnFieldName);
            }
        } else if (Iterable.class.isAssignableFrom(returnType)) {
            ParameterizedType genericReturnType = (ParameterizedType) accessor.getGenericReturnType();
            returnType = (Class) genericReturnType.getActualTypeArguments()[0];
            ObjectMapperImpl childMapper;
            if(AbstractAuditingEntity.class.isAssignableFrom(returnType)) {
                if(restDepth == 0) {
                    FieldMapping idMapping = util.getIdMapping((Class<AbstractAuditingEntity>) returnType);
                    childMapper = new ObjectMapperImpl();
                    childMapper.addMapping(idMapping);
                    return new NestedFieldMapping(fieldName, field, accessor, childMapper, returnFieldName);
                } else {
                    childMapper = initializeDefaultMapper(returnType, restDepth);
                    return new NestedFieldMapping(fieldName, field, accessor, childMapper, returnFieldName);
                }
            } else {
                return new FieldMapping(fieldName, field, accessor, returnFieldName);
            }
        } else {
            return new FieldMapping(fieldName, field, accessor, returnFieldName);
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
            mapper.addMapping(createMapping(fieldName, restDepth - 1));
        } catch (Exception e) {
            throw new ExpressionParsingException(e);
        }
    }

    @Override
    public void onNestedExpression(String fieldName, FieldExpressionParser subParser) throws ExpressionParsingException {
        if(restDepth == 0) {
            return;
        }
        try {
            Field field = null;
            try {
                field = util.getField(clazz, fieldName);
            } catch(Exception e) {
                log.debug("no field {}.{} found, trying accessor", clazz, fieldName);
            }
            Method accessor = util.getAccessor(clazz, fieldName);
            Class<?> returnType = accessor.getReturnType();

            String returnFieldName = fieldName;
            if(field != null) {
                DefaultReturnValue annotation = field.getAnnotation(DefaultReturnValue.class);
                if(annotation != null) {
                    if(annotation.useName().length() > 0) {
                        returnFieldName = annotation.useName();
                    }
                }
            } else if(accessor != null) {
                DefaultReturnValue annotation = accessor.getAnnotation(DefaultReturnValue.class);
                if(annotation != null) {
                    if(annotation.useName().length() > 0) {
                        returnFieldName = annotation.useName();
                    }
                }
            }

            if (Iterable.class.isAssignableFrom(returnType)) {
                returnType = (Class) ((ParameterizedType) accessor.getGenericReturnType())
                    .getActualTypeArguments()[0];
            }

            ObjectMapperBuilder childBuilder = new ObjectMapperBuilder(returnType, restDepth-1);
            subParser.setParserListener(childBuilder);
            subParser.parse();
            FieldMapping nestedMapping = new NestedFieldMapping(fieldName, field, accessor, childBuilder.getMapper(),
                returnFieldName);
            mapper.removeMapping(fieldName);
            mapper.addMapping(nestedMapping);
        } catch (Exception e) {
            throw new ExpressionParsingException(e);
        }
    }
}
