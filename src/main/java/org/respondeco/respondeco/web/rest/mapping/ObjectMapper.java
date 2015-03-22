package org.respondeco.respondeco.web.rest.mapping;

import lombok.ToString;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Created by clemens on 22/03/15.
 */
@ToString
public class ObjectMapper {

    private Set<FieldMapping> mappings;

    public ObjectMapper() {
        this.mappings = new HashSet<>();
    }

    public ObjectMapper addMapping(FieldMapping mapping) {
        this.mappings.add(mapping);
        return this;
    }

    public ObjectMapper removeMapping(String fieldName) {
        FieldMapping m = null;
        for(FieldMapping mapping : this.mappings) {
            if(mapping.getFieldName().equals(fieldName)) {
                m = mapping;
            }
        }
        if(m != null) {
            this.mappings.remove(m);
        }
        return this;
    }

    public Map<String, Object> map(Object object) throws InvocationTargetException, IllegalAccessException {
        Map<String, Object> result = new HashMap<>();
        for(FieldMapping mapping : mappings) {
            result.put(mapping.getFieldName(), mapping.map(object));
        }
        return result;
    }

}
