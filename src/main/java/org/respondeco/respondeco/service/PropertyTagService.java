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
        if(restParams != null) {
            pageable = restParams.buildPageRequest();
        }
        if(filter == null) {
            filter = "";
        }
        List<PropertyTag> result = propertyTagRepository.findByNameContainingIgnoreCase(filter, pageable);
        return result;
    }

    public List<PropertyTag> getOrCreateTags(List<PropertyTag> tags) {
        List<PropertyTag> response = new ArrayList<>();
        if(tags == null) {
            return response;
        }

        tags.stream().forEach(
            tag -> {
                PropertyTag savedTag = propertyTagRepository.findByName(tag.getName());
                if(savedTag == null) {
                    savedTag = new PropertyTag();
                    savedTag.setName(tag.getName());
                    savedTag = propertyTagRepository.save(savedTag);
                }
                response.add(savedTag);
            }
        );

        return response;
    }

}
