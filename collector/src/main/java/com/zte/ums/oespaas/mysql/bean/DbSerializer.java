package com.zte.ums.oespaas.mysql.bean;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

import java.io.IOException;

/**
 * Created by root on 9/7/16.
 */
public class DbSerializer extends JsonSerializer<Db> {
    @Override
    public void serialize(Db db, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException {
        jsonGenerator.writeStartObject();

        if (db != null) {
            jsonGenerator.writeStringField("dbName", db.getDbName());
            jsonGenerator.writeStringField("dbHost", db.getDbHost());
            jsonGenerator.writeStringField("osNeId", db.getOsNeId());
            jsonGenerator.writeStringField("dbNeId", db.getDbNeId());
            jsonGenerator.writeStringField("collectTime", db.getCollectTime());
            jsonGenerator.writeStringField("type", db.getType());
            jsonGenerator.writeStringField("status", db.getStatus());
            jsonGenerator.writeBooleanField("health", db.isHealth());
            jsonGenerator.writeNumberField("queries", db.getQueries());
            jsonGenerator.writeStringField("timeSpent", db.getTimeSpent());
            jsonGenerator.writeStringField("cpu", db.getCpu());
        }

        jsonGenerator.writeEndObject();
    }
}
