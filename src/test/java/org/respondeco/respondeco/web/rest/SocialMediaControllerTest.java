package org.respondeco.respondeco.web.rest;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.respondeco.respondeco.Application;
import org.respondeco.respondeco.domain.SocialMediaConnection;
import org.respondeco.respondeco.domain.User;
import org.respondeco.respondeco.repository.SocialMediaRepository;
import org.respondeco.respondeco.service.SocialMediaService;
import org.respondeco.respondeco.service.UserService;
import org.respondeco.respondeco.service.exception.ConnectionAlreadyExistsException;
import org.respondeco.respondeco.service.exception.NoSuchSocialMediaConnectionException;
import org.respondeco.respondeco.service.exception.OperationForbiddenException;
import org.respondeco.respondeco.testutil.TestUtil;
import org.respondeco.respondeco.web.rest.dto.SocialMediaConnectionResponseDTO;
import org.respondeco.respondeco.web.rest.dto.StringDTO;
import org.respondeco.respondeco.web.rest.dto.TwitterConnectionDTO;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.core.env.Environment;
import org.springframework.social.facebook.connect.FacebookConnectionFactory;
import org.springframework.social.facebook.connect.FacebookOAuth2Template;
import org.springframework.social.twitter.api.Tweet;
import org.springframework.social.twitter.connect.TwitterConnectionFactory;
import org.springframework.social.xing.connect.XingConnectionFactory;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import org.springframework.test.context.support.DirtiesContextTestExecutionListener;
import org.springframework.test.context.transaction.TransactionalTestExecutionListener;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.spy;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Test class for the SocialMediaController
 *
 * @see org.respondeco.respondeco.web.rest.SocialMediaController
 */
/*@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Application.class)
@WebAppConfiguration
@TestExecutionListeners({ DependencyInjectionTestExecutionListener.class,
    DirtiesContextTestExecutionListener.class,
    TransactionalTestExecutionListener.class })
public class SocialMediaControllerTest {



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
    private XingConnectionFactory xingConnectionFactoryMock;

    private SocialMediaService socialMediaServiceMock;
    private MockMvc restSocialMediaMockMvc;

    private SocialMediaConnection socialMediaConnection;
    private User user;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);

        socialMediaServiceMock = spy(new SocialMediaService(
            environmentMock,
            socialMediaRepositoryMock,
            userServiceMock,
            facebookConnectionFactoryMock,
            twitterConnectionFactoryMock,
            xingConnectionFactoryMock
        ));

        SocialMediaController socialMediaController = new SocialMediaController(socialMediaServiceMock);

        this.restSocialMediaMockMvc = MockMvcBuilders.standaloneSetup(socialMediaController).build();

        user = new User();
        user.setId(1L);
        user.setFirstName("firstname");
        user.setLastName("lastname");

        socialMediaConnection = new SocialMediaConnection();
        socialMediaConnection.setId(1L);
        socialMediaConnection.setProvider("facebook");
        socialMediaConnection.setToken("asdfasdfasdf");
        socialMediaConnection.setUser(user);
    }

    @Test
    public void testConnectFacebook_expectOK() throws Exception {

        String url = "http://authorizationurl.fake.com";
        doReturn(url).when(socialMediaServiceMock).createFacebookAuthorizationURL();

        restSocialMediaMockMvc.perform(get("/app/rest/connect/facebook")
            .contentType(TestUtil.APPLICATION_JSON_UTF8))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.string").value(url));
    }

    @Test
    public void testCreateFacebookConnection_expectCreated() throws Exception {
        doReturn(socialMediaConnection).when(socialMediaServiceMock).createFacebookConnection(anyString());
        StringDTO code = new StringDTO("codefromusergrantspermission");

        restSocialMediaMockMvc.perform(post("/app/rest/connect/facebook/createconnection")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(code)))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.id").value(1))
            .andExpect(jsonPath("$.userId").value(1))
            .andExpect(jsonPath("$.provider").value("facebook"));
    }

    @Test
    public void testCreateFacebookConnection_throwsOperationForbiddenException() throws Exception {
        doThrow(OperationForbiddenException.class).when(socialMediaServiceMock).createFacebookConnection(anyString());
        StringDTO code = new StringDTO("codefromusergrantspermission");


        restSocialMediaMockMvc.perform(post("/app/rest/connect/facebook/createconnection")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(code)))
            .andExpect(status().isForbidden());
    }

    @Test
    public void testCreateFacebookPost_expectCreated() throws Exception {
        StringDTO post = new StringDTO("hallo facebook");
        doReturn(post.getString()).when(socialMediaServiceMock).createFacebookPost(anyString());

        restSocialMediaMockMvc.perform(post("/app/rest/socialmedia/facebook/post")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(post)))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.string").value(post.getString()));
    }

    @Test
    public void testCreateFacebookPost_throwsOperationForbiddenException() throws Exception {
        doThrow(OperationForbiddenException.class).when(socialMediaServiceMock).createFacebookPost(anyString());
        StringDTO post = new StringDTO("hallo facebook");

        restSocialMediaMockMvc.perform(post("/app/rest/socialmedia/facebook/post")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(post)))
            .andExpect(status().isForbidden());
    }

    @Test
    public void testCreateFacebookPost_throwsNoSuchSocialMediaConnectionException() throws Exception {
        doThrow(NoSuchSocialMediaConnectionException.class).when(socialMediaServiceMock).createFacebookPost(anyString());
        StringDTO post = new StringDTO("hallo facebook");

        restSocialMediaMockMvc.perform(post("/app/rest/socialmedia/facebook/post")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(post)))
            .andExpect(status().isNotFound());
    }

    @Test
    public void testDisconnectFacebook_expectOK() throws Exception {
        doReturn(socialMediaConnection).when(socialMediaServiceMock).disconnectFacebook();

        restSocialMediaMockMvc.perform(delete("/app/rest/socialmedia/facebook/disconnect")
            .contentType(TestUtil.APPLICATION_JSON_UTF8))
            .andExpect(status().isOk());
    }

    @Test
    public void testDisconnectTwitter_expectOk() throws Exception {
        doReturn(socialMediaConnection).when(socialMediaServiceMock).disconnectTwitter();

        restSocialMediaMockMvc.perform(delete("/app/rest/socialmedia/twitter/disconnect")
            .contentType(TestUtil.APPLICATION_JSON_UTF8))
            .andExpect(status().isOk());
    }

    @Test
    public void testDisconnectTwitter_throwsNoSuchSocialMediaConnectionException() throws Exception {
        doThrow(NoSuchSocialMediaConnectionException.class).when(socialMediaServiceMock).disconnectTwitter();

        restSocialMediaMockMvc.perform(delete("/app/rest/socialmedia/twitter/disconnect")
            .contentType(TestUtil.APPLICATION_JSON_UTF8))
            .andExpect(status().isNotFound());
    }

    @Test
    public void testConnectTwitter_expectOk() throws Exception {
        doReturn("http://authorize.twitter").when(socialMediaServiceMock).createTwitterAuthorizationURL();

        restSocialMediaMockMvc.perform(get("/app/rest/connect/twitter")
            .contentType(TestUtil.APPLICATION_JSON_UTF8))
            .andExpect(status().isOk());
    }

    @Test
    public void testCreateTwitterConnection_expectCreated() throws Exception {
        doReturn(socialMediaConnection).when(socialMediaServiceMock).createTwitterConnection(anyString(), anyString());

        TwitterConnectionDTO dto = new TwitterConnectionDTO();
        dto.setToken("token");
        dto.setVerifier("verifier");

        restSocialMediaMockMvc.perform(post("/app/rest/connect/twitter/createconnection")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(dto)))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.id").value(1))
            .andExpect(jsonPath("$.userId").value(1))
            .andExpect(jsonPath("$.provider").value("facebook"));
    }

    @Test
    public void testCreateTwitterConnection_throwsOperationForbiddenException() throws Exception {
        doThrow(OperationForbiddenException.class).when(socialMediaServiceMock).createTwitterConnection(anyString(), anyString());

        TwitterConnectionDTO dto = new TwitterConnectionDTO();
        dto.setToken("token");
        dto.setVerifier("verifier");

        restSocialMediaMockMvc.perform(post("/app/rest/connect/twitter/createconnection")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(dto)))
            .andExpect(status().isForbidden());
    }

    @Test
    public void testCreateTwitterConnection_throwsConnectionAlreadyExistsException() throws Exception {
        doThrow(ConnectionAlreadyExistsException.class).when(socialMediaServiceMock).createTwitterConnection(anyString(), anyString());

        TwitterConnectionDTO dto = new TwitterConnectionDTO();
        dto.setToken("token");
        dto.setVerifier("verifier");

        restSocialMediaMockMvc.perform(post("/app/rest/connect/twitter/createconnection")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(dto)))
            .andExpect(status().isBadRequest());
    }

    @Test
    public void testCreateTwitterPost_expectCreated() throws Exception {
        Tweet tweet = new Tweet(1L, "text", new Date(), "user",
            "profileImageUrl", 1L, 1L, "de", "source");

        doReturn(tweet).when(socialMediaServiceMock).createTwitterPost(anyString());

        restSocialMediaMockMvc.perform(post("/app/rest/socialmedia/twitter/post")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(new StringDTO("post"))))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.id").value(1))
            .andExpect(jsonPath("$.text").value(tweet.getText()));
    }

    @Test
    public void testCreateTwitterPost_throwsOperationForbiddenException() throws Exception {

        doThrow(OperationForbiddenException.class).when(socialMediaServiceMock).createTwitterPost(anyString());

        restSocialMediaMockMvc.perform(post("/app/rest/socialmedia/twitter/post")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(new StringDTO("post"))))
            .andExpect(status().isForbidden());
    }

    @Test
    public void testCreateTwitterPost_throwsNoSuchSocialMediaConnectionException() throws Exception {

        doThrow(NoSuchSocialMediaConnectionException.class).when(socialMediaServiceMock).createTwitterPost(anyString());

        restSocialMediaMockMvc.perform(post("/app/rest/socialmedia/twitter/post")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(new StringDTO("post"))))
            .andExpect(status().isNotFound());
    }

    @Test
    public void testConnectXing_expectOk() throws Exception {
        String url = "http://authorize.xing";
        doReturn(url).when(socialMediaServiceMock).createXingAuthorizationURL();

        restSocialMediaMockMvc.perform(get("/app/rest/connect/xing")
            .contentType(TestUtil.APPLICATION_JSON_UTF8))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.string").value(url));
    }

    @Test
    public void testCreateXingConnection_expectCreated() throws Exception {
        TwitterConnectionDTO dto = new TwitterConnectionDTO();
        dto.setToken("token");
        dto.setVerifier("verifier");

        socialMediaConnection.setProvider("xing");
        doReturn(socialMediaConnection).when(socialMediaServiceMock).createXingConnection(anyString(), anyString());

        restSocialMediaMockMvc.perform(post("/app/rest/connect/xing/createconnection")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(dto)))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.id").value(1))
            .andExpect(jsonPath("$.userId").value(1))
            .andExpect(jsonPath("$.provider").value("xing"));
    }

    @Test
    public void testCreateXingConnection_throwsOperationForbiddenException() throws Exception {
        TwitterConnectionDTO dto = new TwitterConnectionDTO();
        dto.setToken("token");
        dto.setVerifier("verifier");

        doThrow(OperationForbiddenException.class).when(socialMediaServiceMock).createXingConnection(anyString(),anyString());

        restSocialMediaMockMvc.perform(post("/app/rest/connect/xing/createconnection")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(dto)))
            .andExpect(status().isForbidden());
    }

    @Test
    public void testCreateXingConnection_throwsConnectionAlreadyExistsException() throws Exception {
        TwitterConnectionDTO dto = new TwitterConnectionDTO();
        dto.setToken("token");
        dto.setVerifier("verifier");

        doThrow(ConnectionAlreadyExistsException.class).when(socialMediaServiceMock).createXingConnection(anyString(),anyString());

        restSocialMediaMockMvc.perform(post("/app/rest/connect/xing/createconnection")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(dto)))
            .andExpect(status().isBadRequest());
    }

    @Test
    public void testGetConnections_expectOk() throws Exception {
        List<SocialMediaConnection> connections = new ArrayList<>();
        connections.add(socialMediaConnection);

        doReturn(connections).when(socialMediaServiceMock).getConnectionsForUser();

        restSocialMediaMockMvc.perform(get("/app/rest/connections")
            .contentType(TestUtil.APPLICATION_JSON_UTF8))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$[0].id").value(1));
    }

    @Test
    public void testGetConnections_throwOperationForbidden() throws Exception {
        doThrow(OperationForbiddenException.class).when(socialMediaServiceMock).getConnectionsForUser();

        restSocialMediaMockMvc.perform(get("/app/rest/connections")
            .contentType(TestUtil.APPLICATION_JSON_UTF8))
            .andExpect(status().isForbidden());
    }

}
*/
