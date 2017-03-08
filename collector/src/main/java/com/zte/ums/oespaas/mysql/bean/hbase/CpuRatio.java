package com.zte.ums.oespaas.mysql.bean.hbase;

import com.zte.ums.oespaas.mysql.bean.buffer.AutomaticBuffer;
import com.zte.ums.oespaas.mysql.bean.buffer.Buffer;
import com.zte.ums.oespaas.mysql.bean.buffer.FixedBuffer;

/**
 * Created by 10183966 on 8/31/16.
 */
public class CpuRatio extends AbstractBean {
    //neId == osNeId
    private String cpuIOWaitRatio;
    private String cpuSysRatio;
    private String cpuUserRatio;

    public String getCpuIOWaitRatio() {
        return cpuIOWaitRatio;
    }

    public void setCpuIOWaitRatio(String cpuIOWaitRatio) {
        this.cpuIOWaitRatio = cpuIOWaitRatio;
    }

    public String getCpuSysRatio() {
        return cpuSysRatio;
    }

    public void setCpuSysRatio(String cpuSysRatio) {
        this.cpuSysRatio = cpuSysRatio;
    }

    public String getCpuUserRatio() {
        return cpuUserRatio;
    }

    public void setCpuUserRatio(String cpuUserRatio) {
        this.cpuUserRatio = cpuUserRatio;
    }

    @Override
    public byte[] writeValue() {
        final Buffer buffer = new AutomaticBuffer();
        buffer.put(this.getGranularity());
        buffer.putPrefixedString(this.getCollectTime());
        buffer.putPrefixedString(this.getCpuIOWaitRatio());
        buffer.putPrefixedString(this.getCpuSysRatio());
        buffer.putPrefixedString(this.getCpuUserRatio());
//        buffer.putPrefixedString(this.getNeId());
//        buffer.put(this.getTaskId());

        return buffer.getBuffer();
    }

    @Override
    public int readValue(byte[] bytes) {
        final Buffer buffer = new FixedBuffer(bytes);
        this.setGranularity(buffer.readInt());
        this.setCollectTime(buffer.readPrefixedString());
        this.cpuIOWaitRatio = buffer.readPrefixedString();
        this.cpuSysRatio = buffer.readPrefixedString();
        this.cpuUserRatio = buffer.readPrefixedString();
//        this.setNeId(buffer.readPrefixedString());
//        this.setTaskId(buffer.readInt());

        return buffer.getOffset();
    }

    @Override
    public String toString() {
        return "CpuRatio{" +
                "taskId='" + this.getTaskId() + "\'," +
                "neId='" + this.getNeId() + "\'," +
                "collectTime='" + this.getCollectTime() + "\'," +
                "granularity='" + this.getGranularity() + "\'," +
                "cpuIOWaitRatio='" + cpuIOWaitRatio + '\'' +
                ", cpuSysRatio='" + cpuSysRatio + '\'' +
                ", cpuUserRatio='" + cpuUserRatio + '\'' +
                '}';
    }
}
