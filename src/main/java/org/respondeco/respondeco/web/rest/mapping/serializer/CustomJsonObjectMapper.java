package org.respondeco.respondeco.web.rest.mapping.serializer;


import com.fasterxml.jackson.databind.module.SimpleModule;
import org.joda.time.LocalDate;

/**
 * Created by clemens on 22/04/15.
 */
public class CustomJsonObjectMapper extends com.fasterxml.jackson.databind.ObjectMapper {

    public CustomJsonObjectMapper() {
        SimpleModule customModule = new SimpleModule();
        customModule.addSerializer(LocalDate.class, new CustomLocalDateSerializer());

        customModule.addDeserializer(LocalDate.class, new CustomLocalDateDeserializer());

        this.registerModule(customModule);
    }

}
