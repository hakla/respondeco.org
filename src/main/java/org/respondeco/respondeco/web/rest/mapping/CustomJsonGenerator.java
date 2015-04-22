package org.respondeco.respondeco.web.rest.mapping;

import com.fasterxml.jackson.core.ObjectCodec;
import com.fasterxml.jackson.core.io.IOContext;
import com.fasterxml.jackson.core.json.UTF8JsonGenerator;

import java.io.OutputStream;

/**
 * Created by clemens on 22/04/15.
 */
public class CustomJsonGenerator extends UTF8JsonGenerator {

    public CustomJsonGenerator(IOContext ioContext, int i, ObjectCodec objectCodec, OutputStream outputStream) {
        super(ioContext, i, objectCodec, outputStream);
    }

    public CustomJsonGenerator() {
        super(null, 0, null, null);
    }
}
