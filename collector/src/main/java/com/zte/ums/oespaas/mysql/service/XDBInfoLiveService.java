package com.zte.ums.oespaas.mysql.service;

import com.zte.ums.oespaas.mysql.bean.DBInfoLive;
import com.zte.ums.oespaas.mysql.bean.hbase.*;
import com.zte.ums.oespaas.mysql.hbase.HBaseCheck;
import com.zte.ums.oespaas.mysql.hbase.HBaseOperator;
import com.zte.ums.oespaas.mysql.hbase.MonitorInfoService;
import com.zte.ums.oespaas.mysql.hbase.OSInfoService;
import com.zte.ums.oespaas.mysql.util.MapUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.*;


/**
 * Created by root on 9/2/16.
 */

@Service
public class XDBInfoLiveService {
    private static final Logger logger = LoggerFactory.getLogger(XDBInfoLiveService.class);

    private final int TOPN = 10;

    public DBInfoLive getDBInfoLive(String dbNeId, Range range) {
        if (!HBaseCheck.isChecked){
            HBaseCheck.checkHTable();
        }

        if (! HBaseOperator.dbNeIdList.contains(dbNeId)){
            return null;
        }

        String osNeId = HBaseOperator.dbNeIdMapOSNeId.get(dbNeId);
        List<OSInfo> osInfoList = OSInfoService.getOSInfoList(osNeId, range);
        DBInfoLive.CpuUsage cpuUsage = getCpuUsage(osInfoList);
        List<DBInfoLive.MemUsage> memUsageList = getMemUsageList(osInfoList);

        List<MonitorInfo> monitorInfoList = MonitorInfoService.getMonitorInfoList(dbNeId, range);
        List<DBInfoLive.SqlWaitStateLive> sqlWaitStateLiveList = getSqlWaitStateLive(monitorInfoList);
        List<DBInfoLive.Session> sessionList = getSessionList(monitorInfoList);

        return new DBInfoLive(cpuUsage, memUsageList, sqlWaitStateLiveList, sessionList);
    }

    private DBInfoLive.CpuUsage getCpuUsage(List<OSInfo> osInfoList) {
        double ioUsage = 0.0;
        double sysUsage = 0.0;
        double userUsage = 0.0;
        int totalNum = 0;
        for (OSInfo osInfo : osInfoList) {
            try {
                CpuRatio cpuRatio = osInfo.getCpuRatio();
                if (cpuRatio != null) {
                    if (cpuRatio.getCpuIOWaitRatio() != null && !cpuRatio.getCpuIOWaitRatio().isEmpty()) {
                        ioUsage += Double.valueOf(cpuRatio.getCpuIOWaitRatio());
                    }

                    if (cpuRatio.getCpuSysRatio() != null && !cpuRatio.getCpuSysRatio().isEmpty()) {
                        sysUsage += Double.valueOf(cpuRatio.getCpuSysRatio());
                    }

                    if (cpuRatio.getCpuUserRatio() != null && !cpuRatio.getCpuUserRatio().isEmpty()) {
                        userUsage += Double.valueOf(cpuRatio.getCpuUserRatio());
                    }

                    totalNum++;
                }
            } catch (Exception e){
                logger.error(e.getMessage());
            }
        }

        List<Integer> ioList = new ArrayList<Integer>();
        List<Integer> sysList = new ArrayList<Integer>();
        List<Integer> userList = new ArrayList<Integer>();
        List<Integer> idleList = new ArrayList<Integer>();

        if (totalNum != 0) {
            int io = (int) Math.round(ioUsage / totalNum);
            ioList.add(io);

            int sys = (int) Math.round(sysUsage / totalNum);
            sysList.add(sys);

            int user = (int) Math.round(userUsage / totalNum);
            userList.add(user);

            Integer idle = 100 - io - sys - user;
            idleList.add(idle);
        } else {
            ioList.add(0);
            sysList.add(0);
            userList.add(0);
            idleList.add(0);
        }

        return new DBInfoLive.CpuUsage(ioList, sysList, userList, idleList);
    }

    private List<DBInfoLive.MemUsage> getMemUsageList(List<OSInfo> osInfoList) {
        List<DBInfoLive.MemUsage> memUsageList = new ArrayList<DBInfoLive.MemUsage>();
        if (osInfoList.size() == 0) {
            return memUsageList;
        }

        int totalNum = 0;
        Double totalUsed = 0.0;
        for (OSInfo osInfo : osInfoList) {
            try {
                MemoryRatio memoryRatio = osInfo.getMemoryRatio();
                if (memoryRatio != null) {
                    totalUsed += Double.valueOf(memoryRatio.getMemoryRatio());

                    totalNum++;
                }
            } catch (Exception e){
                logger.error(e.getMessage());
            }
        }

        if (totalNum != 0) {
            int used = (int) Math.round(totalUsed / totalNum);
            int unused = 100 - used;
            memUsageList.add(new DBInfoLive.MemUsage("Used", used));
            memUsageList.add(new DBInfoLive.MemUsage("Available", unused));
        }

        return memUsageList;
    }

    private List<DBInfoLive.SqlWaitStateLive> getSqlWaitStateLive(List<MonitorInfo> monitorInfoList) {
        Map<String, Integer> stateMap = new HashMap<String, Integer>();

        for (MonitorInfo monitorInfo : monitorInfoList) {
            try {
                SessionBean sessionBean = monitorInfo.getSessionBean();
                if (sessionBean != null) {
                    String state = sessionBean.getState();
                    if (!state.isEmpty()) {
                        if (stateMap.containsKey(state)) {
                            stateMap.put(state, stateMap.get(state) + 1);
                        } else {
                            stateMap.put(state, 1);
                        }
                    }
                }
            } catch (Exception e){
                logger.error(e.getMessage());
            }
        }

        List<Map.Entry<?, ?>> topNList = MapUtils.getTopNSortedByMapValueDescend(stateMap, TOPN);
        int totalNum = 0;
        for (Map.Entry<?, ?> entry : topNList) {
            totalNum += (Integer) entry.getValue();
        }

        int otherTotalValue = 0;
        List<DBInfoLive.SqlWaitStateLive> sqlWaitStateLive = new ArrayList<DBInfoLive.SqlWaitStateLive>();

        for (Map.Entry<?, ?> entry : topNList) {
            if (totalNum != 0) {
                String name = (String) entry.getKey();
                Integer value;

                if (entry != topNList.get(topNList.size() - 1)) {
                    value = Math.round((Integer) entry.getValue() * 100 / totalNum);
                    otherTotalValue += value;
                } else {
                    value = 100 - otherTotalValue;
                }

                sqlWaitStateLive.add(new DBInfoLive.SqlWaitStateLive(name, value));
            }
        }

        return sqlWaitStateLive;
    }

    private List<DBInfoLive.Session> getSessionList(List<MonitorInfo> monitorInfoList) {
        Map<String, DBInfoLive.Session> idMapSession = new HashMap<String, DBInfoLive.Session>();
        Map<String, Integer> idMapNum = new HashMap<String, Integer>();

        for (MonitorInfo monitorInfo : monitorInfoList) {
            try {
                SessionBean sessionBean = monitorInfo.getSessionBean();
                if (sessionBean != null) {
                    String id = "";
                    if (sessionBean.getOid() != null) {
                        id = sessionBean.getOid();
                    }

                    String user = "";
                    if (sessionBean.getUser() != null) {
                        user = sessionBean.getUser();
                    }

                    String host = "";
                    if (sessionBean.getHostName() != null) {
                        host = sessionBean.getHostName();
                    }

                    String db = "";
                    if (sessionBean.getDbname() != null) {
                        db = sessionBean.getDbname();
                    }

                    String cmd = "";
                    if (sessionBean.getCommand() != null) {
                        cmd = sessionBean.getCommand();
                    }

                    String state = "";
                    if (sessionBean.getState() != null) {
                        state = sessionBean.getState();
                    }

                    long time = 0L;
                    if (sessionBean.getSpeedTime() != null && !sessionBean.getSpeedTime().isEmpty()) {
                        time = Long.valueOf(sessionBean.getSpeedTime());
                    }

                    String info = "";
                    if (sessionBean.getSqlInfo() != null) {
                        info = sessionBean.getSqlInfo();
                    }

                    DBInfoLive.Session session = new DBInfoLive.Session(id, user, host, db,
                            cmd, time, state, info);
                    if (idMapSession.containsKey(id)) {
                        session.setTime(idMapSession.get(id).getTime() + time);
                        idMapSession.remove(id);
                    }
                    idMapSession.put(id, session);

                    if (idMapNum.containsKey(id)) {
                        idMapNum.put(id, idMapNum.get(id) + 1);
                    } else {
                        idMapNum.put(id, 1);
                    }
                }
            } catch (Exception e) {
                logger.error(e.getMessage());
            }
        }

        for (String id : idMapNum.keySet()) {
            if (idMapNum.get(id) > 1) {
                long time = idMapSession.get(id).getTime() / idMapNum.get(id);
                idMapSession.get(id).setTime(time);
            }
        }

        List<DBInfoLive.Session> sessionList = new ArrayList<DBInfoLive.Session>(idMapSession.values());
        Collections.sort(sessionList, new DBInfoLive.SessionComparator());

        return sessionList;
    }
}
