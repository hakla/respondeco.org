package org.respondeco.respondeco.web.rest;

import javax.inject.Inject;
import javax.transaction.Transactional;
import javax.xml.ws.Response;

import com.codahale.metrics.annotation.Timed;
import com.wordnik.swagger.model.Operation;
import org.jadira.usertype.dateandtime.joda.columnmapper.StringColumnDateTimeZoneWithOffsetMapper;
import org.respondeco.respondeco.domain.SocialMediaConnection;
import org.respondeco.respondeco.service.SocialMediaService;
import org.respondeco.respondeco.service.exception.NoSuchUserException;
import org.respondeco.respondeco.service.exception.OperationForbiddenException;
import org.respondeco.respondeco.web.rest.dto.SocialMediaConnectionResponseDTO;
import org.respondeco.respondeco.web.rest.dto.StringDTO;
import org.respondeco.respondeco.web.rest.dto.TwitterConnectionDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.social.connect.Connection;
import org.springframework.social.facebook.api.Facebook;
import org.springframework.social.facebook.connect.FacebookConnectionFactory;
import org.springframework.social.oauth2.AccessGrant;
import org.springframework.social.oauth2.OAuth2Operations;
import org.springframework.social.oauth2.OAuth2Parameters;
import org.springframework.social.twitter.api.Twitter;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;

/**
 * SocialMediaController
 *
 * This controller handles all requests for the connection of the account with social media platforms
 * like facebook, twitter, google+, xing.
 */
@RestController
@RequestMapping("/app")
@Transactional
public class SocialMediaController {

    private final Logger log = LoggerFactory.getLogger(SocialMediaController.class);

    private Facebook facebook;

    private SocialMediaService socialMediaService;

    @Inject
    public SocialMediaController(SocialMediaService socialMediaService) {
        this.socialMediaService = socialMediaService;
    }

    /**
     * POST  /rest/connect/facebook
     * Post request to get the authorization URL for account connection with facebook.
     */
    @RequestMapping(value = "/rest/connect/facebook",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<?> connectFacebook(){

        ResponseEntity<?> responseEntity;

        String authorizeUrl = socialMediaService.createFacebookAuthorizationURL();
        //angular needs a wrapper for strings
        StringDTO url = new StringDTO(authorizeUrl);

        responseEntity = new ResponseEntity<>(url, HttpStatus.OK);
        log.debug("Facebook AuthorizationURL: " + authorizeUrl);

        return responseEntity;
    }

    /**
     * POST /rest/connect/facebook/createconnection
     * @param code contains the authorizationcode which is needed to get the user token from facebook
     * @return responseentity with HttpStatus.CREATED if the connection was successfully created or
     *         an appropriate error if the creation of the connection was not successful
     */
    @RequestMapping(value="/rest/connect/facebook/createconnection",
        method = RequestMethod.POST,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<?> createFacebookConnection(@RequestBody StringDTO code) {
        log.debug("REST request for creating a new facebook Connection with code: "+ code.getString());


        Connection<Facebook> connection = socialMediaService.createFacebookConnection(code.getString());


        return new ResponseEntity<Connection<Facebook>>(connection, HttpStatus.OK);
    }

    /*
    @RequestMapping(value="/rest/connect/google",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<StringDTO> connectTwitter() {

        String authorizationURL = socialMediaService.createGoogleAuthorizationURL();

        StringDTO url = new StringDTO(authorizationURL);

        return new ResponseEntity<>(url, HttpStatus.OK);
    }*/

    @RequestMapping(value="/rest/connect/twitter",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<?> connectTwitter() {

        String url = socialMediaService.createTwitterAuthorizationURL();
        StringDTO urlDTO = new StringDTO(url);

        return new ResponseEntity<Object>(urlDTO, HttpStatus.OK);
    }

    @RequestMapping(value="/rest/connect/twitter/createconnection",
        method = RequestMethod.POST,
        produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> createTwitterConnection(@RequestBody TwitterConnectionDTO twitterConnectionDTO) {
        ResponseEntity<?> responseEntity;

        String token = twitterConnectionDTO.getToken();
        String verifier = twitterConnectionDTO.getVerifier();
        try{
            Connection<Twitter> connection = socialMediaService.createTwitterConnection(token, verifier);

            responseEntity = new ResponseEntity<>(connection, HttpStatus.CREATED);
        } catch(OperationForbiddenException ex) {
            responseEntity = new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }

        return responseEntity;
    }

    @RequestMapping(value="/rest/twitter",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity postTweet() {

        try{
            socialMediaService.createTwitterPost();
        } catch (OperationForbiddenException ex) {

        }

        return new ResponseEntity(HttpStatus.OK);
    }

    /**
     * Returns all Connections of the currently logged in user or
     * HttpStatus.FORBIDDEN if no user is currently logged in
     * @return response entity containing a list of SocialMediaConnectionResponse DTOs and a HttpStatus
     */
    @RequestMapping(value="/rest/connections",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<SocialMediaConnectionResponseDTO>> getConnections() {
        ResponseEntity<List<SocialMediaConnectionResponseDTO>> responseEntity;

        try {
            List<SocialMediaConnection> connections = socialMediaService.getConnectionsForUser();
            List<SocialMediaConnectionResponseDTO> dtos = SocialMediaConnectionResponseDTO.fromEntities(connections, null);

            responseEntity = new ResponseEntity<>(dtos, HttpStatus.OK);
        } catch (OperationForbiddenException e) {
            responseEntity = new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }

        return responseEntity;
    }



}
