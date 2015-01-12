package org.respondeco.respondeco.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.env.Environment;
import org.springframework.social.connect.Connection;
import org.springframework.social.connect.ConnectionData;
import org.springframework.social.facebook.api.Facebook;
import org.springframework.social.facebook.connect.FacebookConnectionFactory;
import org.springframework.social.oauth2.AccessGrant;
import org.springframework.social.oauth2.OAuth2Operations;
import org.springframework.social.oauth2.OAuth2Parameters;
import org.springframework.stereotype.Service;

import javax.inject.Inject;

/**
 * Created by Benjamin Fraller on 12.01.2015.
 */
@Service
public class SocialMediaService {

    private final Logger log = LoggerFactory.getLogger(SocialMediaService.class);

    private Environment env;

    private FacebookConnectionFactory facebookConnectionFactory;

    @Inject
    public SocialMediaService(Environment env){
        this.env = env;

        facebookConnectionFactory = new FacebookConnectionFactory(env.getProperty("spring.social.facebook.appId"),
            env.getProperty("spring.social.facebook.appSecret"));

    }


    /**
     * Creates the authorization URL for facebook
     * @return Authorization URL as String
     */
    public String createFacebookAuthorizationURL() {
        log.debug("Creating Facebook Authorization URL");
        OAuth2Operations oauthOperations = facebookConnectionFactory.getOAuthOperations();
        OAuth2Parameters params = new OAuth2Parameters();
        params.setRedirectUri(env.getProperty("spring.social.facebook.redirectUrl"));
        params.setScope("publish_actions");

        String authorizationUrl = oauthOperations.buildAuthorizeUrl(params);
        log.debug("AuthorizationURL: " + authorizationUrl);

        return authorizationUrl;
    }

    /**
     * Creates a new FacebookConnection
     * The given code is exchanged for a access token from facebook. With help of this access token we can create
     * a new facebook connection and afterwards we can interact with the users facebook account.
     * @param code code is created after the user grants permission for our app to use his facebook account.
     *             this code is then used to exchange it for the users access token from facebook
     */
    public Connection<Facebook> createFacebookConnection(String code) {
        OAuth2Operations oauthOperations = facebookConnectionFactory.getOAuthOperations();

        //change code for AccessGrant Token
        AccessGrant accessGrant = oauthOperations.exchangeForAccess(code, env.getProperty("spring.social.facebook.redirectUrl"), null);
        Connection<Facebook> connection = facebookConnectionFactory.createConnection(accessGrant);
        log.debug("FacebookConnection: " + connection.toString());



        //connection.getApi().feedOperations().updateStatus("hallo ich bin gerade auf respondeco.org");

        ConnectionData connectionData = connection.createData();
        log.debug("CONNECTION: ");
        log.debug("ACCESSTOKEN: "+connectionData.getAccessToken());
        log.debug("PROVIDERID: " + connectionData.getProviderId());
        log.debug("PROVIDERUSERID: "+connectionData.getProviderUserId());
        log.debug("SECRET: "+connectionData.getRefreshToken());
        log.debug("EXPIRATIONTIME: " + connectionData.getExpireTime());

        ConnectionData newConnectionData = new ConnectionData(null,null,null
            ,null,null, connectionData.getAccessToken(), null, null, null);

        Connection<Facebook> newConn = facebookConnectionFactory.createConnection(newConnectionData);
        newConn.getApi().feedOperations().updateStatus("YO TEST");

        return connection;
    }

}
