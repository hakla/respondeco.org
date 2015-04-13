package org.respondeco.respondeco.config;

import org.respondeco.respondeco.web.rest.mapper.ObjectMapperFactoryProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Created by clemens on 13/04/15.
 */
@Configuration
public class ObjectMappingConfiguration {

    @Bean
    public ObjectMapperFactoryProvider getFactoryProvider() {
        return new ObjectMapperFactoryProvider();
    }

}
