package org.respondeco.respondeco.web.rest.mapping;

import org.respondeco.respondeco.domain.AbstractAuditingEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;

/**
 * Created by clemens on 11/03/15.
 */
public class ResponseMapper {

    private final Logger log = LoggerFactory.getLogger(ResponseMapper.class);

    public class ResponseBuilder {

        private Map<String, Object> response;

        public ResponseBuilder() {
            response = new HashMap<>();
        }

        public void addValue(AbstractAuditingEntity entity, String field)
            throws IllegalAccessException, NoSuchMethodException, InvocationTargetException {

            String fieldName = null;
            Object value = null;
            if(field.contains("(")) {
                log.debug("no complex expression ({})", field);
                fieldName = field.substring(0, field.indexOf("("));
                AbstractAuditingEntity member = (AbstractAuditingEntity) getObject(entity, fieldName);
                String subFields = field.substring(field.indexOf("(") + 1, field.lastIndexOf(")"));
                ResponseMapper mapper = new ResponseMapper(new DefaultFieldExtractor());
                value = mapper.map(member, Arrays.asList(subFields.split(",")));
            } else {
                log.debug("no complex expression ({})", field);
                fieldName = field;
                value = getObject(entity, fieldName);
            }
            response.put(fieldName, value);
        }

        public Map<String, Object> build() {
            return response;
        }

        private Object getObject(Object object, String fieldName)
            throws InvocationTargetException, IllegalAccessException, NoSuchMethodException {

            Method getter = getGetter(object, fieldName);
            return getter.invoke(object);
        }

        private Method getGetter(Object object, String fieldName) throws NoSuchMethodException {
            log.debug("getting getter for {}", fieldName);
            String uppercaseChar = ("" + fieldName.charAt(0)).toUpperCase();
            String getterName = "get" + uppercaseChar + fieldName.substring(1);
            return object.getClass().getMethod(getterName);
        }

    }

    private ResponseBuilder builder;
    private FieldExtractor defaultFieldExtractor;

    public ResponseMapper() {
        builder = new ResponseBuilder();
        defaultFieldExtractor = new TopLevelDefaultFieldExtractor();
    }

    public ResponseMapper(DefaultFieldExtractor defaultFieldExtractor) {
        builder = new ResponseBuilder();
        this.defaultFieldExtractor = defaultFieldExtractor;
    }

    public Map<String, Object> map(AbstractAuditingEntity entity, List<String> wantedFields)
        throws IllegalAccessException, NoSuchMethodException, InvocationTargetException {

        List<String> fields = new ArrayList<>();
        fields.addAll(defaultFieldExtractor.getFieldNames(entity.getClass()));
        fields.addAll(wantedFields);

        for(String field : fields) {
            log.debug("adding value for field {}", field);
            builder.addValue(entity, field);
        }
        return builder.build();
    }

}
