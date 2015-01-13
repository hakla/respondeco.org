package org.respondeco.respondeco.service;

import org.respondeco.respondeco.domain.SocialMediaConnection;
import org.respondeco.respondeco.domain.User;
import org.respondeco.respondeco.repository.SocialMediaRepository;
import org.respondeco.respondeco.service.exception.ConnectionAlreadyExistsException;
import org.respondeco.respondeco.service.exception.NoSuchSocialMediaConnectionException;
import org.respondeco.respondeco.service.exception.OperationForbiddenException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.env.Environment;
import org.springframework.social.connect.Connection;
import org.springframework.social.connect.ConnectionData;
import org.springframework.social.facebook.api.Facebook;
import org.springframework.social.facebook.connect.FacebookConnectionFactory;
import org.springframework.social.google.connect.GoogleConnectionFactory;
import org.springframework.social.oauth1.AuthorizedRequestToken;
import org.springframework.social.oauth1.OAuth1Operations;
import org.springframework.social.oauth1.OAuth1Parameters;
import org.springframework.social.oauth1.OAuthToken;
import org.springframework.social.oauth2.AccessGrant;
import org.springframework.social.oauth2.OAuth2Operations;
import org.springframework.social.oauth2.OAuth2Parameters;
import org.springframework.social.twitter.api.Tweet;
import org.springframework.social.twitter.api.Twitter;
import org.springframework.social.twitter.connect.TwitterConnectionFactory;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.util.List;

/**
 * Created by Benjamin Fraller on 12.01.2015.
 */
@Service
public class SocialMediaService {

    private final Logger log = LoggerFactory.getLogger(SocialMediaService.class);

    private Environment env;
    private SocialMediaRepository socialMediaRepository;

    private UserService userService;


    private FacebookConnectionFactory facebookConnectionFactory;
    private TwitterConnectionFactory twitterConnectionFactory;
    private GoogleConnectionFactory googleConnectionFactory;

    @Inject
    public SocialMediaService(Environment env, SocialMediaRepository socialMediaRepository, UserService userService){
        this.env = env;
        this.socialMediaRepository = socialMediaRepository;
        this.userService = userService;

        facebookConnectionFactory = new FacebookConnectionFactory(env.getProperty("spring.social.facebook.appId"),
            env.getProperty("spring.social.facebook.appSecret"));

        twitterConnectionFactory = new TwitterConnectionFactory(env.getProperty("spring.social.twitter.appId"),
            env.getProperty("spring.social.twitter.appSecret"));

        googleConnectionFactory = new GoogleConnectionFactory(env.getProperty("spring.social.google.clientId"),
            env.getProperty("spring.social.google.clientSecret"));



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
    public SocialMediaConnection createFacebookConnection(String code) throws OperationForbiddenException{
        User user = userService.getUserWithAuthorities();
        if(user == null) {
            throw new OperationForbiddenException("no current user found");
        }

        OAuth2Operations oauthOperations = facebookConnectionFactory.getOAuthOperations();

        //change code for AccessGrant Token
        AccessGrant accessGrant = oauthOperations.exchangeForAccess(code, env.getProperty("spring.social.facebook.redirectUrl"), null);
        Connection<Facebook> connection = facebookConnectionFactory.createConnection(accessGrant);
        log.debug("FacebookConnection: " + connection.toString());

        ConnectionData connectionData = connection.createData();

        SocialMediaConnection socialMediaConnection = new SocialMediaConnection();
        socialMediaConnection.setProvider("facebook");
        socialMediaConnection.setToken(connectionData.getAccessToken());
        socialMediaConnection.setUser(user);
        socialMediaConnection = socialMediaRepository.save(socialMediaConnection);

        return socialMediaConnection;
    }


    /**
     * Creates a new post on facebook on behalf of the user if
     * his account is connected to facebook.
     * @param post
     * @throws OperationForbiddenException
     */
    public String createFacebookPost(String post) throws OperationForbiddenException, NoSuchSocialMediaConnectionException {
        User user = userService.getUserWithAuthorities();
        if(user == null) {
            throw new OperationForbiddenException("no current user found");
        }

        SocialMediaConnection socialMediaConnection = socialMediaRepository.findByUserAndProvider(user, "facebook");
        if(socialMediaConnection == null) {
            throw new NoSuchSocialMediaConnectionException("no connection found for user with id: " + user.getId());
        }

        //create connection with help of socialMediaConnection
        ConnectionData connectionData = new ConnectionData(null,null,null
            ,null,null, socialMediaConnection.getToken(), null, null, null);

        Connection<Facebook> connection = facebookConnectionFactory.createConnection(connectionData);
        String status = connection.getApi().feedOperations().updateStatus(post);

        return status;
    }




    /**
     * Creates the Authorization URL for the user if he wants to connect his respondeco account
     * with his Twitter account
     * @return Authorization URL as String
     */
    public String createTwitterAuthorizationURL() {
        log.debug("Creating Twitter Authorization URL");

        OAuth1Operations oauthOperations = twitterConnectionFactory.getOAuthOperations();
        OAuthToken requestToken = oauthOperations.fetchRequestToken(env.getProperty("spring.social.twitter.redirectUrl"), null);
        String authorizeUrl = oauthOperations.buildAuthorizeUrl(requestToken.getValue(), OAuth1Parameters.NONE);

        log.debug("Secret:" +requestToken.getSecret());
        log.debug("AuthorizeURL: " + authorizeUrl);

        return authorizeUrl;
    }

    /**
     * Creates a Twitter Connection with help of the token and oauthVerifier from the user.
     * @param token user token which is used to create the connection to the users twitter account
     * @param oauthVerifier used to verify the token
     * @return Returns a twitter connection, therefor our app can interact with the api on behalf of the user
     */
    public SocialMediaConnection createTwitterConnection(String token, String oauthVerifier)
        throws OperationForbiddenException, ConnectionAlreadyExistsException{

        //check if user is logged in
        User user = userService.getUserWithAuthorities();
        if(user == null) {
            throw new OperationForbiddenException("no current user found");
        }

        //check if connection already exists
        SocialMediaConnection socialMediaConnection = socialMediaRepository.findByUserAndProvider(user, "twitter");
        if(socialMediaConnection != null) {
            throw new ConnectionAlreadyExistsException("connection for user " + user.getId() + " with twitter already exists");
        }

        //if no connection exists -> create new connection with oauth1
        OAuthToken requestToken = new OAuthToken(token,null);
        OAuth1Operations oauthOperations = twitterConnectionFactory.getOAuthOperations();
        OAuthToken accessToken = oauthOperations.exchangeForAccessToken(
            new AuthorizedRequestToken(requestToken, oauthVerifier),null);

        Connection<Twitter> connection = twitterConnectionFactory.createConnection(accessToken);

        socialMediaConnection = new SocialMediaConnection();
        socialMediaConnection.setProvider("twitter");
        socialMediaConnection.setToken(accessToken.getValue());
        socialMediaConnection.setSecret(accessToken.getSecret());
        socialMediaConnection.setUser(user);
        socialMediaConnection = socialMediaRepository.save(socialMediaConnection);

        //try to post
        //SocialMediaConnection c = socialMediaRepository.getOne(0L);
        //Connection<Twitter> newCon = twitterConnectionFactory.createConnection(new ConnectionData(
        //    c.getProvider(), null, null, null, null, c.getToken(), null, null, null)
        //);


        //newCon.getApi().timelineOperations().updateStatus("asdfasdf");

        return socialMediaConnection;
    }


    /**
     * Creates a new Tweet on Twitter if the user account is connected with Twitter.
     * @param post message to post.
     * @return created Tweet object.
     * @throws OperationForbiddenException if user is not logged in.
     * @throws NoSuchSocialMediaConnectionException if users account is not connected with twitter.
     */
     public Tweet createTwitterPost(String post) throws OperationForbiddenException, NoSuchSocialMediaConnectionException {
        User user = userService.getUserWithAuthorities();

        if(user == null) {
            throw new OperationForbiddenException("no current user found");
        }

        SocialMediaConnection socialMediaConnection = socialMediaRepository.findByUserAndProvider(user, "twitter");
        if(socialMediaConnection == null) {
            throw new NoSuchSocialMediaConnectionException("no connection found for user with id: " + user.getId());
        }

        Connection<Twitter> connection = twitterConnectionFactory.createConnection(new ConnectionData(
            socialMediaConnection.getProvider(), null, null, null, null, socialMediaConnection.getToken(), socialMediaConnection.getSecret(), null, null)
        );

        Tweet tweet = connection.getApi().timelineOperations().updateStatus(post);

        return tweet;
    }


    /**
     * Returns a List of social media connection for the current user
     * @return List of social medie aconnections
     * @throws OperationForbiddenException if user is not logged in
     */
    public List<SocialMediaConnection> getConnectionsForUser() throws OperationForbiddenException {

        User user = userService.getUserWithAuthorities();
        if(user == null) {
            throw new OperationForbiddenException("no current user found");
        }
        List<SocialMediaConnection> connections = socialMediaRepository.findByUser(user);

        return connections;
    }

    /**
     * Create Authorization URL for the client to allow access for respondeco
     * @return AuthorizationURL as String
     *
    public String createGoogleAuthorizationURL() {
    OAuth2Operations oauthOperations = googleConnectionFactory.getOAuthOperations();
    OAuth2Parameters params = new OAuth2Parameters();
    params.setRedirectUri(env.getProperty("spring.social.google.redirectUrl"));
    params.setScope("https://www.googleapis.com/auth/plus.login");

    String authorizationURL = oauthOperations.buildAuthenticateUrl(params);

    return authorizationURL;
    }


    public Connection<Google> createGoogleConnection(String code) {

    OAuth2Operations oauthOperations = googleConnectionFactory.getOAuthOperations();
    AccessGrant accessGrant = oauthOperations.exchangeForAccess(code, env.getProperty("spring.social.google.redirectUrl"), null);
    Connection<Google> connection = googleConnectionFactory.createConnection(accessGrant);

    connection.getApi().plusOperations().


    }
     */

}
