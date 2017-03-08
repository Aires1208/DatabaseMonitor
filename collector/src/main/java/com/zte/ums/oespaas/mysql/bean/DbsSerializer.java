package com.zte.ums.oespaas.mysql.bean;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

import java.io.IOException;

/**
 * Created by root on 9/7/16.
 */
public class DbsSerializer extends JsonSerializer<Dbs> {
    @Override
    public void serialize(Dbs dbs, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException {
        jsonGenerator.writeStartObject();
        jsonGenerator.writeFieldName("dbs");
        jsonGenerator.writeStartArray();

        if (dbs != null && dbs.getDbList().size() != 0) {
            for (Db db : dbs.getDbList()) {
                if (db != null) {
                    jsonGenerator.writeObject(db);
                }
            }
        }

        jsonGenerator.writeEndArray();
        jsonGenerator.writeEndObject();
    }
}
