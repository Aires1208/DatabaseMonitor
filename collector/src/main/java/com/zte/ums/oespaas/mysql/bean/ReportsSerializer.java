package com.zte.ums.oespaas.mysql.bean;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

import java.io.IOException;
import java.util.List;

/**
 * Created by root on 9/8/16.
 */
public class ReportsSerializer extends JsonSerializer<Reports> {
    @Override
    public void serialize(Reports reports, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException {
        jsonGenerator.writeStartObject();

        if (reports != null) {
            writeWaitStateChart(reports.getWaitStateChart(), jsonGenerator);
            writeWaitStateTableList(reports.getWaitStateTableList(), jsonGenerator);
            writeTopActivityChart(reports.getTopActivityChart(), jsonGenerator);
            writeTopActivityTableList(reports.getTopActivityTableList(), jsonGenerator);
        }

        jsonGenerator.writeEndObject();
    }

    private void writeWaitStateChart(Reports.WaitStateChart waitStateChart, JsonGenerator jsonGenerator) throws IOException {
        jsonGenerator.writeFieldName("waitStateChart");
        jsonGenerator.writeStartObject();

        if (waitStateChart != null) {
            writeArray("legend", waitStateChart.getLegend(), jsonGenerator);
            writeArray("time", waitStateChart.getTime(), jsonGenerator);
            writeArrayArray("data", waitStateChart.getData(), jsonGenerator);
        }

        jsonGenerator.writeEndObject();
    }

    private void writeWaitStateTableList(List<Reports.WaitStateTable> waitStateTableList, JsonGenerator jsonGenerator) throws IOException {
        jsonGenerator.writeFieldName("waitStateTable");
        jsonGenerator.writeStartArray();

        if (waitStateTableList.size() != 0) {
            for (Reports.WaitStateTable waitStateTable : waitStateTableList) {
                jsonGenerator.writeStartObject();

                if (waitStateTable != null) {
                    jsonGenerator.writeStringField("waitState", waitStateTable.getWaitState());
                    jsonGenerator.writeStringField("description", waitStateTable.getDescription());
                    jsonGenerator.writeStringField("totalTime", waitStateTable.getTotalTime());
                }

                jsonGenerator.writeEndObject();
            }
        }

        jsonGenerator.writeEndArray();
    }

    private void writeTopActivityChart(Reports.TopActivityChart topActivityChart, JsonGenerator jsonGenerator) throws IOException {
        jsonGenerator.writeFieldName("topActivityChart");
        jsonGenerator.writeStartObject();

        if (topActivityChart != null) {
            writeArray("legend", topActivityChart.getLegend(), jsonGenerator);
            writeArray("time", topActivityChart.getTime(), jsonGenerator);
            writeArrayArray("data", topActivityChart.getData(), jsonGenerator);
        }

        jsonGenerator.writeEndObject();
    }

    private void writeTopActivityTableList(List<Reports.TopActivityTable> topActivityTableList, JsonGenerator jsonGenerator) throws IOException {
        jsonGenerator.writeFieldName("topActivityTable");
        jsonGenerator.writeStartArray();

        if (topActivityTableList.size() != 0) {
            for (Reports.TopActivityTable topActivityTable : topActivityTableList) {
                jsonGenerator.writeStartObject();

                if (topActivityTable != null) {
                    jsonGenerator.writeStringField("queryid", topActivityTable.getQueryid());
                    jsonGenerator.writeStringField("query", topActivityTable.getQuery());
                    jsonGenerator.writeStringField("totalTime", topActivityTable.getTotalTime());
                }

                jsonGenerator.writeEndObject();
            }
        }

        jsonGenerator.writeEndArray();
    }

    private void writeArray(String fieldName, List<?> list, JsonGenerator jsonGenerator) throws IOException {
        jsonGenerator.writeFieldName(fieldName);
        jsonGenerator.writeStartArray();
        for (Object obj : list) {
            jsonGenerator.writeObject(obj);
        }
        jsonGenerator.writeEndArray();
    }

    private void writeArrayArray(String fieldName, List<? extends List<?>> lists, JsonGenerator jsonGenerator) throws IOException {
        jsonGenerator.writeFieldName(fieldName);
        jsonGenerator.writeStartArray();
        for (List<?> list : lists) {
            jsonGenerator.writeStartArray();
            for (Object obj : list) {
                jsonGenerator.writeObject(obj);
            }
            jsonGenerator.writeEndArray();
        }
        jsonGenerator.writeEndArray();
    }
}
