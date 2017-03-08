package com.zte.ums.oespaas.mysql.bean;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

import java.io.IOException;

/**
 * Created by root on 9/7/16.
 */
public class ClientSerializer extends JsonSerializer<Client> {
    @Override
    public void serialize(Client client, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException {
        jsonGenerator.writeStartObject();

        if (client != null) {
            jsonGenerator.writeStringField("client", client.getClient());
            jsonGenerator.writeStringField("count", client.getCount());
            jsonGenerator.writeStringField("weight", client.getWeight());
        }

        jsonGenerator.writeEndObject();
    }
}
