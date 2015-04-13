package org.respondeco.respondeco.web.rest.mapper;

import lombok.ToString;

import java.util.*;
import java.util.stream.Collectors;

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
        this.mappings = this.mappings
            .stream()
            .filter(mapping -> !mapping.getFieldName().equals(fieldName))
            .collect(Collectors.toSet());
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

    @Override
    public List<Map<String, Object>> mapAll(List objects) throws MappingException {
        return (List<Map<String, Object>>) objects.stream()
            .map(this::map)
            .collect(Collectors.toList());
    }

}
