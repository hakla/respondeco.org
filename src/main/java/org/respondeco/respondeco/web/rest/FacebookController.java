package org.respondeco.respondeco.web.rest;

import javax.inject.Inject;
import javax.transaction.Transactional;

import com.codahale.metrics.annotation.Timed;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.social.connect.Connection;
import org.springframework.social.facebook.api.Facebook;
import org.springframework.social.facebook.api.PagedList;
import org.springframework.social.facebook.api.PostData;
import org.springframework.social.facebook.connect.FacebookConnectionFactory;
import org.springframework.social.oauth2.AccessGrant;
import org.springframework.social.oauth2.OAuth2Operations;
import org.springframework.social.oauth2.OAuth2Parameters;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/app")
@Transactional
public class FacebookController {

    private Facebook facebook;

    @Inject
    public FacebookController(Facebook facebook) {
        this.facebook = facebook;
    }

    /**
     * POST  /rest/register -> register the user.
     */
    @RequestMapping(value = "/rest/facebook",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<?> connectFacebook(){

        ResponseEntity<?> responseEntity = new ResponseEntity<Object>(HttpStatus.OK);
        FacebookConnectionFactory connectionFactory = new FacebookConnectionFactory("801386343241847","0ee624ec572168e2c8af19e4fd870cab");
        OAuth2Operations oauthOperations = connectionFactory.getOAuthOperations();
        OAuth2Parameters params = new OAuth2Parameters();
        params.setRedirectUri("http://localhost:9000/#/");
        params.setScope("publish_actions");

        String authorizeUrl = oauthOperations.buildAuthorizeUrl(params);

        System.out.println("AUTHORIZEURL: " + authorizeUrl);

        return responseEntity;
    }

    @RequestMapping(value="/rest/facebook/connect",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<?> createConnection(@RequestParam String code) {

        ResponseEntity<?> responseEntity = new ResponseEntity<Object>(HttpStatus.OK);
        FacebookConnectionFactory connectionFactory = new FacebookConnectionFactory("801386343241847","0ee624ec572168e2c8af19e4fd870cab");
        OAuth2Operations oauthOperations = connectionFactory.getOAuthOperations();


        AccessGrant accessGrant = oauthOperations.exchangeForAccess(code, "http://localhost:9000/#/", null);
        Connection<Facebook> connection = connectionFactory.createConnection(accessGrant);

        System.out.println("DISPLAYNAME: " + connection.getApi().feedOperations().updateStatus("hallo ich bin gerade auf respondeco.org"));

        return new ResponseEntity<Connection<Facebook>>(connection, HttpStatus.OK);

    }


}
