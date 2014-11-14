package org.respondeco.respondeco.web.rest;

import com.codahale.metrics.annotation.Timed;
import com.wordnik.swagger.annotations.Api;
import com.wordnik.swagger.annotations.ApiOperation;
import org.respondeco.respondeco.domain.ProfilePicture;
import org.respondeco.respondeco.domain.User;
import org.respondeco.respondeco.repository.ProfilePictureRepository;
import org.respondeco.respondeco.repository.UserRepository;
import org.respondeco.respondeco.security.AuthoritiesConstants;
import org.respondeco.respondeco.service.ProfilePictureService;
import org.respondeco.respondeco.web.rest.dto.ProfilePictureDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.annotation.security.RolesAllowed;
import javax.inject.Inject;
import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.Optional;

/**
 * REST controller for managing ProfilePicture.
 */
@Api(value = "profilepictures", description = "Profile Picture API")
@RestController
@RequestMapping("/app")
public class ProfilePictureController {

    private final Logger log = LoggerFactory.getLogger(ProfilePictureController.class);

    private ProfilePictureRepository profilePictureRepository;
    private ProfilePictureService profilePictureService;
    private UserRepository userRepository;

    @Inject
    public ProfilePictureController(ProfilePictureRepository profilePictureRepository,
                                    ProfilePictureService profilePictureService,
                                    UserRepository userRepository) {
        this.profilePictureRepository = profilePictureRepository;
        this.profilePictureService = profilePictureService;
        this.userRepository = userRepository;
    }

    /**
     * POST  /rest/profilepictures -> Create a new profilepicture.
     */
    @ApiOperation(value = "Create profile picture", notes = "Create or change the current user's profile picture")
    @RequestMapping(value = "/rest/profilepictures",
            method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @RolesAllowed({AuthoritiesConstants.ADMIN, AuthoritiesConstants.USER})
    @Timed
    public ResponseEntity<?> create(@RequestBody ProfilePictureDTO profilePictureDTO) throws UnsupportedEncodingException {
        log.debug("REST request to save ProfilePicture : {}", profilePictureDTO);
        return Optional.ofNullable(
                profilePictureService.createProfilePicture(profilePictureDTO.getLabel(), profilePictureDTO.getData()))
                    .map(profilePicture -> new ResponseEntity<>(HttpStatus.OK))
                    .orElse(new ResponseEntity<>(HttpStatus.BAD_REQUEST));
    }

    /**
     * GET  /rest/profilepictures/:userlogin -> get the "userlogin" profilepicture.
     */
    @ApiOperation(value = "Get profile picture", notes = "Get the profile picture of the 'userlogin' user")
    @RequestMapping(value = "/rest/profilepictures/{userlogin}",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<ProfilePicture> get(@PathVariable String userlogin) {
        log.debug("REST request to get ProfilePicture : {}", userlogin);
        User user = userRepository.findByLogin(userlogin);
        return Optional.ofNullable(profilePictureRepository.findOne(user.getId()))
            .map(profilepicture -> new ResponseEntity<>(
                profilepicture,
                HttpStatus.OK))
            .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    /**
     * DELETE  /rest/profilepictures -> delete the profilepicture of the currently authenticated user.
     */
    @ApiOperation(value = "Delete profile picture", notes = "Delete the current user's profile picture")
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
    @ApiOperation(value = "Delete profile picture",
            notes = "Delete the profile picture of a specific user (admin only)")
    @RequestMapping(value = "/rest/profilepictures/{userlogin}",
            method = RequestMethod.DELETE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @RolesAllowed(AuthoritiesConstants.ADMIN)
    @Timed
    public void deleteOtherProfilePicture(@PathVariable String userlogin) {
        log.debug("REST request to delete ProfilePicture : {}", userlogin);
        User user = userRepository.findByLogin(userlogin);
        profilePictureRepository.delete(user.getId());
    }
}
