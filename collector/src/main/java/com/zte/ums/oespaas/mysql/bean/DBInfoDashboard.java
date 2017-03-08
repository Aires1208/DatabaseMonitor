package com.zte.ums.oespaas.mysql.bean;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import java.util.List;

/**
 * Created by root on 9/2/16.
 */

@JsonSerialize(using = DBInfoDashboardSerializer.class)
public class DBInfoDashboard {
    private Summary summary;
    private LoadTimeSpent loadTimeSpent;
    private List<SqlWaitStates> sqlWaitStatesList;
    private AvgConnect avgConnect;
    private CpuInfo cpuInfo;
    private MemInfo memInfo;
    private DiskInfo diskInfo;
    private NetInfo netInfo;

    public DBInfoDashboard(Summary summary,
                           LoadTimeSpent loadTimeSpent,
                           List<SqlWaitStates> sqlWaitStatesList,
                           AvgConnect avgConnect,
                           CpuInfo cpuInfo,
                           MemInfo memInfo,
                           DiskInfo diskInfo,
                           NetInfo netInfo) {
        this.summary = summary;
        this.loadTimeSpent = loadTimeSpent;
        this.sqlWaitStatesList = sqlWaitStatesList;
        this.avgConnect = avgConnect;
        this.cpuInfo = cpuInfo;
        this.memInfo = memInfo;
        this.diskInfo = diskInfo;
        this.netInfo = netInfo;
    }

    public Summary getSummary() {
        return summary;
    }

    public LoadTimeSpent getLoadTimeSpent() {
        return loadTimeSpent;
    }

    public List<SqlWaitStates> getSqlWaitStatesList() {
        return sqlWaitStatesList;
    }

    public AvgConnect getAvgConnect() {
        return avgConnect;
    }

    public CpuInfo getCpuInfo() {
        return cpuInfo;
    }

    public MemInfo getMemInfo() {
        return memInfo;
    }

    public DiskInfo getDiskInfo() {
        return diskInfo;
    }

    public NetInfo getNetInfo() {
        return netInfo;
    }

    public static class Summary {
        private String dbName;
        private boolean dbHealth;
        private String dbType;
        private String totalExec;

        public Summary(String dbName, boolean dbHealth, String dbType, String totalExec) {
            this.dbName = dbName;
            this.dbHealth = dbHealth;
            this.dbType = dbType;
            this.totalExec = totalExec;
        }

        public String getDbName() {
            return dbName;
        }

        public boolean getDbHealth() {
            return dbHealth;
        }

        public String getDbType() {
            return dbType;
        }

        public String getTotalExec() {
            return totalExec;
        }
    }

    public static class LoadTimeSpent {
        private String info;
        private List<String> time;
        private List<Long> load;
        private List<Long> timeSpent;

        public LoadTimeSpent(String info, List<String> time, List<Long> load, List<Long> timeSpent) {
            this.info = info;
            this.time = time;
            this.load = load;
            this.timeSpent = timeSpent;
        }

        public String getInfo() {
            return info;
        }

        public List<String> getTime() {
            return time;
        }

        public List<Long> getLoad() {
            return load;
        }

        public List<Long> getTimeSpent() {
            return timeSpent;
        }
    }

    public static class SqlWaitStates {
        private String name;
        private Integer value;

        public SqlWaitStates(String name, Integer value) {
            this.name = name;
            this.value = value;
        }

        public String getName() {
            return name;
        }

        public Integer getValue() {
            return value;
        }
    }

    public static class AvgConnect {
        private String info;
        private List<String> time;
        private List<Integer> data;

        public AvgConnect(String info, List<String> time, List<Integer> data) {
            this.info = info;
            this.time = time;
            this.data = data;
        }

        public String getInfo() {
            return info;
        }

        public List<String> getTime() {
            return time;
        }

        public List<Integer> getData() {
            return data;
        }
    }

    public static class CpuInfo {
        private String info;
        private List<String> time;
        private List<Double> sys;
        private List<Double> user;

        public CpuInfo(String info, List<String> time, List<Double> sys, List<Double> user) {
            this.info = info;
            this.time = time;
            this.sys = sys;
            this.user = user;
        }

        public String getInfo() {
            return info;
        }

        public List<String> getTime() {
            return time;
        }

        public List<Double> getSys() {
            return sys;
        }

        public List<Double> getUser() {
            return user;
        }
    }

    public static class MemInfo {
        private String info;
        private List<String> time;
        private List<Double> data;

        public MemInfo(String info, List<String> time, List<Double> data) {
            this.info = info;
            this.time = time;
            this.data = data;
        }

        public String getInfo() {
            return info;
        }

        public List<String> getTime() {
            return time;
        }

        public List<Double> getData() {
            return data;
        }
    }

    public static class DiskInfo {
        private String info;
        private List<String> time;
        private List<Double> incoming;
        private List<Double> outgoing;

        public DiskInfo(String info, List<String> time, List<Double> incoming, List<Double> outgoing) {
            this.info = info;
            this.time = time;
            this.incoming = incoming;
            this.outgoing = outgoing;
        }

        public String getInfo() {
            return info;
        }

        public List<String> getTime() {
            return time;
        }

        public List<Double> getIncoming() {
            return incoming;
        }

        public List<Double> getOutgoing() {
            return outgoing;
        }
    }

    public static class NetInfo {
        private String info;
        private List<String> time;
        private List<Double> incoming;
        private List<Double> outgoing;

        public NetInfo(String info, List<String> time, List<Double> incoming, List<Double> outgoing) {
            this.info = info;
            this.time = time;
            this.incoming = incoming;
            this.outgoing = outgoing;
        }

        public String getInfo() {
            return info;
        }

        public List<String> getTime() {
            return time;
        }

        public List<Double> getIncoming() {
            return incoming;
        }

        public List<Double> getOutgoing() {
            return outgoing;
        }
    }
}
