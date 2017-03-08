package com.zte.ums.oespaas.mysql.bean.hbase;


import com.zte.ums.oespaas.mysql.bean.buffer.AutomaticBuffer;
import com.zte.ums.oespaas.mysql.bean.buffer.Buffer;
import com.zte.ums.oespaas.mysql.bean.buffer.FixedBuffer;

public class SessionBean extends AbstractBean {
    //neId == dbNeId
    private String oid;
    private String user;
    private String hostName;
    private String dbname;
    private String command;
    private String speedTime;
    private String state;
    private String sqlInfo;

    public String getOid() {
        return oid;
    }

    public void setOid(String oid) {
        this.oid = oid;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getHostName() {
        return hostName;
    }

    public void setHostName(String hostName) {
        this.hostName = hostName;
    }

    public String getDbname() {
        return dbname;
    }

    public void setDbname(String dbname) {
        this.dbname = dbname;
    }

    public String getCommand() {
        return command;
    }

    public void setCommand(String command) {
        this.command = command;
    }

    public String getSpeedTime() {
        return speedTime;
    }

    public void setSpeedTime(String speedTime) {
        this.speedTime = speedTime;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
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
        buffer.putPrefixedString(this.getOid());
        buffer.putPrefixedString(this.getUser());
        buffer.putPrefixedString(this.getHostName());
        buffer.putPrefixedString(this.getDbname());
        buffer.putPrefixedString(this.getCommand());
        buffer.putPrefixedString(this.getState());
        buffer.putPrefixedString(this.getSqlInfo());
        buffer.putPrefixedString(this.getSpeedTime());
//        buffer.putPrefixedString(this.getNeId());
//        buffer.put(this.getTaskId());

        return buffer.getBuffer();
    }

    public int readValue(byte[] bytes) {
        final Buffer buffer = new FixedBuffer(bytes);
        this.setGranularity(buffer.readInt());
        this.setCollectTime(buffer.readPrefixedString());
        this.oid = buffer.readPrefixedString();
        this.user = buffer.readPrefixedString();
        this.hostName = buffer.readPrefixedString();
        this.dbname = buffer.readPrefixedString();
        this.command = buffer.readPrefixedString();
        this.state = buffer.readPrefixedString();
        this.sqlInfo = buffer.readPrefixedString();
        this.speedTime = buffer.readPrefixedString();
//        this.setNeId(buffer.readPrefixedString());
//        this.setTaskId(buffer.readInt());

        return buffer.getOffset();
    }

    @Override
    public String toString() {
        return "SessionBean{" +
                "taskId='" + this.getTaskId() + "\'," +
                "neId='" + this.getNeId() + "\'," +
                "collectTime='" + this.getCollectTime() + "\'," +
                "granularity='" + this.getGranularity() + "\'," +
                "oid='" + oid + '\'' +
                ", user='" + user + '\'' +
                ", hostName='" + hostName + '\'' +
                ", dbname='" + dbname + '\'' +
                ", command='" + command + '\'' +
                ", speedTime='" + speedTime + '\'' +
                ", state='" + state + '\'' +
                ", sqlInfo='" + sqlInfo + '\'' +
                '}';
    }
}
