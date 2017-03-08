package com.zte.ums.oespaas.mysql.bean;


import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

import java.io.IOException;
import java.util.List;

/**
 * Created by root on 9/1/16.
 */
public class DBInfoObjectsSerializer extends JsonSerializer<DBInfoObjects> {
    @Override
    public void serialize(DBInfoObjects dbInfoObjects, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException {
        jsonGenerator.writeStartObject();

        if (dbInfoObjects != null) {
            writeUsers(jsonGenerator, dbInfoObjects.getUsers());
            writeVariables(jsonGenerator, dbInfoObjects.getVariables());
            writeStorages(jsonGenerator, dbInfoObjects.getStorages());
            writeLog(jsonGenerator, dbInfoObjects.getLog());
            writeDatabase(jsonGenerator, dbInfoObjects.getDatabase());
        }

        jsonGenerator.writeEndObject();
    }

    private void writeUsers(JsonGenerator jsonGenerator, List<DBInfoObjects.User> users) throws IOException {
        jsonGenerator.writeFieldName("users");
        jsonGenerator.writeStartArray();

        if (users.size() != 0) {
            for (DBInfoObjects.User user : users) {
                if (user != null) {
                    jsonGenerator.writeStartObject();
                    jsonGenerator.writeStringField("username", user.getUserName());
                    jsonGenerator.writeStringField("host", user.getHost());
                    jsonGenerator.writeEndObject();
                }
            }
        }

        jsonGenerator.writeEndArray();
    }

    private void writeVariables(JsonGenerator jsonGenerator, List<DBInfoObjects.Variable> variables) throws IOException {
        jsonGenerator.writeFieldName("variables");
        jsonGenerator.writeStartArray();

        if (variables.size() != 0) {
            for (DBInfoObjects.Variable variable : variables) {
                if (variable != null) {
                    jsonGenerator.writeStartObject();
                    jsonGenerator.writeStringField("variableName", variable.getVariableName());
                    jsonGenerator.writeStringField("variableValue", variable.getVariableValue());
                    jsonGenerator.writeEndObject();
                }
            }
        }

        jsonGenerator.writeEndArray();
    }

    private void writeStorages(JsonGenerator jsonGenerator, List<DBInfoObjects.Storage> storages) throws IOException {
        jsonGenerator.writeFieldName("storage");
        jsonGenerator.writeStartArray();

        if (storages.size() != 0) {
            for (DBInfoObjects.Storage storage : storages) {
                if (storage != null) {
                    jsonGenerator.writeStartObject();
                    jsonGenerator.writeStringField("databaseName", storage.getDatabaseName());
                    jsonGenerator.writeStringField("indexLength", storage.getIndexLength());
                    jsonGenerator.writeStringField("dataLength", storage.getDataLength());
                    jsonGenerator.writeEndObject();
                }
            }
        }

        jsonGenerator.writeEndArray();
    }

    private void writeLog(JsonGenerator jsonGenerator, DBInfoObjects.Log log) throws IOException {
        jsonGenerator.writeFieldName("log");
        jsonGenerator.writeStartObject();

        if (log != null) {
            jsonGenerator.writeStringField("logPath", log.getLogPath());
        }

        jsonGenerator.writeEndObject();
    }

    private void writeDatabase(JsonGenerator jsonGenerator, DBInfoObjects.Database database) throws IOException {
        jsonGenerator.writeFieldName("database");
        jsonGenerator.writeStartObject();

        if (database != null) {
            jsonGenerator.writeStringField("uptime", database.getUpTime());
            jsonGenerator.writeStringField("version", database.getVersion());
        }

        jsonGenerator.writeEndObject();
    }
}
