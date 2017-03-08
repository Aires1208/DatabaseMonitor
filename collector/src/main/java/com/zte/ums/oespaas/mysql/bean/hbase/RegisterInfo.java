package com.zte.ums.oespaas.mysql.bean.hbase;


import com.zte.ums.oespaas.mysql.bean.buffer.AutomaticBuffer;
import com.zte.ums.oespaas.mysql.bean.buffer.Buffer;
import com.zte.ums.oespaas.mysql.bean.buffer.FixedBuffer;

/**
 * Created by 10183966 on 8/22/16.
 */
public class RegisterInfo {
    //rowkey=osNeId^collectTime

    private String osNeId;
    private String dbNeId;
    private String collectTime;
    private String dbName;
    private String url;
    private String dbType;
    private String dbUsername;
    private String dbPassword;
    private long dbPort;
    private String dbSID;
    private String connectType;
    private long hostPort;
    private String hostUsername;
    private String hostPassword;

    public RegisterInfo() {
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

    public String getDbName() {
        return dbName;
    }

    public void setDbName(String dbName) {
        this.dbName = dbName;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getDbType() {
        return dbType;
    }

    public void setDbType(String dbType) {
        this.dbType = dbType;
    }

    public String getDbUsername() {
        return dbUsername;
    }

    public void setDbUsername(String dbUsername) {
        this.dbUsername = dbUsername;
    }

    public String getDbPassword() {
        return dbPassword;
    }

    public void setDbPassword(String dbPassword) {
        this.dbPassword = dbPassword;
    }

    public long getDbPort() {
        return dbPort;
    }

    public void setDbPort(long dbPort) {
        this.dbPort = dbPort;
    }

    public String getDbSID() {
        return dbSID;
    }

    public void setDbSID(String dbSID) {
        this.dbSID = dbSID;
    }

    public String getConnectType() {
        return connectType;
    }

    public void setConnectType(String connectType) {
        this.connectType = connectType;
    }

    public long getHostPort() {
        return hostPort;
    }

    public void setHostPort(long hostPort) {
        this.hostPort = hostPort;
    }

    public String getHostUsername() {
        return hostUsername;
    }

    public void setHostUsername(String hostUsername) {
        this.hostUsername = hostUsername;
    }

    public String getHostPassword() {
        return hostPassword;
    }

    public void setHostPassword(String hostPassword) {
        this.hostPassword = hostPassword;
    }


    public byte[] writeValue() {
        final Buffer buffer = new AutomaticBuffer();
        buffer.putPrefixedString(this.getDbName());
        buffer.putPrefixedString(this.getUrl());
        buffer.putPrefixedString(this.getDbType());
        buffer.putPrefixedString(this.getDbUsername());
        buffer.putPrefixedString(this.getDbPassword());
        buffer.put(this.getDbPort());
        buffer.putPrefixedString(this.getDbSID());
        buffer.putPrefixedString(this.getConnectType());
        buffer.put(this.getHostPort());
        buffer.putPrefixedString(this.getHostUsername());
        buffer.putPrefixedString(this.getHostPassword());
        buffer.putPrefixedString(this.getOsNeId());
        buffer.putPrefixedString(this.getDbNeId());

        return buffer.getBuffer();
    }

    public int readValue(byte[] bytes) {
        final Buffer buffer = new FixedBuffer(bytes);
        this.dbName = buffer.readPrefixedString();
        this.url = buffer.readPrefixedString();
        this.dbType = buffer.readPrefixedString();
        this.dbUsername = buffer.readPrefixedString();
        this.dbPassword = buffer.readPrefixedString();
        this.dbPort = buffer.readLong();
        this.dbSID = buffer.readPrefixedString();
        this.connectType = buffer.readPrefixedString();
        this.hostPort = buffer.readLong();
        this.hostUsername = buffer.readPrefixedString();
        this.hostPassword = buffer.readPrefixedString();
        this.osNeId = buffer.readPrefixedString();
        this.dbNeId = buffer.readPrefixedString();

        return buffer.getOffset();
    }


    @Override
    public String toString() {
        return "RegisterInfo{" +
                "osNeId='" + osNeId + '\'' +
                ", dbNeId='" + dbNeId + '\'' +
                ", collectTime='" + collectTime + '\'' +
                ", dbName='" + dbName + '\'' +
                ", url='" + url + '\'' +
                ", dbType='" + dbType + '\'' +
                ", dbUsername='" + dbUsername + '\'' +
                ", dbPassword='" + dbPassword + '\'' +
                ", dbPort=" + dbPort +
                ", dbSID='" + dbSID + '\'' +
                ", connectType='" + connectType + '\'' +
                ", hostPort=" + hostPort +
                ", hostUsername='" + hostUsername + '\'' +
                ", hostPassword='" + hostPassword + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        RegisterInfo that = (RegisterInfo) o;

        if (dbPort != that.dbPort) return false;
        if (dbName != null ? !dbName.equals(that.dbName) : that.dbName != null) return false;
        return url != null ? url.equals(that.url) : that.url == null;

    }

    @Override
    public int hashCode() {
        int result = dbName != null ? dbName.hashCode() : 0;
        result = 31 * result + (url != null ? url.hashCode() : 0);
        result = 31 * result + (int) (dbPort ^ (dbPort >>> 32));
        return result;
    }
}