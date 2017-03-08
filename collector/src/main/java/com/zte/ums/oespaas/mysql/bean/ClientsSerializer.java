package com.zte.ums.oespaas.mysql.bean;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

import java.io.IOException;

/**
 * Created by root on 9/7/16.
 */
public class ClientsSerializer extends JsonSerializer<Clients> {
    @Override
    public void serialize(Clients clients, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException {
        jsonGenerator.writeStartObject();

        jsonGenerator.writeFieldName("clientTopN");
        jsonGenerator.writeStartArray();

        if (clients != null && clients.getClientList().size() != 0) {
            for (Client client : clients.getClientList()) {
                if (client != null) {
                    jsonGenerator.writeObject(client);
                }
            }
        }

        jsonGenerator.writeEndArray();
        jsonGenerator.writeEndObject();
    }
}
