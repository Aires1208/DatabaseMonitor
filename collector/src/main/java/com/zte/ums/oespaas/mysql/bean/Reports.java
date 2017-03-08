package com.zte.ums.oespaas.mysql.bean;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import java.util.List;

/**
 * Created by root on 9/7/16.
 */
@JsonSerialize(using = ReportsSerializer.class)
public class Reports {
    private WaitStateChart waitStateChart;
    private List<WaitStateTable> waitStateTableList;
    private TopActivityChart topActivityChart;
    private List<TopActivityTable> topActivityTableList;

    public Reports(WaitStateChart waitStateChart, List<WaitStateTable> waitStateTableList,
                   TopActivityChart topActivityChart, List<TopActivityTable> topActivityTableList) {
        this.waitStateChart = waitStateChart;
        this.waitStateTableList = waitStateTableList;
        this.topActivityChart = topActivityChart;
        this.topActivityTableList = topActivityTableList;
    }

    public WaitStateChart getWaitStateChart() {
        return waitStateChart;
    }

    public List<WaitStateTable> getWaitStateTableList() {
        return waitStateTableList;
    }

    public TopActivityChart getTopActivityChart() {
        return topActivityChart;
    }

    public List<TopActivityTable> getTopActivityTableList() {
        return topActivityTableList;
    }

    public static class WaitStateChart {
        private List<String> legend;
        private List<String> time;
        private List<List<Long>> data;

        public WaitStateChart(List<String> legend, List<String> time, List<List<Long>> data) {
            this.legend = legend;
            this.time = time;
            this.data = data;
        }

        public List<String> getLegend() {
            return legend;
        }

        public List<String> getTime() {
            return time;
        }

        public List<List<Long>> getData() {
            return data;
        }
    }

    public static class WaitStateTable {
        private String waitState;
        private String description;
        private String totalTime;

        public WaitStateTable(String waitState, String description, String totalTime) {
            this.waitState = waitState;
            this.description = description;
            this.totalTime = totalTime;
        }

        public String getWaitState() {
            return waitState;
        }

        public String getDescription() {
            return description;
        }

        public String getTotalTime() {
            return totalTime;
        }
    }

    public static class TopActivityChart {
        private List<String> legend;
        private List<String> time;
        private List<List<Long>> data;

        public TopActivityChart(List<String> legend, List<String> time, List<List<Long>> data) {
            this.legend = legend;
            this.time = time;
            this.data = data;
        }

        public List<String> getLegend() {
            return legend;
        }

        public List<String> getTime() {
            return time;
        }

        public List<List<Long>> getData() {
            return data;
        }
    }

    public static class TopActivityTable {
        private String queryid;
        private String query;
        private String totalTime;

        public TopActivityTable(String queryid, String query, String totalTime) {
            this.queryid = queryid;
            this.query = query;
            this.totalTime = totalTime;
        }

        public String getQueryid() {
            return queryid;
        }

        public String getQuery() {
            return query;
        }

        public String getTotalTime() {
            return totalTime;
        }
    }
}
