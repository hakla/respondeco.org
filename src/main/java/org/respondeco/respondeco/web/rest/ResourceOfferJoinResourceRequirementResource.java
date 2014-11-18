package org.respondeco.respondeco.web.rest;

import com.codahale.metrics.annotation.Timed;
import org.respondeco.respondeco.domain.ResourceOfferJoinResourceRequirement;
import org.respondeco.respondeco.repository.ResourceOfferJoinResourceRequirementRepository;
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
 * REST controller for managing ResourceOfferJoinResourceRequirement.
 */
@RestController
@RequestMapping("/app")
public class ResourceOfferJoinResourceRequirementResource {

    private final Logger log = LoggerFactory.getLogger(ResourceOfferJoinResourceRequirementResource.class);

    @Inject
    private ResourceOfferJoinResourceRequirementRepository resourceOfferJoinResourceRequirementRepository;

    /**
     * POST  /rest/resourceOfferJoinResourceRequirements -> Create a new resourceOfferJoinResourceRequirement.
     */
    @RequestMapping(value = "/rest/resourceOfferJoinResourceRequirements",
            method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public void create(@RequestBody ResourceOfferJoinResourceRequirement resourceOfferJoinResourceRequirement) {
        log.debug("REST request to save ResourceOfferJoinResourceRequirement : {}", resourceOfferJoinResourceRequirement);
        resourceOfferJoinResourceRequirementRepository.save(resourceOfferJoinResourceRequirement);
    }

    /**
     * GET  /rest/resourceOfferJoinResourceRequirements -> get all the resourceOfferJoinResourceRequirements.
     */
    @RequestMapping(value = "/rest/resourceOfferJoinResourceRequirements",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public List<ResourceOfferJoinResourceRequirement> getAll() {
        log.debug("REST request to get all ResourceOfferJoinResourceRequirements");
        return resourceOfferJoinResourceRequirementRepository.findAll();
    }

    /**
     * GET  /rest/resourceOfferJoinResourceRequirements/:id -> get the "id" resourceOfferJoinResourceRequirement.
     */
    @RequestMapping(value = "/rest/resourceOfferJoinResourceRequirements/{id}",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<ResourceOfferJoinResourceRequirement> get(@PathVariable Long id) {
        log.debug("REST request to get ResourceOfferJoinResourceRequirement : {}", id);
        return Optional.ofNullable(resourceOfferJoinResourceRequirementRepository.findOne(id))
            .map(resourceOfferJoinResourceRequirement -> new ResponseEntity<>(
                resourceOfferJoinResourceRequirement,
                HttpStatus.OK))
            .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    /**
     * DELETE  /rest/resourceOfferJoinResourceRequirements/:id -> delete the "id" resourceOfferJoinResourceRequirement.
     */
    @RequestMapping(value = "/rest/resourceOfferJoinResourceRequirements/{id}",
            method = RequestMethod.DELETE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public void delete(@PathVariable Long id) {
        log.debug("REST request to delete ResourceOfferJoinResourceRequirement : {}", id);
        resourceOfferJoinResourceRequirementRepository.delete(id);
    }
}
