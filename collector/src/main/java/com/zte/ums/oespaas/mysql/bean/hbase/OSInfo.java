package com.zte.ums.oespaas.mysql.bean.hbase;


public class OSInfo {
    //rowkey=osNeId^collectTime

    private String osNeId;
    private String collectTime;
    private CpuRatio cpuRatio;
    private MemoryRatio memoryRatio;
    private NetworkIo networkIo;
    private DiskIo diskIo;

    public OSInfo() {
    }

    public String getOsNeId() {
        return osNeId;
    }

    public void setOsNeId(String osNeId) {
        this.osNeId = osNeId;
    }

    public String getCollectTime() {
        return collectTime;
    }

    public void setCollectTime(String collectTime) {
        this.collectTime = collectTime;
    }

    public CpuRatio getCpuRatio() {
        return cpuRatio;
    }

    public void setCpuRatio(CpuRatio cpuRatio) {
        this.cpuRatio = cpuRatio;
    }

    public MemoryRatio getMemoryRatio() {
        return memoryRatio;
    }

    public void setMemoryRatio(MemoryRatio memoryRatio) {
        this.memoryRatio = memoryRatio;
    }

    public NetworkIo getNetworkIo() {
        return networkIo;
    }

    public void setNetworkIo(NetworkIo networkIo) {
        this.networkIo = networkIo;
    }

    public DiskIo getDiskIo() {
        return diskIo;
    }

    public void setDiskIo(DiskIo diskIo) {
        this.diskIo = diskIo;
    }

    @Override
    public String toString() {
        return "OSInfo{" +
                "osNeId='" + osNeId + '\'' +
                ", collectTime='" + collectTime + '\'' +
                ", cpuRatio=" + cpuRatio +
                ", memoryRatio=" + memoryRatio +
                ", networkIo=" + networkIo +
                ", diskIo=" + diskIo +
                '}';
    }
}
