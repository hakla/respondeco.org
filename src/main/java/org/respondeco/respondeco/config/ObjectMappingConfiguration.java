package org.respondeco.respondeco.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.respondeco.respondeco.web.rest.mapping.serializer.CustomJsonObjectMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Created by clemens on 22/04/15.
 */

@Configuration
public class ObjectMappingConfiguration {

    @Bean
    public com.fasterxml.jackson.databind.ObjectMapper getCustomMapper() {
        return new CustomJsonObjectMapper();
    }

}
