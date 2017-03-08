package com.zte.ums.oespaas.mysql.bean;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

import java.io.IOException;
import java.util.List;

/**
 * Created by root on 9/2/16.
 */
public class DBInfoDashboardSerializer extends JsonSerializer<DBInfoDashboard> {
    @Override
    public void serialize(DBInfoDashboard dbInfoDashboard, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException {
        jsonGenerator.writeStartObject();

        if (dbInfoDashboard != null) {
            writeSummary(jsonGenerator, dbInfoDashboard.getSummary());
            writeLoadTimeSpent(jsonGenerator, dbInfoDashboard.getLoadTimeSpent());
            writeSqlWaitStatesList(jsonGenerator, dbInfoDashboard.getSqlWaitStatesList());
            writeAvgConnect(jsonGenerator, dbInfoDashboard.getAvgConnect());
            writeCpuInfo(jsonGenerator, dbInfoDashboard.getCpuInfo());
            writeMemInfo(jsonGenerator, dbInfoDashboard.getMemInfo());
            writeDiskInfo(jsonGenerator, dbInfoDashboard.getDiskInfo());
            writeNetInfo(jsonGenerator, dbInfoDashboard.getNetInfo());
        }

        jsonGenerator.writeEndObject();
    }

    private void writeSummary(JsonGenerator jsonGenerator, DBInfoDashboard.Summary summary) throws IOException {
        jsonGenerator.writeFieldName("summary");
        jsonGenerator.writeStartObject();

        if (summary != null) {
            jsonGenerator.writeStringField("dbName", summary.getDbName());
            jsonGenerator.writeBooleanField("dbHealth", summary.getDbHealth());
            jsonGenerator.writeStringField("dbType", summary.getDbType());
            jsonGenerator.writeStringField("totalExec", summary.getTotalExec());
        }

        jsonGenerator.writeEndObject();
    }

    private void writeLoadTimeSpent(JsonGenerator jsonGenerator, DBInfoDashboard.LoadTimeSpent loadTimeSpent) throws IOException {
        jsonGenerator.writeFieldName("loadTimeSpent");
        jsonGenerator.writeStartObject();

        if (loadTimeSpent != null) {
            jsonGenerator.writeStringField("info", loadTimeSpent.getInfo());

            jsonGenerator.writeFieldName("time");
            writeArray(jsonGenerator, loadTimeSpent.getTime());

            jsonGenerator.writeFieldName("load");
            writeArray(jsonGenerator, loadTimeSpent.getLoad());

            jsonGenerator.writeFieldName("timeSpent");
            writeArray(jsonGenerator, loadTimeSpent.getTimeSpent());
        }

        jsonGenerator.writeEndObject();
    }

    private void writeSqlWaitStatesList(JsonGenerator jsonGenerator, List<DBInfoDashboard.SqlWaitStates> sqlWaitStatesList) throws IOException {
        jsonGenerator.writeFieldName("sqlWaitStates");
        jsonGenerator.writeStartArray();

        if (sqlWaitStatesList.size() != 0) {
            for (DBInfoDashboard.SqlWaitStates sqlWaitStates : sqlWaitStatesList) {
                if (sqlWaitStates != null) {
                    jsonGenerator.writeStartObject();
                    jsonGenerator.writeStringField("name", sqlWaitStates.getName());
                    jsonGenerator.writeNumberField("value", sqlWaitStates.getValue());
                    jsonGenerator.writeEndObject();
                }
            }
        }

        jsonGenerator.writeEndArray();
    }

    private void writeAvgConnect(JsonGenerator jsonGenerator, DBInfoDashboard.AvgConnect avgConnect) throws IOException {
        jsonGenerator.writeFieldName("avgConnect");
        jsonGenerator.writeStartObject();

        if (avgConnect != null) {
            jsonGenerator.writeStringField("info", avgConnect.getInfo());

            jsonGenerator.writeFieldName("time");
            writeArray(jsonGenerator, avgConnect.getTime());

            jsonGenerator.writeFieldName("data");
            writeArray(jsonGenerator, avgConnect.getData());
        }

        jsonGenerator.writeEndObject();
    }

    private void writeCpuInfo(JsonGenerator jsonGenerator, DBInfoDashboard.CpuInfo cpuInfo) throws IOException {
        jsonGenerator.writeFieldName("cpuInfo");
        jsonGenerator.writeStartObject();

        if (cpuInfo != null) {
            jsonGenerator.writeStringField("info", cpuInfo.getInfo());

            jsonGenerator.writeFieldName("time");
            writeArray(jsonGenerator, cpuInfo.getTime());

            jsonGenerator.writeFieldName("sys");
            writeArray(jsonGenerator, cpuInfo.getSys());

            jsonGenerator.writeFieldName("user");
            writeArray(jsonGenerator, cpuInfo.getUser());
        }

        jsonGenerator.writeEndObject();
    }

    private void writeMemInfo(JsonGenerator jsonGenerator, DBInfoDashboard.MemInfo memInfo) throws IOException {
        jsonGenerator.writeFieldName("memInfo");
        jsonGenerator.writeStartObject();

        if (memInfo != null) {
            jsonGenerator.writeStringField("info", memInfo.getInfo());

            jsonGenerator.writeFieldName("time");
            writeArray(jsonGenerator, memInfo.getTime());

            jsonGenerator.writeFieldName("data");
            writeArray(jsonGenerator, memInfo.getData());
        }

        jsonGenerator.writeEndObject();
    }

    private void writeDiskInfo(JsonGenerator jsonGenerator, DBInfoDashboard.DiskInfo diskInfo) throws IOException {
        jsonGenerator.writeFieldName("diskInfo");
        jsonGenerator.writeStartObject();

        if (diskInfo != null) {
            jsonGenerator.writeStringField("info", diskInfo.getInfo());

            jsonGenerator.writeFieldName("time");
            writeArray(jsonGenerator, diskInfo.getTime());

            jsonGenerator.writeFieldName("incoming");
            writeArray(jsonGenerator, diskInfo.getIncoming());

            jsonGenerator.writeFieldName("outgoing");
            writeArray(jsonGenerator, diskInfo.getOutgoing());
        }

        jsonGenerator.writeEndObject();
    }

    private void writeNetInfo(JsonGenerator jsonGenerator, DBInfoDashboard.NetInfo netInfo) throws IOException {
        jsonGenerator.writeFieldName("netInfo");
        jsonGenerator.writeStartObject();

        if (netInfo != null) {
            jsonGenerator.writeStringField("info", netInfo.getInfo());

            jsonGenerator.writeFieldName("time");
            writeArray(jsonGenerator, netInfo.getTime());

            jsonGenerator.writeFieldName("incoming");
            writeArray(jsonGenerator, netInfo.getIncoming());

            jsonGenerator.writeFieldName("outgoing");
            writeArray(jsonGenerator, netInfo.getOutgoing());
        }

        jsonGenerator.writeEndObject();
    }

    private void writeArray(JsonGenerator jsonGenerator, List<?> list) throws IOException {
        jsonGenerator.writeStartArray();
        for (Object obj : list) {
            jsonGenerator.writeObject(obj);
        }
        jsonGenerator.writeEndArray();
    }
}
