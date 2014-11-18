package org.respondeco.respondeco.web.rest;

import com.codahale.metrics.annotation.Timed;
import org.respondeco.respondeco.domain.ResourceOfferJoinResourceTag;
import org.respondeco.respondeco.repository.ResourceOfferJoinResourceTagRepository;
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
 * REST controller for managing ResourceOfferJoinResourceTag.
 */
@RestController
@RequestMapping("/app")
public class ResourceOfferJoinResourceTagResource {

    private final Logger log = LoggerFactory.getLogger(ResourceOfferJoinResourceTagResource.class);

    @Inject
    private ResourceOfferJoinResourceTagRepository resourceOfferJoinResourceTagRepository;

    /**
     * POST  /rest/resourceOfferJoinResourceTags -> Create a new resourceOfferJoinResourceTag.
     */
    @RequestMapping(value = "/rest/resourceOfferJoinResourceTags",
            method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public void create(@RequestBody ResourceOfferJoinResourceTag resourceOfferJoinResourceTag) {
        log.debug("REST request to save ResourceOfferJoinResourceTag : {}", resourceOfferJoinResourceTag);
        resourceOfferJoinResourceTagRepository.save(resourceOfferJoinResourceTag);
    }

    /**
     * GET  /rest/resourceOfferJoinResourceTags -> get all the resourceOfferJoinResourceTags.
     */
    @RequestMapping(value = "/rest/resourceOfferJoinResourceTags",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public List<ResourceOfferJoinResourceTag> getAll() {
        log.debug("REST request to get all ResourceOfferJoinResourceTags");
        return resourceOfferJoinResourceTagRepository.findAll();
    }

    /**
     * GET  /rest/resourceOfferJoinResourceTags/:id -> get the "id" resourceOfferJoinResourceTag.
     */
    @RequestMapping(value = "/rest/resourceOfferJoinResourceTags/{id}",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<ResourceOfferJoinResourceTag> get(@PathVariable Long id) {
        log.debug("REST request to get ResourceOfferJoinResourceTag : {}", id);
        return Optional.ofNullable(resourceOfferJoinResourceTagRepository.findOne(id))
            .map(resourceOfferJoinResourceTag -> new ResponseEntity<>(
                resourceOfferJoinResourceTag,
                HttpStatus.OK))
            .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    /**
     * DELETE  /rest/resourceOfferJoinResourceTags/:id -> delete the "id" resourceOfferJoinResourceTag.
     */
    @RequestMapping(value = "/rest/resourceOfferJoinResourceTags/{id}",
            method = RequestMethod.DELETE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public void delete(@PathVariable Long id) {
        log.debug("REST request to delete ResourceOfferJoinResourceTag : {}", id);
        resourceOfferJoinResourceTagRepository.delete(id);
    }
}
