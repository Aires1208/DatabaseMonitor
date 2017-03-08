package com.zte.ums.oespaas.mysql.bean.hbase;

public abstract class AbstractBean {
    private String metricId;
    private int taskId;
    private String neId;
    private int granularity;
    private String collectTime;

    public String getMetricId() {
        return metricId;
    }

    public void setMetricId(String metricId) {
        this.metricId = metricId;
    }

    public int getTaskId() {
        return taskId;
    }

    public void setTaskId(int taskId) {
        this.taskId = taskId;
    }

    public String getNeId() {
        return neId;
    }

    public void setNeId(String neId) {
        this.neId = neId;
    }

    public int getGranularity() {
        return granularity;
    }

    public void setGranularity(int granularity) {
        this.granularity = granularity;
    }

    public String getCollectTime() {
        return collectTime;
    }

    public void setCollectTime(String collectTime) {
        this.collectTime = collectTime;
    }

    public abstract byte[] writeValue();

    public abstract int readValue(byte[] bytes);

    @Override
    public String toString() {
        return "AbstractBean{" +
                "metricId='" + metricId + '\'' +
                ", taskId=" + taskId +
                ", neId='" + neId + '\'' +
                ", granularity=" + granularity +
                ", collectTime='" + collectTime + '\'' +
                '}';
    }
}
