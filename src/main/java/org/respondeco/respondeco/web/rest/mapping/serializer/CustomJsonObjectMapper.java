package org.respondeco.respondeco.web.rest.mapping.serializer;


import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import org.joda.time.LocalDate;

import java.lang.reflect.ParameterizedType;
import java.util.List;

/**
 * Created by clemens on 22/04/15.
 */
public class CustomJsonObjectMapper extends com.fasterxml.jackson.databind.ObjectMapper {

    public CustomJsonObjectMapper(List<StdSerializer<?>> serializers) {
        final SimpleModule customModule = new SimpleModule("Custom (De)Serializers for respondeco.org");

        serializers.stream().forEach(
            s -> customModule.addSerializer(s)
        );

        this.registerModule(customModule);
    }


}
