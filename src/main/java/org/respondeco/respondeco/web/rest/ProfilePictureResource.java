package org.respondeco.respondeco.web.rest;

import com.codahale.metrics.annotation.Timed;
import org.respondeco.respondeco.domain.ProfilePicture;
import org.respondeco.respondeco.repository.ProfilePictureRepository;
import org.respondeco.respondeco.security.AuthoritiesConstants;
import org.respondeco.respondeco.service.ProfilePictureService;
import org.respondeco.respondeco.web.rest.dto.ProfilePictureDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import sun.security.util.SecurityConstants;

import javax.annotation.security.RolesAllowed;
import javax.inject.Inject;
import java.io.UnsupportedEncodingException;
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
    private ProfilePictureRepository profilePictureRepository;

    @Inject
    private ProfilePictureService profilePictureService;

    /**
     * POST  /rest/profilepictures -> Create a new profilepicture.
     */
    @RequestMapping(value = "/rest/profilepictures",
            method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @RolesAllowed({AuthoritiesConstants.ADMIN, AuthoritiesConstants.USER})
    @Timed
    public void create(@RequestBody ProfilePictureDTO profilePictureDTO) throws UnsupportedEncodingException {
        log.debug("REST request to save ProfilePicture : {}", profilePictureDTO);
        profilePictureService.createProfilePicture(profilePictureDTO.getLabel(), profilePictureDTO.getData());
    }

    /**
     * GET  /rest/profilepictures -> get all the profilepictures.
     */
    @RequestMapping(value = "/rest/profilepictures",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @RolesAllowed(AuthoritiesConstants.ADMIN)
    @Timed
    public List<ProfilePicture> getAll() {
        log.debug("REST request to get all ProfilePictures");
        return profilePictureRepository.findAll();
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
        return Optional.ofNullable(profilePictureRepository.findOne(userlogin))
            .map(profilepicture -> new ResponseEntity<>(
                profilepicture,
                HttpStatus.OK))
            .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    /**
     * DELETE  /rest/profilepictures -> delete the profilepicture of the currently authenticated user.
     */
    @RequestMapping(value = "/rest/profilepictures",
            method = RequestMethod.DELETE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @RolesAllowed({AuthoritiesConstants.ADMIN, AuthoritiesConstants.USER})
    @Timed
    public void deleteOwnProfilePicture() {
        log.debug("REST request to delete ProfilePicture of current user");
        profilePictureService.deleteProfilePictureCurrentUser();
    }

    /**
     * DELETE  /rest/profilepictures/:id -> delete the "id" profilepicture.
     */
    @RequestMapping(value = "/rest/profilepictures/{userlogin}",
            method = RequestMethod.DELETE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @RolesAllowed(AuthoritiesConstants.ADMIN)
    @Timed
    public void deleteOtherProfilePicture(@PathVariable String userlogin) {
        log.debug("REST request to delete ProfilePicture : {}", userlogin);
        profilePictureRepository.delete(userlogin);
    }
}
