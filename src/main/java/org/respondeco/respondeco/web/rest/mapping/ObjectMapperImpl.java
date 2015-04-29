package org.respondeco.respondeco.web.rest.mapping;

import lombok.ToString;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by clemens on 22/03/15.
 */
@ToString
public class ObjectMapperImpl implements ObjectMapper {

    private static Logger log = LoggerFactory.getLogger(ObjectMapperImpl.class);

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
            .filter(mapping -> !fieldName.equals(mapping.getFieldName()))  //mapping.getFieldName() may be null
            .collect(Collectors.toSet());
        return this;
    }

    public ObjectMapperImpl clearAllButId() {
        this.mappings = this.mappings.stream()
            .filter(
                mapping ->
                    "id".equals(mapping.getFieldName()))    //mapping.getFieldName() may be null
            .collect(Collectors.toSet());
        log.debug("cleared mappings, remaining mappings: {}", mappings);
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
