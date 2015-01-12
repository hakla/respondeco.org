package org.respondeco.respondeco.web.rest;

import javax.inject.Inject;
import javax.transaction.Transactional;

import com.codahale.metrics.annotation.Timed;
import org.respondeco.respondeco.service.SocialMediaService;
import org.respondeco.respondeco.web.rest.dto.StringDTO;
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
import org.springframework.web.bind.annotation.*;

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

        ResponseEntity<?> responseEntity = new ResponseEntity<Object>(HttpStatus.OK);
        FacebookConnectionFactory connectionFactory = new FacebookConnectionFactory("801386343241847","0ee624ec572168e2c8af19e4fd870cab");
        OAuth2Operations oauthOperations = connectionFactory.getOAuthOperations();
        OAuth2Parameters params = new OAuth2Parameters();
        params.setRedirectUri("http://localhost:9000/#/social-networks");
        params.setScope("publish_actions");

        String authorizeUrl = oauthOperations.buildAuthorizeUrl(params);
        //angular needs a wrapper for strings
        StringDTO url = new StringDTO();
        url.setString(authorizeUrl);

        responseEntity = new ResponseEntity<>(url, HttpStatus.OK);
        log.debug("AUTHORIZEURL: " + authorizeUrl);

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
    public ResponseEntity<?> createConnection(@RequestParam String code) {
       // log.debug("TESTTT "+ code.getString());

        ResponseEntity<?> responseEntity = new ResponseEntity<Object>(HttpStatus.OK);
        FacebookConnectionFactory connectionFactory = new FacebookConnectionFactory("801386343241847","0ee624ec572168e2c8af19e4fd870cab");
        OAuth2Operations oauthOperations = connectionFactory.getOAuthOperations();


        AccessGrant accessGrant = oauthOperations.exchangeForAccess(code, "http://localhost:9000/#/social-networks", null);
        Connection<Facebook> connection = connectionFactory.createConnection(accessGrant);

        System.out.println("DISPLAYNAME: " + connection.getApi().feedOperations().updateStatus("hallo ich bin gerade auf respondeco.org"));

        return new ResponseEntity<Connection<Facebook>>(connection, HttpStatus.OK);
    }


}
