package org.respondeco.respondeco.config;

import org.resthub.web.springmvc.router.RouterConfigurationSupport;
import org.springframework.context.annotation.Configuration;

import java.util.Arrays;
import java.util.List;

/**
 * Created by clemens on 18/09/15.
 */
//@Configuration
public class RoutingConfiguration extends RouterConfigurationSupport {


    @Override
    public List<String> listRouteFiles() {
        return Arrays.asList(
            "classpath:config/routes.conf"
        );
    }
}
