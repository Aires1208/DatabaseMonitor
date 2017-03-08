package com.zte.ums.oespaas.mysql.bean.hbase;


import com.zte.ums.oespaas.mysql.bean.buffer.AutomaticBuffer;
import com.zte.ums.oespaas.mysql.bean.buffer.Buffer;
import com.zte.ums.oespaas.mysql.bean.buffer.FixedBuffer;

public class GeneralLogBean extends AbstractBean {
    //neId == dbNeId
    private String eventTime;
    private String userHost;
    private String commandType;
    private String sqlInfo;

    public String getEventTime() {
        return eventTime;
    }

    public void setEventTime(String eventTime) {
        this.eventTime = eventTime;
    }

    public String getUserHost() {
        return userHost;
    }

    public void setUserHost(String userHost) {
        this.userHost = userHost;
    }

    public String getCommandType() {
        return commandType;
    }

    public void setCommandType(String commandType) {
        this.commandType = commandType;
    }

    public String getSqlInfo() {
        return sqlInfo;
    }

    public void setSqlInfo(String sqlInfo) {
        this.sqlInfo = sqlInfo;
    }

    public byte[] writeValue() {
        final Buffer buffer = new AutomaticBuffer();
        buffer.put(this.getGranularity());
        buffer.putPrefixedString(this.getCollectTime());
        buffer.putPrefixedString(this.getEventTime());
        buffer.putPrefixedString(this.getUserHost());
        buffer.putPrefixedString(this.getCommandType());
        buffer.putPrefixedString(this.getSqlInfo());
//        buffer.putPrefixedString(this.getNeId());
//        buffer.put(this.getTaskId());

        return buffer.getBuffer();
    }

    public int readValue(byte[] bytes) {
        final Buffer buffer = new FixedBuffer(bytes);
        this.setGranularity(buffer.readInt());
        this.setCollectTime(buffer.readPrefixedString());
        this.eventTime = buffer.readPrefixedString();
        this.userHost = buffer.readPrefixedString();
        this.commandType = buffer.readPrefixedString();
        this.sqlInfo = buffer.readPrefixedString();
//        this.setNeId(buffer.readPrefixedString());
//        this.setTaskId(buffer.readInt());

        return buffer.getOffset();
    }

    @Override
    public String toString() {
        return "GeneralLogBean{" +
                "taskId='" + this.getTaskId() + "\'," +
                "neId='" + this.getNeId() + "\'," +
                "collectTime='" + this.getCollectTime() + "\'," +
                "granularity='" + this.getGranularity() + "\'," +
                "eventTime='" + eventTime + '\'' +
                ", userHost='" + userHost + '\'' +
                ", commandType='" + commandType + '\'' +
                ", sqlInfo='" + sqlInfo + '\'' +
                '}';
    }
}
