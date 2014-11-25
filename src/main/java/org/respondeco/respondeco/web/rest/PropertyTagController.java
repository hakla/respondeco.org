package org.respondeco.respondeco.web.rest;

import com.codahale.metrics.annotation.Timed;
import org.respondeco.respondeco.domain.PropertyTag;
import org.respondeco.respondeco.repository.PropertyTagRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
     * POST  /rest/propertytags -> Create a new propertytag.
     */
    @RequestMapping(value = "/rest/propertytags",
            method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public void create(@RequestBody PropertyTag propertytag) {
        log.debug("REST request to save PropertyTag : {}", propertytag);
        propertytagRepository.save(propertytag);
    }

    /**
     * GET  /rest/propertytags -> get all the propertytags.
     */
    @RequestMapping(value = "/rest/propertytags",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public List<PropertyTag> getAll() {
        log.debug("REST request to get all PropertyTags");
        return propertytagRepository.findAll();
    }

    /**
     * GET  /rest/propertytags/:id -> get the "id" propertytag.
     */
    @RequestMapping(value = "/rest/propertytags/{id}",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<PropertyTag> get(@PathVariable Long id) {
        log.debug("REST request to get PropertyTag : {}", id);
        return Optional.ofNullable(propertytagRepository.findOne(id))
            .map(propertytag -> new ResponseEntity<>(
                propertytag,
                HttpStatus.OK))
            .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    /**
     * DELETE  /rest/propertytags/:id -> delete the "id" propertytag.
     */
    @RequestMapping(value = "/rest/propertytags/{id}",
            method = RequestMethod.DELETE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public void delete(@PathVariable Long id) {
        log.debug("REST request to delete PropertyTag : {}", id);
        propertytagRepository.delete(id);
    }
}
