package org.respondeco.respondeco.config;

import org.respondeco.respondeco.aop.RESTWrapperAspect;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.context.annotation.Profile;

/**
 * Created by klaus.harrer on 13.04.15.
 */
@Configuration
@EnableAspectJAutoProxy
public class RESTWrapperAspectConfiguration {

    @Bean
    @Profile(Constants.SPRING_PROFILE_DEVELOPMENT)
    public RESTWrapperAspect restFieldsAspect() { return new RESTWrapperAspect(); }
}
