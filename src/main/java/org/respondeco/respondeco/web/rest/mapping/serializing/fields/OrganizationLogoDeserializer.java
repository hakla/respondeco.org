package org.respondeco.respondeco.web.rest.mapping.serializing.fields;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import org.respondeco.respondeco.domain.ISOCategory;
import org.respondeco.respondeco.domain.Image;

import java.io.IOException;

/**
 * Created by Clemens Puehringer on 17/08/15.
 */
public class OrganizationLogoDeserializer extends JsonDeserializer<Image> {
    @Override
    public Image deserialize(JsonParser jsonParser, DeserializationContext deserializationContext)
        throws IOException, JsonProcessingException {
        if (jsonParser.getCurrentToken() != JsonToken.START_OBJECT) {
            throw new IOException("invalid start marker");
        }
        Image image = new Image();
        while (jsonParser.nextToken() != JsonToken.END_OBJECT) {
            String fieldname = jsonParser.getCurrentName();
            jsonParser.nextToken();  //move to next token in string
            if ("id".equals(fieldname)) {
                image.setId(jsonParser.getLongValue());
                break;
            }
        }
        return image;
    }
}
