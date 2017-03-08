package com.zte.ums.oespaas.mysql.bean;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

import java.io.IOException;

/**
 * Created by root on 9/7/16.
 */
public class QueriesSerializer extends JsonSerializer<Queries> {
    @Override
    public void serialize(Queries queries, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException {
        jsonGenerator.writeStartObject();
        jsonGenerator.writeFieldName("queryTopN");
        jsonGenerator.writeStartArray();

        if (queries != null && queries.getQueryList().size() != 0) {
            for (Query query : queries.getQueryList()) {
                if (query != null) {
                    jsonGenerator.writeObject(query);
                }
            }
        }

        jsonGenerator.writeEndArray();
        jsonGenerator.writeEndObject();
    }
}
