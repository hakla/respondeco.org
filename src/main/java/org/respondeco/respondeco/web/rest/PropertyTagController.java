package org.respondeco.respondeco.web.rest;

import com.codahale.metrics.annotation.Timed;
import org.respondeco.respondeco.domain.PropertyTag;
import org.respondeco.respondeco.repository.PropertyTagRepository;
import org.respondeco.respondeco.service.PropertyTagService;
import org.respondeco.respondeco.web.rest.dto.PropertyTagResponseDTO;
import org.respondeco.respondeco.web.rest.util.RestParameters;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.inject.Inject;
import java.util.List;
import java.util.Optional;

/**
 * REST controller for managing PropertyTag.
 */
@RestController
@RequestMapping("/app")
public class PropertyTagController {

    private final Logger log = LoggerFactory.getLogger(PropertyTagController.class);

    private PropertyTagService propertyTagService;

    @Inject
    public PropertyTagController(PropertyTagService propertyTagService) {
        this.propertyTagService = propertyTagService;
    }

    /**
     * GET  /rest/names/propertytags -> get propertytags.
     */
    @RequestMapping(value = "/rest/propertytags",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public List<PropertyTagResponseDTO> getTagsMatching(
            @RequestParam(required = false) String filter,
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer pageSize,
            @RequestParam(required = false) String order,
            @RequestParam(required = false) String fields) {
        log.debug("REST request to get PropertyTags");
        return propertyTagService.getPropertyTags(filter, new RestParameters(page, pageSize, order, fields));
    }

}
