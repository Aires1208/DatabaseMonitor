package com.zte.ums.oespaas.mysql.bean;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

import java.io.IOException;

/**
 * Created by root on 9/7/16.
 */
public class SessionSerializer extends JsonSerializer<Session> {
    @Override
    public void serialize(Session session, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException {
        jsonGenerator.writeStartObject();

        if (session != null) {
            jsonGenerator.writeStringField("sessionId", session.getSessionId());
            jsonGenerator.writeStringField("timeSpent", session.getTimeSpent());
            jsonGenerator.writeStringField("weight", session.getWeight());
        }

        jsonGenerator.writeEndObject();
    }
}
