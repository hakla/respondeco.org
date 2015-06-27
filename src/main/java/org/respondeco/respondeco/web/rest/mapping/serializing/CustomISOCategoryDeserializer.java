package org.respondeco.respondeco.web.rest.mapping.serializing;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import org.joda.time.LocalDate;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.respondeco.respondeco.domain.ISOCategory;

import java.io.IOException;

/**
 * Created by clemens on 24/06/15.
 */
@CustomDeserializer
public class CustomISOCategoryDeserializer  extends StdDeserializer<ISOCategory> {

    public CustomISOCategoryDeserializer() {
        super(ISOCategory.class);
    }

    @Override
    public ISOCategory deserialize(JsonParser jsonParser, DeserializationContext deserializationContext)
        throws IOException {
        if (jsonParser.getCurrentToken() != JsonToken.START_OBJECT) {
            throw new IOException("invalid start marker");
        }
        ISOCategory isoCategory = new ISOCategory();
        while (jsonParser.nextToken() != JsonToken.END_OBJECT) {
            String fieldname = jsonParser.getCurrentName();
            jsonParser.nextToken();  //move to next token in string
            if ("id".equals(fieldname)) {
                isoCategory.setId(jsonParser.getLongValue());
            } else if ("key".equals(fieldname)) {
                isoCategory.setKey(jsonParser.getText());
            }
        }
        return isoCategory;
    }
}
