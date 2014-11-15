package org.respondeco.respondeco.web.rest;

import com.codahale.metrics.annotation.Timed;
import org.respondeco.respondeco.domain.Project;
import org.respondeco.respondeco.repository.ProjectRepository;
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
 * REST controller for managing ProjectIdea.
 */
@RestController
@RequestMapping("/app")
public class ProjectContoller {

    private final Logger log = LoggerFactory.getLogger(ProjectContoller.class);

    @Inject
    private ProjectRepository projectideaRepository;

    /**
     * POST  /rest/projectideas -> Create a new projectidea.
     */
    @RequestMapping(value = "/rest/projectideas",
            method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public void create(@RequestBody Project projectidea) {
        log.debug("REST request to save ProjectIdea : {}", projectidea);
        projectideaRepository.save(projectidea);
    }

    /**
     * GET  /rest/projectideas -> get all the projectideas.
     */
    @RequestMapping(value = "/rest/projectideas",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public List<Project> getAll() {
        log.debug("REST request to get all ProjectIdeas");
        return projectideaRepository.findAll();
    }

    /**
     * GET  /rest/projectideas/:id -> get the "id" projectidea.
     */
    @RequestMapping(value = "/rest/projectideas/{id}",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Project> get(@PathVariable Long id) {
        log.debug("REST request to get ProjectIdea : {}", id);
        return Optional.ofNullable(projectideaRepository.findOne(id))
            .map(projectidea -> new ResponseEntity<>(
                projectidea,
                HttpStatus.OK))
            .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    /**
     * DELETE  /rest/projectideas/:id -> delete the "id" projectidea.
     */
    @RequestMapping(value = "/rest/projectideas/{id}",
            method = RequestMethod.DELETE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public void delete(@PathVariable Long id) {
        log.debug("REST request to delete ProjectIdea : {}", id);
        projectideaRepository.delete(id);
    }
}
