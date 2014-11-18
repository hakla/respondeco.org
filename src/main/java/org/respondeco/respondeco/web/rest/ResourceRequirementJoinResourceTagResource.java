package org.respondeco.respondeco.web.rest;

import com.codahale.metrics.annotation.Timed;
import org.respondeco.respondeco.domain.ResourceRequirementJoinResourceTag;
import org.respondeco.respondeco.repository.ResourceRequirementJoinResourceTagRepository;
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
 * REST controller for managing ResourceRequirementJoinResourceTag.
 */
@RestController
@RequestMapping("/app")
public class ResourceRequirementJoinResourceTagResource {

    private final Logger log = LoggerFactory.getLogger(ResourceRequirementJoinResourceTagResource.class);

    @Inject
    private ResourceRequirementJoinResourceTagRepository resourceRequirementJoinResourceTagRepository;

    /**
     * POST  /rest/resourceRequirementJoinResourceTags -> Create a new resourceRequirementJoinResourceTag.
     */
    @RequestMapping(value = "/rest/resourceRequirementJoinResourceTags",
            method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public void create(@RequestBody ResourceRequirementJoinResourceTag resourceRequirementJoinResourceTag) {
        log.debug("REST request to save ResourceRequirementJoinResourceTag : {}", resourceRequirementJoinResourceTag);
        resourceRequirementJoinResourceTagRepository.save(resourceRequirementJoinResourceTag);
    }

    /**
     * GET  /rest/resourceRequirementJoinResourceTags -> get all the resourceRequirementJoinResourceTags.
     */
    @RequestMapping(value = "/rest/resourceRequirementJoinResourceTags",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public List<ResourceRequirementJoinResourceTag> getAll() {
        log.debug("REST request to get all ResourceRequirementJoinResourceTags");
        return resourceRequirementJoinResourceTagRepository.findAll();
    }

    /**
     * GET  /rest/resourceRequirementJoinResourceTags/:id -> get the "id" resourceRequirementJoinResourceTag.
     */
    @RequestMapping(value = "/rest/resourceRequirementJoinResourceTags/{id}",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<ResourceRequirementJoinResourceTag> get(@PathVariable Long id) {
        log.debug("REST request to get ResourceRequirementJoinResourceTag : {}", id);
        return Optional.ofNullable(resourceRequirementJoinResourceTagRepository.findOne(id))
            .map(resourceRequirementJoinResourceTag -> new ResponseEntity<>(
                resourceRequirementJoinResourceTag,
                HttpStatus.OK))
            .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    /**
     * DELETE  /rest/resourceRequirementJoinResourceTags/:id -> delete the "id" resourceRequirementJoinResourceTag.
     */
    @RequestMapping(value = "/rest/resourceRequirementJoinResourceTags/{id}",
            method = RequestMethod.DELETE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public void delete(@PathVariable Long id) {
        log.debug("REST request to delete ResourceRequirementJoinResourceTag : {}", id);
        resourceRequirementJoinResourceTagRepository.delete(id);
    }
}
