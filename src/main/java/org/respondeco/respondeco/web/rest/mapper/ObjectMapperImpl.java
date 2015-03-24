package org.respondeco.respondeco.web.rest.mapper;

import lombok.ToString;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Created by clemens on 22/03/15.
 */
@ToString
public class ObjectMapperImpl implements ObjectMapper {

    private Set<FieldMapping> mappings;

    public ObjectMapperImpl() {
        this.mappings = new HashSet<>();
    }

    public ObjectMapperImpl addMapping(FieldMapping mapping) {
        this.mappings.add(mapping);
        return this;
    }

    public ObjectMapperImpl removeMapping(String fieldName) {
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

    @Override
    public Map<String, Object> map(Object object) throws MappingException {
        Map<String, Object> result = new HashMap<>();
        for(FieldMapping mapping : mappings) {
            result.put(mapping.getFieldName(), mapping.map(object));
        }
        return result;
    }

}
