package org.respondeco.respondeco.web.rest;

import com.codahale.metrics.annotation.Timed;
import org.respondeco.respondeco.domain.ResourceOffer;
import org.respondeco.respondeco.repository.ResourceOfferRepository;
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
 * REST controller for managing ResourceOffer.
 */
@RestController
@RequestMapping("/app")
public class ResourceOfferResource {

    private final Logger log = LoggerFactory.getLogger(ResourceOfferResource.class);

    @Inject
    private ResourceOfferRepository resourceOfferRepository;

    /**
     * POST  /rest/resourceOffers -> Create a new resourceOffer.
     */
    @RequestMapping(value = "/rest/resourceOffers",
            method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public void create(@RequestBody ResourceOffer resourceOffer) {
        log.debug("REST request to save ResourceOffer : {}", resourceOffer);
        resourceOfferRepository.save(resourceOffer);
    }

    /**
     * GET  /rest/resourceOffers -> get all the resourceOffers.
     */
    @RequestMapping(value = "/rest/resourceOffers",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public List<ResourceOffer> getAll() {
        log.debug("REST request to get all ResourceOffers");
        return resourceOfferRepository.findAll();
    }

    /**
     * GET  /rest/resourceOffers/:id -> get the "id" resourceOffer.
     */
    @RequestMapping(value = "/rest/resourceOffers/{id}",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<ResourceOffer> get(@PathVariable Long id) {
        log.debug("REST request to get ResourceOffer : {}", id);
        return Optional.ofNullable(resourceOfferRepository.findOne(id))
            .map(resourceOffer -> new ResponseEntity<>(
                resourceOffer,
                HttpStatus.OK))
            .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    /**
     * DELETE  /rest/resourceOffers/:id -> delete the "id" resourceOffer.
     */
    @RequestMapping(value = "/rest/resourceOffers/{id}",
            method = RequestMethod.DELETE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public void delete(@PathVariable Long id) {
        log.debug("REST request to delete ResourceOffer : {}", id);
        resourceOfferRepository.delete(id);
    }
}
