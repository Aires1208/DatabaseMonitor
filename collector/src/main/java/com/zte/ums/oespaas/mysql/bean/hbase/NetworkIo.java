package com.zte.ums.oespaas.mysql.bean.hbase;

import com.zte.ums.oespaas.mysql.bean.buffer.AutomaticBuffer;
import com.zte.ums.oespaas.mysql.bean.buffer.Buffer;
import com.zte.ums.oespaas.mysql.bean.buffer.FixedBuffer;

/**
 * Created by 10183966 on 8/31/16.
 */
public class NetworkIo extends AbstractBean {
    //neId == osNeId
    private String mySQLInTransrate;
    private String periodIntransRate;
    private String mySQLOutTransrate;

    public String getMySQLInTransrate() {
        return mySQLInTransrate;
    }

    public void setMySQLInTransrate(String mySQLInTransrate) {
        this.mySQLInTransrate = mySQLInTransrate;
    }

    public String getPeriodIntransRate() {
        return periodIntransRate;
    }

    public void setPeriodIntransRate(String periodIntransRate) {
        this.periodIntransRate = periodIntransRate;
    }

    public String getMySQLOutTransrate() {
        return mySQLOutTransrate;
    }

    public void setMySQLOutTransrate(String mySQLOutTransrate) {
        this.mySQLOutTransrate = mySQLOutTransrate;
    }

    @Override
    public byte[] writeValue() {
        final Buffer buffer = new AutomaticBuffer();
        buffer.put(this.getGranularity());
        buffer.putPrefixedString(this.getCollectTime());
        buffer.putPrefixedString(this.getMySQLInTransrate());
        buffer.putPrefixedString(this.getPeriodIntransRate());
        buffer.putPrefixedString(this.getMySQLOutTransrate());
//        buffer.putPrefixedString(this.getNeId());
//        buffer.put(this.getTaskId());

        return buffer.getBuffer();
    }

    public int readValue(byte[] bytes) {
        final Buffer buffer = new FixedBuffer(bytes);
        this.setGranularity(buffer.readInt());
        this.setCollectTime(buffer.readPrefixedString());
        this.mySQLInTransrate = buffer.readPrefixedString();
        this.periodIntransRate = buffer.readPrefixedString();
        this.mySQLOutTransrate = buffer.readPrefixedString();
//        this.setNeId(buffer.readPrefixedString());
//        this.setTaskId(buffer.readInt());

        return buffer.getOffset();
    }

    @Override
    public String toString() {
        return "NetworkIo{" +
                "taskId='" + this.getTaskId() + "\'," +
                "neId='" + this.getNeId() + "\'," +
                "collectTime='" + this.getCollectTime() + "\'," +
                "granularity='" + this.getGranularity() + "\'," +
                "mySQLInTransrate='" + mySQLInTransrate + '\'' +
                ", periodIntransRate='" + periodIntransRate + '\'' +
                ", mySQLOutTransrate='" + mySQLOutTransrate + '\'' +
                '}';
    }
}
