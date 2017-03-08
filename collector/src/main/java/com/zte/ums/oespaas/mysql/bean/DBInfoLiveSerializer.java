package com.zte.ums.oespaas.mysql.bean;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

import java.io.IOException;
import java.util.List;

/**
 * Created by root on 9/2/16.
 */
public class DBInfoLiveSerializer extends JsonSerializer<DBInfoLive> {
    @Override
    public void serialize(DBInfoLive dbInfoLive, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException {
        jsonGenerator.writeStartObject();

        if (dbInfoLive != null) {
            writeCpuUsage(jsonGenerator, dbInfoLive.getCpuUsage());
            writeMemUsageList(jsonGenerator, dbInfoLive.getMemUsageList());
            writeSqlWaitStateLiveList(jsonGenerator, dbInfoLive.getSqlWaitStateLiveList());
            writeSessionList(jsonGenerator, dbInfoLive.getSessionList());
        }

        jsonGenerator.writeEndObject();
    }

    private void writeCpuUsage(JsonGenerator jsonGenerator, DBInfoLive.CpuUsage cpuUsage) throws IOException {
        jsonGenerator.writeFieldName("cpuUsage");
        jsonGenerator.writeStartObject();

        if (cpuUsage != null) {
            jsonGenerator.writeFieldName("io");
            jsonGenerator.writeStartArray();
            for (Integer io : cpuUsage.getIoList()) {
                jsonGenerator.writeObject(io);
            }
            jsonGenerator.writeEndArray();

            jsonGenerator.writeFieldName("sys");
            jsonGenerator.writeStartArray();
            for (Integer sys : cpuUsage.getSysList()) {
                jsonGenerator.writeObject(sys);
            }
            jsonGenerator.writeEndArray();

            jsonGenerator.writeFieldName("user");
            jsonGenerator.writeStartArray();
            for (Integer user : cpuUsage.getUserList()) {
                jsonGenerator.writeObject(user);
            }
            jsonGenerator.writeEndArray();

            jsonGenerator.writeFieldName("idle");
            jsonGenerator.writeStartArray();
            for (Integer idle : cpuUsage.getIdleList()) {
                jsonGenerator.writeObject(idle);
            }
            jsonGenerator.writeEndArray();
        }

        jsonGenerator.writeEndObject();
    }

    private void writeMemUsageList(JsonGenerator jsonGenerator, List<DBInfoLive.MemUsage> memUsageList) throws IOException {
        jsonGenerator.writeFieldName("memUsage");
        jsonGenerator.writeStartArray();

        if (memUsageList.size() != 0) {
            for (DBInfoLive.MemUsage memUsage : memUsageList) {
                if (memUsage != null) {
                    jsonGenerator.writeStartObject();
                    jsonGenerator.writeStringField("name", memUsage.getName());
                    jsonGenerator.writeNumberField("value", memUsage.getValue());
                    jsonGenerator.writeEndObject();
                }
            }
        }

        jsonGenerator.writeEndArray();
    }

    private void writeSqlWaitStateLiveList(JsonGenerator jsonGenerator, List<DBInfoLive.SqlWaitStateLive> sqlWaitStateLiveList) throws IOException {
        jsonGenerator.writeFieldName("sqlWaitStateLive");
        jsonGenerator.writeStartArray();

        if (sqlWaitStateLiveList.size() != 0) {
            for (DBInfoLive.SqlWaitStateLive sqlWaitStateLive : sqlWaitStateLiveList) {
                if (sqlWaitStateLive != null) {
                    jsonGenerator.writeStartObject();
                    jsonGenerator.writeStringField("name", sqlWaitStateLive.getName());
                    jsonGenerator.writeNumberField("value", sqlWaitStateLive.getValue());
                    jsonGenerator.writeEndObject();
                }
            }
        }

        jsonGenerator.writeEndArray();
    }

    private void writeSessionList(JsonGenerator jsonGenerator, List<DBInfoLive.Session> sessionList) throws IOException {
        jsonGenerator.writeFieldName("sessionList");
        jsonGenerator.writeStartArray();

        if (sessionList.size() != 0) {
            for (DBInfoLive.Session session : sessionList) {
                if (session != null) {
                    jsonGenerator.writeStartObject();
                    jsonGenerator.writeStringField("id", session.getId());
                    jsonGenerator.writeStringField("user", session.getUser());
                    jsonGenerator.writeStringField("host", session.getHost());
                    jsonGenerator.writeStringField("db", session.getDb());
                    jsonGenerator.writeStringField("cmd", session.getCmd());
                    jsonGenerator.writeNumberField("time", session.getTime());
                    jsonGenerator.writeStringField("state", session.getState());
                    jsonGenerator.writeStringField("info", session.getInfo());
                    jsonGenerator.writeEndObject();
                }
            }
        }

        jsonGenerator.writeEndArray();
    }
}
