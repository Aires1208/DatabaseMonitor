package com.zte.ums.oespaas.mysql.bean;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

import java.io.IOException;

/**
 * Created by root on 9/7/16.
 */
public class QuerySerializer extends JsonSerializer<Query> {
    @Override
    public void serialize(Query query, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException {
        jsonGenerator.writeStartObject();

        if (query != null) {
            jsonGenerator.writeStringField("query", query.getQuery());
            jsonGenerator.writeStringField("elapsedTime", query.getTimeSpent());
            jsonGenerator.writeStringField("weight", query.getWeight());
        }

        jsonGenerator.writeEndObject();
    }
}
