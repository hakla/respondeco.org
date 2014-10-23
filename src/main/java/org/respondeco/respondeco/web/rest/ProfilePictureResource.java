package org.respondeco.respondeco.web.rest;

import com.codahale.metrics.annotation.Timed;
import org.respondeco.respondeco.domain.ProfilePicture;
import org.respondeco.respondeco.repository.ProfilePictureRepository;
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
 * REST controller for managing ProfilePicture.
 */
@RestController
@RequestMapping("/app")
public class ProfilePictureResource {

    private final Logger log = LoggerFactory.getLogger(ProfilePictureResource.class);

    @Inject
    private ProfilePictureRepository profilepictureRepository;

    /**
     * POST  /rest/profilepictures -> Create a new profilepicture.
     */
    @RequestMapping(value = "/rest/profilepictures",
            method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public void create(@RequestBody ProfilePicture profilepicture) {
        log.debug("REST request to save ProfilePicture : {}", profilepicture);
        profilepictureRepository.save(profilepicture);
    }

    /**
     * GET  /rest/profilepictures -> get all the profilepictures.
     */
    @RequestMapping(value = "/rest/profilepictures",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public List<ProfilePicture> getAll() {
        log.debug("REST request to get all ProfilePictures");
        return profilepictureRepository.findAll();
    }

    /**
     * GET  /rest/profilepictures/:id -> get the "id" profilepicture.
     */
    @RequestMapping(value = "/rest/profilepictures/{userlogin}",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<ProfilePicture> get(@PathVariable String userlogin) {
        log.debug("REST request to get ProfilePicture : {}", userlogin);
        return Optional.ofNullable(profilepictureRepository.findOne(userlogin))
            .map(profilepicture -> new ResponseEntity<>(
                profilepicture,
                HttpStatus.OK))
            .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    /**
     * DELETE  /rest/profilepictures/:id -> delete the "id" profilepicture.
     */
    @RequestMapping(value = "/rest/profilepictures/{userlogin}",
            method = RequestMethod.DELETE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public void delete(@PathVariable String userlogin) {
        log.debug("REST request to delete ProfilePicture : {}", userlogin);
        profilepictureRepository.delete(userlogin);
    }
}
