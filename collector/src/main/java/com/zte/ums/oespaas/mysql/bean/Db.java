package com.zte.ums.oespaas.mysql.bean;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;

/**
 * Created by 10183966 on 8/17/16.
 */
@JsonSerialize(using = DbSerializer.class)
public class Db {
    private String dbName;
    private String dbHost;
    private String osNeId;
    private String dbNeId;
    private String collectTime;
    private String type;
    private String status;
    private boolean health;
    private long queries;
    private String timeSpent;
    private String cpu;

    public String getDbName() {
        return dbName;
    }

    public void setDbName(String dbName) {
        this.dbName = dbName;
    }

    public String getDbHost() {
        return dbHost;
    }

    public void setDbHost(String dbHost) {
        this.dbHost = dbHost;
    }

    public String getOsNeId() {
        return osNeId;
    }

    public void setOsNeId(String osNeId) {
        this.osNeId = osNeId;
    }

    public String getDbNeId() {
        return dbNeId;
    }

    public void setDbNeId(String dbNeId) {
        this.dbNeId = dbNeId;
    }

    public String getCollectTime() {
        return collectTime;
    }

    public void setCollectTime(String collectTime) {
        this.collectTime = collectTime;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public boolean isHealth() {
        return health;
    }

    public void setHealth(boolean health) {
        this.health = health;
    }

    public long getQueries() {
        return queries;
    }

    public void setQueries(long queries) {
        this.queries = queries;
    }

    public String getTimeSpent() {
        return timeSpent;
    }

    public void setTimeSpent(String timeSpent) {
        this.timeSpent = timeSpent;
    }

    public String getCpu() {
        return cpu;
    }

    public void setCpu(String cpu) {
        this.cpu = cpu;
    }
}
