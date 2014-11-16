package org.respondeco.respondeco.web.rest;

import com.codahale.metrics.annotation.Timed;
import org.respondeco.respondeco.domain.ProjectLogo;
import org.respondeco.respondeco.repository.ProjectLogoRepository;
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
 * REST controller for managing ProjectLogo.
 */
@RestController
@RequestMapping("/app")
public class ProjectLogoController {

    private final Logger log = LoggerFactory.getLogger(ProjectLogoController.class);

    @Inject
    private ProjectLogoRepository projectlogoRepository;

    /**
     * POST  /rest/projectlogos -> Create a new projectlogo.
     */
    @RequestMapping(value = "/rest/projectlogos",
            method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public void create(@RequestBody ProjectLogo projectlogo) {
        log.debug("REST request to save ProjectLogo : {}", projectlogo);
        projectlogoRepository.save(projectlogo);
    }

    /**
     * GET  /rest/projectlogos -> get all the projectlogos.
     */
    @RequestMapping(value = "/rest/projectlogos",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public List<ProjectLogo> getAll() {
        log.debug("REST request to get all ProjectLogos");
        return projectlogoRepository.findAll();
    }

    /**
     * GET  /rest/projectlogos/:id -> get the "id" projectlogo.
     */
    @RequestMapping(value = "/rest/projectlogos/{id}",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<ProjectLogo> get(@PathVariable Long id) {
        log.debug("REST request to get ProjectLogo : {}", id);
        return Optional.ofNullable(projectlogoRepository.findOne(id))
            .map(projectlogo -> new ResponseEntity<>(
                projectlogo,
                HttpStatus.OK))
            .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    /**
     * DELETE  /rest/projectlogos/:id -> delete the "id" projectlogo.
     */
    @RequestMapping(value = "/rest/projectlogos/{id}",
            method = RequestMethod.DELETE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public void delete(@PathVariable Long id) {
        log.debug("REST request to delete ProjectLogo : {}", id);
        projectlogoRepository.delete(id);
    }
}
