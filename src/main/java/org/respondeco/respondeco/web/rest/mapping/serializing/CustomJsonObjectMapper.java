package org.respondeco.respondeco.web.rest.mapping.serializing;


import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import org.hibernate.usertype.DynamicParameterizedType;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;

/**
 * Created by clemens on 22/04/15.
 */
public class CustomJsonObjectMapper extends com.fasterxml.jackson.databind.ObjectMapper {

    public CustomJsonObjectMapper(List<StdSerializer<?>> serializers, List<StdDeserializer<?>> deserializers) {
        final SimpleModule customModule = new SimpleModule("Custom (De)Serializers for respondeco.org");

        serializers.stream().forEach(
            s -> customModule.addSerializer(s)
        );

        deserializers.stream().forEach(
            d -> {
                //this is the actual class of the deserializer, the deserializer is subclassed by spring and
                //instantiated as a subclass classname$$EnhancerBySpringCGLIB
                Class<?> superClass = d.getClass().getSuperclass();
                //generic superclass of the deserializer is the
                //com.fasterxml.jackson.databind.deser.std.StdDeserializer<T> class
                Type genericSuperclass = superClass.getGenericSuperclass();
                Class<Object> genericType =
                    (Class<Object>)((ParameterizedType) genericSuperclass).getActualTypeArguments()[0];
                customModule.addDeserializer(genericType, d);
        });

        this.registerModule(customModule);
    }


}
