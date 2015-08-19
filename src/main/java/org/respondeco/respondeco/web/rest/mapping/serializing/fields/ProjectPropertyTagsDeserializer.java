package org.respondeco.respondeco.web.rest.mapping.serializing.fields;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import org.respondeco.respondeco.domain.Image;
import org.respondeco.respondeco.domain.PropertyTag;

import java.io.IOException;

/**
 * Created by Clemens Puehringer on 18/08/15.
 */
public class ProjectPropertyTagsDeserializer extends JsonDeserializer<PropertyTag> {
    @Override
    public PropertyTag deserialize(JsonParser jsonParser, DeserializationContext deserializationContext)
        throws IOException, JsonProcessingException {
        if (jsonParser.getCurrentToken() != JsonToken.START_OBJECT) {
            throw new IOException("invalid start marker");
        }
        PropertyTag propertyTag = new PropertyTag();
        while (jsonParser.nextToken() != JsonToken.END_OBJECT) {
            String fieldname = jsonParser.getCurrentName();
            jsonParser.nextToken();  //move to next token in string
            if ("id".equals(fieldname)) {
                propertyTag.setId(jsonParser.getLongValue());
            }
            if("name".equals(fieldname)) {
                propertyTag.setName(fieldname);
            }
        }
        return propertyTag;
    }
}
