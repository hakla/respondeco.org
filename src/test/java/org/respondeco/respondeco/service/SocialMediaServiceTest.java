package org.respondeco.respondeco.service;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.respondeco.respondeco.Application;
import org.respondeco.respondeco.domain.SocialMediaConnection;
import org.respondeco.respondeco.domain.User;
import org.respondeco.respondeco.repository.SocialMediaRepository;
import org.respondeco.respondeco.service.exception.ConnectionAlreadyExistsException;
import org.respondeco.respondeco.service.exception.NoSuchSocialMediaConnectionException;
import org.respondeco.respondeco.service.exception.OperationForbiddenException;
import org.scribe.model.Token;
import org.scribe.model.Verifier;
import org.scribe.oauth.OAuthService;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.core.env.Environment;
import org.springframework.social.connect.Connection;
import org.springframework.social.connect.ConnectionData;
import org.springframework.social.facebook.api.Facebook;
import org.springframework.social.facebook.api.FacebookLink;
import org.springframework.social.facebook.api.FeedOperations;
import org.springframework.social.facebook.connect.FacebookConnectionFactory;
import org.springframework.social.oauth1.AuthorizedRequestToken;
import org.springframework.social.oauth1.OAuth1Operations;
import org.springframework.social.oauth1.OAuth1Parameters;
import org.springframework.social.oauth1.OAuthToken;
import org.springframework.social.oauth2.AccessGrant;
import org.springframework.social.oauth2.OAuth2Operations;
import org.springframework.social.oauth2.OAuth2Parameters;
import org.springframework.social.twitter.api.TimelineOperations;
import org.springframework.social.twitter.api.Tweet;
import org.springframework.social.twitter.api.Twitter;
import org.springframework.social.twitter.connect.TwitterConnectionFactory;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.*;

/**
 * Test class for SocialMediaService
 *
 * @see org.respondeco.respondeco.service.SocialMediaService
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Application.class)
@WebAppConfiguration
public class SocialMediaServiceTest {

    @Mock
    private Environment environmentMock;

    @Mock
    private SocialMediaRepository socialMediaRepositoryMock;

    @Mock
    private UserService userServiceMock;

    @Mock
    private FacebookConnectionFactory facebookConnectionFactoryMock;

    @Mock
    private TwitterConnectionFactory twitterConnectionFactoryMock;

    @Mock
    private OAuthService xingServiceMock;


    private SocialMediaService socialMediaService;

    private OAuth2Operations oAuth2Operations;
    private Connection<Facebook> facebookConnection;
    private ConnectionData connectionData;
    private SocialMediaConnection facebookSocialMediaConnection;

    private SocialMediaConnection twitterSocialMediaConnection;
    private OAuth1Operations oAuth1Operations;
    private Connection<Twitter> twitterConnection;

    private SocialMediaConnection xingSocialMediaConnection;

    private User user;

    @Before
    public void setup() throws Exception {
        MockitoAnnotations.initMocks(this);
        this.socialMediaService = new SocialMediaService(environmentMock, socialMediaRepositoryMock, userServiceMock,
            facebookConnectionFactoryMock, twitterConnectionFactoryMock, xingServiceMock);

        user = new User();
        user.setId(1L);
        user.setFirstName("firstname");
        user.setLastName("lastname");

        facebookSocialMediaConnection = new SocialMediaConnection();
        facebookSocialMediaConnection.setId(1L);
        facebookSocialMediaConnection.setUser(user);
        facebookSocialMediaConnection.setToken("accesstoken");
        facebookSocialMediaConnection.setProvider("facebook");

        twitterSocialMediaConnection = new SocialMediaConnection();
        twitterSocialMediaConnection.setId(2L);
        twitterSocialMediaConnection.setUser(user);
        twitterSocialMediaConnection.setToken("token");
        twitterSocialMediaConnection.setSecret("secret");
        twitterSocialMediaConnection.setProvider("twitter");

        xingSocialMediaConnection = new SocialMediaConnection();
        xingSocialMediaConnection.setId(3L);
        xingSocialMediaConnection.setUser(user);
        xingSocialMediaConnection.setToken("token");
        xingSocialMediaConnection.setSecret("secret");
        xingSocialMediaConnection.setProvider("xing");

        //create needed mocks
        //mocks for facebook
        oAuth2Operations = mock(OAuth2Operations.class);
        doReturn(new AccessGrant("accesstoken")).when(oAuth2Operations).exchangeForAccess(anyString(), anyString(),
            any());

        connectionData = mock(ConnectionData.class);
        doReturn("accesstoken").when(connectionData).getAccessToken();

        facebookConnection = (Connection<Facebook>)mock(Connection.class);
        doReturn(connectionData).when(facebookConnection).createData();

        OAuthToken accessToken = new OAuthToken("accesstoken","secret");

        //mocks for twitter
        oAuth1Operations = mock(OAuth1Operations.class);
        doReturn(accessToken).when(oAuth1Operations).exchangeForAccessToken(any(AuthorizedRequestToken.class), any());

        twitterConnection = (Connection<Twitter>)mock(Connection.class);
    }

    @Test
    public void testCreateFacebookAuthorizationURL_shouldCreateURL() throws Exception {
        String expectedUrl = "www.authorizeurl.com";
        doReturn(oAuth2Operations).when(facebookConnectionFactoryMock).getOAuthOperations();

        doReturn("redirect.url").when(environmentMock).getProperty("spring.social.facebook.redirectUrl");
        doReturn(expectedUrl).when(oAuth2Operations).buildAuthorizeUrl(any(OAuth2Parameters.class));

        String url = socialMediaService.createFacebookAuthorizationURL();
        assertEquals(expectedUrl, url);
    }

    @Test
    public void testCreateFacebookConnection_shouldCreateSocialMediaConnection() throws Exception {
        doReturn(user).when(userServiceMock).getUserWithAuthorities();

        doReturn(oAuth2Operations).when(facebookConnectionFactoryMock).getOAuthOperations();
        doReturn(facebookConnection).when(facebookConnectionFactoryMock).createConnection(any(AccessGrant.class));

        doAnswer(invocation -> {
            Object[] args = invocation.getArguments();

            SocialMediaConnection socialMediaConnection = (SocialMediaConnection) args[0];
            return socialMediaConnection;
        }).when(socialMediaRepositoryMock).save(any(SocialMediaConnection.class));

        SocialMediaConnection socialMediaConnection = socialMediaService.createFacebookConnection("code");

        verify(socialMediaRepositoryMock, times(1)).save(any(SocialMediaConnection.class));
        assertEquals(socialMediaConnection.getUser().getId().longValue(), 1L);
        assertEquals(socialMediaConnection.getProvider(),"facebook");
        assertEquals(socialMediaConnection.getToken(), "accesstoken");
    }

    @Test(expected = OperationForbiddenException.class)
    public void testCreateFacebookConnection_shouldThrowOperationForbiddenException() throws Exception {
        doReturn(null).when(userServiceMock).getUserWithAuthorities();

        socialMediaService.createFacebookConnection("code");
    }

    @Test
    public void testCreateFacebookPost_shouldCreateFacebookPost() throws Exception {
        doReturn(user).when(userServiceMock).getUserWithAuthorities();

        doReturn(facebookSocialMediaConnection).when(socialMediaRepositoryMock).findByUserAndProvider(user, "facebook");
        doReturn(facebookConnection).when(facebookConnectionFactoryMock).createConnection(any(ConnectionData.class));

        //mock connection.getApi().feedOperations().updateStatus(post);
        Facebook facebook = mock(Facebook.class);
        FeedOperations feedOperations = mock(FeedOperations.class);
        String postId = "123456789";

        doReturn(facebook).when(facebookConnection).getApi();
        doReturn(feedOperations).when(facebook).feedOperations();
        doReturn(postId).when(feedOperations).postLink(anyString(), any(FacebookLink.class));

        postId = socialMediaService.createFacebookPost("urlpath","post");

        verify(socialMediaRepositoryMock, times(1)).findByUserAndProvider(user,"facebook");
        assertEquals(postId, "123456789");
    }

    @Test
    public void testCreateTwitterAuthorizationURL_shouldCreateURL() throws Exception {
        String expectedUrl = "www.authorizeurl.fake";

        doReturn(oAuth1Operations).when(twitterConnectionFactoryMock).getOAuthOperations();
        doReturn(new OAuthToken("token", null)).when(oAuth1Operations).fetchRequestToken(anyString(), any());
        doReturn(expectedUrl).when(oAuth1Operations).buildAuthorizeUrl("token", OAuth1Parameters.NONE);

        String url = socialMediaService.createTwitterAuthorizationURL();

        assertEquals(expectedUrl, url);
    }

    @Test
    public void testCreateTwitterConnection_shouldCreateTwitterConnection() throws Exception {
        doReturn(user).when(userServiceMock).getUserWithAuthorities();
        doReturn(null).when(socialMediaRepositoryMock).findByUserAndProvider(user, "twitter");

        doReturn(twitterConnection).when(twitterConnectionFactoryMock).createConnection(any(OAuthToken.class));
        doReturn(oAuth1Operations).when(twitterConnectionFactoryMock).getOAuthOperations();

        doAnswer(invocation -> {
            Object[] args = invocation.getArguments();
            SocialMediaConnection socialMediaConnection = (SocialMediaConnection) args[0];
            socialMediaConnection.setId(2L);

            return socialMediaConnection;
        }).when(socialMediaRepositoryMock).save(any(SocialMediaConnection.class));

        SocialMediaConnection socialMediaConnection = socialMediaService.createTwitterConnection("token", "verifier");

        verify(socialMediaRepositoryMock, times(1)).save(any(SocialMediaConnection.class));
        assertEquals(socialMediaConnection.getId().longValue(), 2L);
        assertEquals(socialMediaConnection.getProvider(), "twitter");
        assertEquals(socialMediaConnection.getToken(), "accesstoken");
        assertEquals(socialMediaConnection.getSecret(), "secret");
    }

    @Test(expected = ConnectionAlreadyExistsException.class)
    public void testCreateTwitterConnection_shouldThrowConnectionAlreadyExistsException() throws Exception {
        doReturn(user).when(userServiceMock).getUserWithAuthorities();
        doReturn(twitterSocialMediaConnection).when(socialMediaRepositoryMock).findByUserAndProvider(user, "twitter");

        socialMediaService.createTwitterConnection("token","verifier");
    }

    @Test(expected = OperationForbiddenException.class)
    public void testCreateTwitterConnection_shouldThrowOperationForbiddenException() throws Exception {
        doReturn(null).when(userServiceMock).getUserWithAuthorities();
        socialMediaService.createTwitterConnection("token","verifier");
    }

    @Test
    public void testCreateTwitterPost_shouldCreateTwitterPost() throws Exception {
        doReturn(user).when(userServiceMock).getUserWithAuthorities();
        doReturn(twitterSocialMediaConnection).when(socialMediaRepositoryMock).findByUserAndProvider(user,"twitter");

        doReturn(twitterConnection).when(twitterConnectionFactoryMock).createConnection(any(ConnectionData.class));


        Twitter conn = mock(Twitter.class);
        TimelineOperations timelineOperations = mock(TimelineOperations.class);
        Tweet tweet = new Tweet(1L, "post", null,null,null,null,1L ,null, null);

        doReturn(conn).when(twitterConnection).getApi();
        doReturn(timelineOperations).when(conn).timelineOperations();
        doReturn(tweet).when(timelineOperations).updateStatus(anyString());

        Tweet returnedTweet = socialMediaService.createTwitterPost("urlpath", "post");

        verify(socialMediaRepositoryMock, times(1)).findByUserAndProvider(user, "twitter");
        assertEquals(returnedTweet.getText(), "post");
    }

    @Test(expected = NoSuchSocialMediaConnectionException.class)
    public void testCreateTwitterPost_shouldThrowNoSuchSocialMediaConnectionException() throws Exception {
        doReturn(user).when(userServiceMock).getUserWithAuthorities();
        doReturn(null).when(socialMediaRepositoryMock).findByUserAndProvider(user,"twitter");

        socialMediaService.createTwitterPost("urlpath", "post");
    }

    @Test
    public void testDisconnectTwitter_shouldDisconnectTwitter() throws Exception {
        doReturn(user).when(userServiceMock).getUserWithAuthorities();
        doReturn(twitterSocialMediaConnection).when(socialMediaRepositoryMock).findByUserAndProvider(user, "twitter");

        socialMediaService.disconnectTwitter();

        verify(socialMediaRepositoryMock, times(1)).delete(twitterSocialMediaConnection);
    }

    @Test
    public void testGetConnectionsForUser_shouldReturnConnectionsForUser() throws Exception {
        doReturn(user).when(userServiceMock).getUserWithAuthorities();
        doAnswer(invocation -> {
            List<SocialMediaConnection> connections = new ArrayList<>();
            connections.add(facebookSocialMediaConnection);
            connections.add(twitterSocialMediaConnection);

            return connections;
        }).when(socialMediaRepositoryMock).findByUserAndActiveIsTrue(user);


        List<SocialMediaConnection> connections = socialMediaService.getConnectionsForUser();

        verify(socialMediaRepositoryMock, times(1)).findByUserAndActiveIsTrue(user);
        assertEquals(connections.get(0).getProvider(), "facebook");
        assertEquals(connections.get(0).getUser().getId().longValue(), 1L);
        assertEquals(connections.get(1).getProvider(), "twitter");
        assertEquals(connections.get(1).getUser().getId().longValue(), 1L);
    }

    @Test
    public void testCreateXingConnection_shouldCreateXingConnection() throws Exception {
        doReturn(user).when(userServiceMock).getUserWithAuthorities();

        //mock temp saved token from authorize url step (needed for access token)
        SocialMediaConnection temp = new SocialMediaConnection();
        temp.setToken("token");
        temp.setSecret("secret");
        temp.setProvider("xing");
        temp.setUser(user);

        doReturn(temp).when(socialMediaRepositoryMock).findByUserAndProviderAndActiveIsFalse(user, "xing");

        doAnswer(invocation -> {
            Object[] args = invocation.getArguments();
            SocialMediaConnection socialMediaConnections = (SocialMediaConnection) args[0];

            return socialMediaConnections;
        }).when(socialMediaRepositoryMock).save(any(SocialMediaConnection.class));

        Token accessToken = new Token("accesstoken", "verifier");
        doReturn(accessToken).when(xingServiceMock).getAccessToken(any(Token.class), any(Verifier.class));

        SocialMediaConnection socialMediaConnection = socialMediaService.createXingConnection("verifier");

        verify(socialMediaRepositoryMock, times(1)).save(any(SocialMediaConnection.class));
        assertEquals(socialMediaConnection.getProvider(), "xing");
        assertEquals(socialMediaConnection.getUser().getId().longValue(), 1L);
        assertEquals(socialMediaConnection.isActive(), true);
        assertEquals(socialMediaConnection.getToken(), "accesstoken");
        assertEquals(socialMediaConnection.getSecret(), "verifier");
    }

    @Test(expected = NoSuchSocialMediaConnectionException.class)
    public void testCreateXingPost_shouldCreateXingPost() throws Exception {
        doReturn(user).when(userServiceMock).getUserWithAuthorities();
        doReturn(null).when(socialMediaRepositoryMock).findByUserAndProviderAndActiveIsTrue(user,"xing");

        socialMediaService.createXingPost("url", "post");
    }

    @Test
    public void testCreateXingAuthorizationURL_shouldCreateAuthorizationURL() throws Exception {
        doReturn(user).when(userServiceMock).getUserWithAuthorities();

        Token requestToken = new Token("token", "secret");
        doReturn(requestToken).when(xingServiceMock).getRequestToken();

        doReturn(xingSocialMediaConnection).when(socialMediaRepositoryMock).findByUserAndProviderAndActiveIsFalse(user, "xing");

        doReturn("authorizationurl").when(xingServiceMock).getAuthorizationUrl(requestToken);

        String url = socialMediaService.createXingAuthorizationURL();

        //saving token for next authorization step (for getting the accesstoken)
        verify(socialMediaRepositoryMock, times(1)).save(any(SocialMediaConnection.class));
        assertEquals(url, "authorizationurl");
    }

    @Test
    public void testDisconnectXing_shouldDisconnectXing() throws Exception {
        doReturn(user).when(userServiceMock).getUserWithAuthorities();

        doReturn(xingSocialMediaConnection).when(socialMediaRepositoryMock).findByUserAndProviderAndActiveIsTrue(user, "xing");

        SocialMediaConnection connection = socialMediaService.disconnectXing();

        verify(socialMediaRepositoryMock, times(1)).delete(any(SocialMediaConnection.class));
        assertEquals(connection.getId().longValue(), 3L);
        assertEquals(connection.getProvider(), "xing");

    }
}
