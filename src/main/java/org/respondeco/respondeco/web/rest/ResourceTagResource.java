package org.respondeco.respondeco.web.rest;

import com.codahale.metrics.annotation.Timed;
import org.respondeco.respondeco.domain.ResourceTag;
import org.respondeco.respondeco.repository.ResourceTagRepository;
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
 * REST controller for managing ResourceTag.
 */
@RestController
@RequestMapping("/app")
public class ResourceTagResource {

    private final Logger log = LoggerFactory.getLogger(ResourceTagResource.class);

    @Inject
    private ResourceTagRepository resourceTagRepository;

    /**
     * POST  /rest/resourceTags -> Create a new resourceTag.
     */
    @RequestMapping(value = "/rest/resourceTags",
            method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public void create(@RequestBody ResourceTag resourceTag) {
        log.debug("REST request to save ResourceTag : {}", resourceTag);
        resourceTagRepository.save(resourceTag);
    }

    /**
     * GET  /rest/resourceTags -> get all the resourceTags.
     */
    @RequestMapping(value = "/rest/resourceTags",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public List<ResourceTag> getAll() {
        log.debug("REST request to get all ResourceTags");
        return resourceTagRepository.findAll();
    }

    /**
     * GET  /rest/resourceTags/:id -> get the "id" resourceTag.
     */
    @RequestMapping(value = "/rest/resourceTags/{id}",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<ResourceTag> get(@PathVariable Long id) {
        log.debug("REST request to get ResourceTag : {}", id);
        return Optional.ofNullable(resourceTagRepository.findOne(id))
            .map(resourceTag -> new ResponseEntity<>(
                resourceTag,
                HttpStatus.OK))
            .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    /**
     * DELETE  /rest/resourceTags/:id -> delete the "id" resourceTag.
     */
    @RequestMapping(value = "/rest/resourceTags/{id}",
            method = RequestMethod.DELETE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public void delete(@PathVariable Long id) {
        log.debug("REST request to delete ResourceTag : {}", id);
        resourceTagRepository.delete(id);
    }
}
