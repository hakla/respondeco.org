package org.respondeco.respondeco.web.rest;

import com.codahale.metrics.annotation.Timed;
import org.respondeco.respondeco.domain.PropertyTag;
import org.respondeco.respondeco.repository.PropertyTagRepository;
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

    @Inject
    private PropertyTagRepository propertytagRepository;

    /**
     * GET  /rest/propertytags -> get all the propertytags.
     */
    @RequestMapping(value = "/rest/propertytags",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public List<String> getTagsMatching(@RequestParam String filter, @RequestParam Integer limit) {
        log.debug("REST request to get all PropertyTags");
        if(filter == null) {
            filter = "";
        }
        if(limit == null) {
            limit = 20;
        }
        PageRequest request = new PageRequest(0, limit);
        return propertytagRepository.getNamesMatching(filter, request);
    }

}
