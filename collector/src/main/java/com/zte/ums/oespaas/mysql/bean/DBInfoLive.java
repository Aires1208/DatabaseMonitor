package com.zte.ums.oespaas.mysql.bean;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import java.util.Comparator;
import java.util.List;

/**
 * Created by root on 9/2/16.
 */

@JsonSerialize(using = DBInfoLiveSerializer.class)
public class DBInfoLive {
    private CpuUsage cpuUsage;
    private List<MemUsage> memUsageList;
    private List<SqlWaitStateLive> sqlWaitStateLiveList;
    private List<Session> sessionList;

    public DBInfoLive(CpuUsage cpuUsage, List<MemUsage> memUsageList,
                      List<SqlWaitStateLive> sqlWaitStateLiveList,
                      List<Session> sessionList) {
        this.cpuUsage = cpuUsage;
        this.memUsageList = memUsageList;
        this.sqlWaitStateLiveList = sqlWaitStateLiveList;
        this.sessionList = sessionList;
    }

    public CpuUsage getCpuUsage() {
        return cpuUsage;
    }

    public List<MemUsage> getMemUsageList() {
        return memUsageList;
    }

    public List<SqlWaitStateLive> getSqlWaitStateLiveList() {
        return sqlWaitStateLiveList;
    }

    public List<Session> getSessionList() {
        return sessionList;
    }

    public static class CpuUsage {
        private List<Integer> ioList;
        private List<Integer> sysList;
        private List<Integer> userList;
        private List<Integer> idleList;

        public CpuUsage(List<Integer> ioList, List<Integer> sysList,
                        List<Integer> userList, List<Integer> idleList) {
            this.ioList = ioList;
            this.sysList = sysList;
            this.userList = userList;
            this.idleList = idleList;
        }

        public List<Integer> getIoList() {
            return ioList;
        }

        public List<Integer> getSysList() {
            return sysList;
        }

        public List<Integer> getUserList() {
            return userList;
        }

        public List<Integer> getIdleList() {
            return idleList;
        }
    }

    public static class MemUsage {
        private String name;
        private Integer value;

        public MemUsage(String name, Integer value) {
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

    public static class SqlWaitStateLive {
        private String name;
        private Integer value;

        public SqlWaitStateLive(String name, Integer value) {
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

    public static class Session {
        private String id;
        private String user;
        private String host;
        private String db;
        private String cmd;
        private long time;
        private String state;
        private String info;

        public Session(String id, String user, String host,
                       String db, String cmd, long time,
                       String state, String info) {
            this.id = id;
            this.user = user;
            this.host = host;
            this.db = db;
            this.cmd = cmd;
            this.time = time;
            this.state = state;
            this.info = info;
        }

        public String getId() {
            return id;
        }

        public String getUser() {
            return user;
        }

        public String getHost() {
            return host;
        }

        public String getDb() {
            return db;
        }

        public String getCmd() {
            return cmd;
        }

        public void setTime(long time) {
            this.time = time;
        }

        public long getTime() {
            return time;
        }

        public String getState() {
            return state;
        }

        public String getInfo() {
            return info;
        }
    }

    public static class SessionComparator implements Comparator<Session> {

        public int compare(Session s1, Session s2) {
            if (!s1.getCmd().equals(s2.getCmd())) {
                return s1.getCmd().compareTo(s2.getCmd());
            } else {
                return Integer.valueOf(s1.getId()) - Integer.valueOf(s2.getId());
            }
        }
    }
}
