package org.respondeco.respondeco.service;

import org.respondeco.respondeco.domain.PropertyTag;
import org.respondeco.respondeco.repository.PropertyTagRepository;
import org.respondeco.respondeco.web.rest.dto.PropertyTagResponseDTO;
import org.respondeco.respondeco.web.rest.util.RestParameters;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Clemens Puehringer on 27/11/14.
 */

@Service
@Transactional
public class PropertyTagService {

    private PropertyTagRepository propertyTagRepository;

    @Inject
    public PropertyTagService(PropertyTagRepository propertyTagRepository) {
        this.propertyTagRepository = propertyTagRepository;
    }

    public List<PropertyTag> getPropertyTags(String filter, RestParameters restParams) {
        Pageable pageable = null;
        List<String> fields = null;
        if(restParams != null) {
            pageable = restParams.buildPageRequest();
            fields = restParams.getFields();
        }
        if(filter == null) {
            filter = "";
        }
        List<PropertyTag> result = propertyTagRepository.findByNameContainingIgnoreCase(filter, pageable);
        return result;
    }

    public List<PropertyTag> getOrCreateTags(List<String> tagNames) {
        List<PropertyTag> response = new ArrayList<>();
        if(tagNames == null) {
            return response;
        }
        PropertyTag tag = null;
        for(String s : tagNames) {
            tag = propertyTagRepository.findByName(s);
            if(tag == null) {
                tag = new PropertyTag();
                tag.setName(s);
                tag = propertyTagRepository.save(tag);
            }
            response.add(tag);
        }
        return response;
    }

}
