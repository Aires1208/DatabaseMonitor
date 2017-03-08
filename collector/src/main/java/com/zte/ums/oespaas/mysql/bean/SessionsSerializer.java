package com.zte.ums.oespaas.mysql.bean;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

import java.io.IOException;

/**
 * Created by root on 9/7/16.
 */
public class SessionsSerializer extends JsonSerializer<Sessions> {
    @Override
    public void serialize(Sessions sessions, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException {
        jsonGenerator.writeStartObject();
        jsonGenerator.writeFieldName("sessionTopN");
        jsonGenerator.writeStartArray();

        if (sessions != null && sessions.getSessionList().size() != 0) {
            for (Session session : sessions.getSessionList()) {
                if (session != null) {
                    jsonGenerator.writeObject(session);
                }
            }
        }

        jsonGenerator.writeEndArray();
        jsonGenerator.writeEndObject();
    }
}
