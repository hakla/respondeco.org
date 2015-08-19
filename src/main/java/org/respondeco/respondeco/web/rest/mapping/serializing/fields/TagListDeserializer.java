package org.respondeco.respondeco.web.rest.mapping.serializing.fields;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import org.respondeco.respondeco.domain.Image;
import org.respondeco.respondeco.domain.PropertyTag;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Clemens Puehringer on 18/08/15.
 */
public class TagListDeserializer extends JsonDeserializer<List<PropertyTag>> {
    @Override
    public List<PropertyTag> deserialize(JsonParser jsonParser, DeserializationContext deserializationContext)
        throws IOException, JsonProcessingException {
        if (jsonParser.getCurrentToken() != JsonToken.START_ARRAY) {
            throw new IOException("ProjectPropertyTagsDeserializer: invalid start marker, expected START_ARRAY for " +
                "a list of property tags");
        }
        List<PropertyTag> propertyTags = new ArrayList<>();
        while(jsonParser.nextToken() == JsonToken.START_OBJECT) {
            PropertyTag propertyTag = new PropertyTag();
            while(jsonParser.nextToken() != JsonToken.END_OBJECT) {
                String fieldName = jsonParser.getCurrentName();
                jsonParser.nextToken();  //move to next token in string
                if ("id".equals(fieldName)) {
                    propertyTag.setId(jsonParser.getLongValue());
                }
                if ("name".equals(fieldName)) {
                    propertyTag.setName(jsonParser.getText());
                }
            }
            propertyTags.add(propertyTag);
        }
        if(jsonParser.getCurrentToken() != JsonToken.END_ARRAY) {
            throw new IOException("ProjectPropertyTagsDeserializer: invalid end marker, " +
                "expected END_ARRAY, got " + jsonParser.getCurrentToken());
        }

        return propertyTags;
    }
}
