package com.zte.ums.oespaas.mysql.bean.hbase;


import com.zte.ums.oespaas.mysql.bean.buffer.AutomaticBuffer;
import com.zte.ums.oespaas.mysql.bean.buffer.Buffer;
import com.zte.ums.oespaas.mysql.bean.buffer.FixedBuffer;

public class SlowLogBean extends AbstractBean {
    //neId == dbNeId
    private String beginTime;
    private String userHost;
    private String queryTime;
    private String lockTime;
    private String dbName;
    private String sqlText;

    public String getBeginTime() {
        return beginTime;
    }

    public void setBeginTime(String beginTime) {
        this.beginTime = beginTime;
    }

    public String getUserHost() {
        return userHost;
    }

    public void setUserHost(String userHost) {
        this.userHost = userHost;
    }

    public String getQueryTime() {
        return queryTime;
    }

    public void setQueryTime(String queryTime) {
        this.queryTime = queryTime;
    }

    public String getLockTime() {
        return lockTime;
    }

    public void setLockTime(String lockTime) {
        this.lockTime = lockTime;
    }

    public String getDbName() {
        return dbName;
    }

    public void setDbName(String dbName) {
        this.dbName = dbName;
    }

    public String getSqlText() {
        return sqlText;
    }

    public void setSqlText(String sqlText) {
        this.sqlText = sqlText;
    }

    public byte[] writeValue() {
        final Buffer buffer = new AutomaticBuffer();
        buffer.put(this.getGranularity());
        buffer.putPrefixedString(this.getCollectTime());
        buffer.putPrefixedString(this.getBeginTime());
        buffer.putPrefixedString(this.getUserHost());
        buffer.putPrefixedString(this.getQueryTime());
        buffer.putPrefixedString(this.getLockTime());
        buffer.putPrefixedString(this.getDbName());
        buffer.putPrefixedString(this.getSqlText());
//        buffer.putPrefixedString(this.getNeId());
//        buffer.put(this.getTaskId());

        return buffer.getBuffer();
    }

    public int readValue(byte[] bytes) {
        final Buffer buffer = new FixedBuffer(bytes);
        this.setGranularity(buffer.readInt());
        this.setCollectTime(buffer.readPrefixedString());
        this.beginTime = buffer.readPrefixedString();
        this.userHost = buffer.readPrefixedString();
        this.queryTime = buffer.readPrefixedString();
        this.lockTime = buffer.readPrefixedString();
        this.dbName = buffer.readPrefixedString();
        this.sqlText = buffer.readPrefixedString();
//        this.setNeId(buffer.readPrefixedString());
//        this.setTaskId(buffer.readInt());

        return buffer.getOffset();
    }

    @Override
    public String toString() {
        return "SlowLogBean{" +
                "taskId='" + this.getTaskId() + "\'," +
                "neId='" + this.getNeId() + "\'," +
                "collectTime='" + this.getCollectTime() + "\'," +
                "granularity='" + this.getGranularity() + "\'," +
                "beginTime='" + beginTime + '\'' +
                ", userHost='" + userHost + '\'' +
                ", queryTime='" + queryTime + '\'' +
                ", lockTime='" + lockTime + '\'' +
                ", dbName='" + dbName + '\'' +
                ", sqlText='" + sqlText + '\'' +
                '}';
    }
}
