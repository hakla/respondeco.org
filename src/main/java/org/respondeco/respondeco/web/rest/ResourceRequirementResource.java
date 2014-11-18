package org.respondeco.respondeco.web.rest;

import com.codahale.metrics.annotation.Timed;
import org.respondeco.respondeco.domain.ResourceRequirement;
import org.respondeco.respondeco.repository.ResourceRequirementRepository;
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
 * REST controller for managing ResourceRequirement.
 */
@RestController
@RequestMapping("/app")
public class ResourceRequirementResource {

    private final Logger log = LoggerFactory.getLogger(ResourceRequirementResource.class);

    @Inject
    private ResourceRequirementRepository resourceRequirementRepository;

    /**
     * POST  /rest/resourceRequirements -> Create a new resourceRequirement.
     */
    @RequestMapping(value = "/rest/resourceRequirements",
            method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public void create(@RequestBody ResourceRequirement resourceRequirement) {
        log.debug("REST request to save ResourceRequirement : {}", resourceRequirement);
        resourceRequirementRepository.save(resourceRequirement);
    }

    /**
     * GET  /rest/resourceRequirements -> get all the resourceRequirements.
     */
    @RequestMapping(value = "/rest/resourceRequirements",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public List<ResourceRequirement> getAll() {
        log.debug("REST request to get all ResourceRequirements");
        return resourceRequirementRepository.findAll();
    }

    /**
     * GET  /rest/resourceRequirements/:id -> get the "id" resourceRequirement.
     */
    @RequestMapping(value = "/rest/resourceRequirements/{id}",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<ResourceRequirement> get(@PathVariable Long id) {
        log.debug("REST request to get ResourceRequirement : {}", id);
        return Optional.ofNullable(resourceRequirementRepository.findOne(id))
            .map(resourceRequirement -> new ResponseEntity<>(
                resourceRequirement,
                HttpStatus.OK))
            .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    /**
     * DELETE  /rest/resourceRequirements/:id -> delete the "id" resourceRequirement.
     */
    @RequestMapping(value = "/rest/resourceRequirements/{id}",
            method = RequestMethod.DELETE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public void delete(@PathVariable Long id) {
        log.debug("REST request to delete ResourceRequirement : {}", id);
        resourceRequirementRepository.delete(id);
    }
}
