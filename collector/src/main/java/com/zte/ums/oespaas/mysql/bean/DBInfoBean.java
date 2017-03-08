package com.zte.ums.oespaas.mysql.bean;


import com.zte.ums.oespaas.mysql.bean.buffer.AutomaticBuffer;
import com.zte.ums.oespaas.mysql.bean.buffer.Buffer;
import com.zte.ums.oespaas.mysql.bean.buffer.FixedBuffer;
import com.zte.ums.oespaas.mysql.bean.hbase.AbstractBean;

public class DBInfoBean extends AbstractBean {
    //neId == dbNeId
    private String dbName;
    private String dataLength;
    private String indexLength;

    public String getDbName() {
        return dbName;
    }

    public void setDbName(String dbName) {
        this.dbName = dbName;
    }

    public String getDataLength() {
        return dataLength;
    }

    public void setDataLength(String dataLength) {
        this.dataLength = dataLength;
    }

    public String getIndexLength() {
        return indexLength;
    }

    public void setIndexLength(String indexLength) {
        this.indexLength = indexLength;
    }

    public byte[] writeValue() {
        final Buffer buffer = new AutomaticBuffer();
        buffer.put(this.getGranularity());
        buffer.putPrefixedString(this.getCollectTime());
        buffer.putPrefixedString(this.getDbName());
        buffer.putPrefixedString(this.getIndexLength());
        buffer.putPrefixedString(this.getDataLength());
//        buffer.putPrefixedString(this.getNeId());
//        buffer.put(this.getTaskId());

        return buffer.getBuffer();
    }

    public int readValue(byte[] bytes) {
        final Buffer buffer = new FixedBuffer(bytes);
        this.setGranularity(buffer.readInt());
        this.setCollectTime(buffer.readPrefixedString());
        this.setDbName(buffer.readPrefixedString());
        this.setIndexLength(buffer.readPrefixedString());
        this.setDataLength(buffer.readPrefixedString());
//        this.setNeId(buffer.readPrefixedString());
//        this.setTaskId(buffer.readInt());

        return buffer.getOffset();
    }

    @Override
    public String toString() {
        return "DBInfoBean{" +
                "taskId='" + this.getTaskId() + "\'," +
                "neId='" + this.getNeId() + "\'," +
                "collectTime='" + this.getCollectTime() + "\'," +
                "granularity='" + this.getGranularity() + "\'," +
                "dbName='" + dbName + '\'' +
                ", dataLength='" + dataLength + '\'' +
                ", indexLength='" + indexLength + '\'' +
                '}';
    }
}
