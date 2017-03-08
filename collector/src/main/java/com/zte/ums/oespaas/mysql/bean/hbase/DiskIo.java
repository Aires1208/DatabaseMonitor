package com.zte.ums.oespaas.mysql.bean.hbase;

import com.zte.ums.oespaas.mysql.bean.buffer.AutomaticBuffer;
import com.zte.ums.oespaas.mysql.bean.buffer.Buffer;
import com.zte.ums.oespaas.mysql.bean.buffer.FixedBuffer;

/**
 * Created by 10183966 on 8/31/16.
 */
public class DiskIo extends AbstractBean {
    //neId == osNeId
    private String diskReadBytes;
    private String mysqlBytes;
    private String diskWriteBytes;

    public String getDiskReadBytes() {
        return diskReadBytes;
    }

    public void setDiskReadBytes(String diskReadBytes) {
        this.diskReadBytes = diskReadBytes;
    }

    public String getMysqlBytes() {
        return mysqlBytes;
    }

    public void setMysqlBytes(String mysqlBytes) {
        this.mysqlBytes = mysqlBytes;
    }

    public String getDiskWriteBytes() {
        return diskWriteBytes;
    }

    public void setDiskWriteBytes(String diskWriteBytes) {
        this.diskWriteBytes = diskWriteBytes;
    }

    @Override
    public byte[] writeValue() {
        final Buffer buffer = new AutomaticBuffer();
        buffer.put(this.getGranularity());
        buffer.putPrefixedString(this.getCollectTime());
        buffer.putPrefixedString(this.getDiskReadBytes());
        buffer.putPrefixedString(this.getMysqlBytes());
        buffer.putPrefixedString(this.getDiskWriteBytes());
//        buffer.putPrefixedString(this.getNeId());
//        buffer.put(this.getTaskId());

        return buffer.getBuffer();
    }

    public int readValue(byte[] bytes) {
        final Buffer buffer = new FixedBuffer(bytes);
        this.setGranularity(buffer.readInt());
        this.setCollectTime(buffer.readPrefixedString());
        this.diskReadBytes = buffer.readPrefixedString();
        this.mysqlBytes = buffer.readPrefixedString();
        this.diskWriteBytes = buffer.readPrefixedString();
//        this.setNeId(buffer.readPrefixedString());
//        this.setTaskId(buffer.readInt());

        return buffer.getOffset();
    }

    @Override
    public String toString() {
        return "DiskIo{" +
                "taskId='" + this.getTaskId() + "\'," +
                "neId='" + this.getNeId() + "\'," +
                "collectTime='" + this.getCollectTime() + "\'," +
                "granularity='" + this.getGranularity() + "\'," +
                "diskReadBytes='" + diskReadBytes + '\'' +
                ", mysqlBytes='" + mysqlBytes + '\'' +
                ", diskWriteBytes='" + diskWriteBytes + '\'' +
                '}';
    }
}
