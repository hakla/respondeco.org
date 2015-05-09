package org.respondeco.respondeco.web.rest.mapping.serializer;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * Custom Jackson serializer for displaying Joda Time dates.
 */
@Component
@CustomSerializer
public class CustomDateTimeSerializer extends StdSerializer<DateTime> {

    private static DateTimeFormatter formatter =
            DateTimeFormat.forPattern("yyyy-MM-dd");

    public CustomDateTimeSerializer() {
        super(DateTime.class);
    }

    @Override
    public void serialize(DateTime value, JsonGenerator generator,
                          SerializerProvider serializerProvider)
            throws IOException {
        generator.writeString(formatter.print(value));
    }
}
