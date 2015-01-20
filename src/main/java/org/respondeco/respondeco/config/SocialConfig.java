package org.respondeco.respondeco.config;

import org.scribe.builder.ServiceBuilder;
import org.scribe.builder.api.XingApi;
import org.scribe.oauth.OAuthService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.social.facebook.connect.FacebookConnectionFactory;
import org.springframework.social.twitter.connect.TwitterConnectionFactory;

import javax.inject.Inject;

@Configuration
public class SocialConfig {

    @Inject
    private Environment environment;

    @Bean
    public FacebookConnectionFactory facebookConnectionFactory() {
        return new FacebookConnectionFactory(
            environment.getProperty("spring.social.facebook.appId"),
            environment.getProperty("spring.social.facebook.appSecret"));
    }

    @Bean
    public TwitterConnectionFactory twitterConnectionFactory() {
        return new TwitterConnectionFactory(
           environment.getProperty("spring.social.twitter.appId"),
           environment.getProperty("spring.social.twitter.appSecret")
        );
    }

    @Bean
    public OAuthService xingServiceBuilder() {
        return new ServiceBuilder()
            .provider(XingApi.class)
            .apiKey(environment.getProperty("spring.social.xing.appId"))
            .apiSecret(environment.getProperty("spring.social.xing.appSecret"))
            .callback(environment.getProperty("spring.social.xing.redirectUrl"))
            .build();
    }


}
