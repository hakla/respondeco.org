package org.respondeco.respondeco.config;

import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import org.respondeco.respondeco.web.rest.mapping.serializing.types.CustomTypeDeserializer;
import org.respondeco.respondeco.web.rest.mapping.serializing.types.CustomJsonObjectMapper;
import org.respondeco.respondeco.web.rest.mapping.serializing.types.CustomTypeSerializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.aop.framework.Advised;
import org.springframework.aop.support.AopUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerAdapter;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by clemens on 22/04/15.
 */

@Configuration
public class JsonObjectMappingConfiguration implements ApplicationContextAware, InitializingBean {

    private static Logger log = LoggerFactory.getLogger(JsonObjectMappingConfiguration.class);

    @Inject
    private RequestMappingHandlerAdapter requestMappingHandlerAdapter;

    private ApplicationContext applicationContext;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        Map<String, Object> serializers = applicationContext.getBeansWithAnnotation(CustomTypeSerializer.class);
        Map<String, Object> deserializers = applicationContext.getBeansWithAnnotation(CustomTypeDeserializer.class);

        List<StdSerializer<?>> stdSerializers = new ArrayList<>();
        for(Object s : serializers.values()) {
            if (AopUtils.isJdkDynamicProxy(s)) {
                Advised advised = (Advised) s;
                try {
                    stdSerializers.add((StdSerializer<?>) advised.getTargetSource().getTarget());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                stdSerializers.add((StdSerializer<?>)  s);
            }
        }
        log.debug("Found custom serializers: {}", stdSerializers);

        List<StdDeserializer<?>> stdDeserializers = new ArrayList<>();
        for(Object d : deserializers.values()) {
            if (AopUtils.isJdkDynamicProxy(d)) {
                Advised advised = (Advised) d;
                try {
                    d = advised.getTargetSource().getTarget();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            log.debug("deserializer object: {}", d);
            stdDeserializers.add((StdDeserializer<?>) d);
        }
        log.debug("Found custom deserializers: {}", stdDeserializers);

        log.debug("looking for MappingJackson2HttpMessageConverter");
        List<HttpMessageConverter<?>> messageConverters = requestMappingHandlerAdapter.getMessageConverters();
        for (HttpMessageConverter<?> messageConverter : messageConverters) {
            if (messageConverter instanceof MappingJackson2HttpMessageConverter) {
                log.debug("MappingJackson2HttpMessageConverter found, setting CustomJsonObjectMapper");
                MappingJackson2HttpMessageConverter m = (MappingJackson2HttpMessageConverter) messageConverter;
                m.setObjectMapper(new CustomJsonObjectMapper(stdSerializers, stdDeserializers));
            }
        }

    }
}
