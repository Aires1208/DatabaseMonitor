package com.zte.ums.oespaas.mysql.service;

import com.zte.ums.oespaas.mysql.bean.DBInfoDashboard;
import com.zte.ums.oespaas.mysql.bean.Db;
import com.zte.ums.oespaas.mysql.bean.DbBuilder;
import com.zte.ums.oespaas.mysql.bean.hbase.*;
import com.zte.ums.oespaas.mysql.hbase.HBaseCheck;
import com.zte.ums.oespaas.mysql.hbase.HBaseOperator;
import com.zte.ums.oespaas.mysql.hbase.MonitorInfoService;
import com.zte.ums.oespaas.mysql.hbase.OSInfoService;
import com.zte.ums.oespaas.mysql.util.MapUtils;
import com.zte.ums.oespaas.mysql.util.NetUtils;
import com.zte.ums.oespaas.mysql.util.TimeConvert;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by root on 9/2/16.
 */

@Service
public class XDBInfoDashboardService {
    private static final Logger logger = LoggerFactory.getLogger(XDBInfoDashboardService.class);

    @Autowired
    private XDBsService xdBsService;

    private final int TOPN = 10;
    private final int RANGE_COUNT = 10;

    public DBInfoDashboard getDBInfoDashboard(String dbNeId, Range range) {
        if (!HBaseCheck.isChecked){
            HBaseCheck.checkHTable();
        }

        if (! HBaseOperator.dbNeIdList.contains(dbNeId)){
            return null;
        }

        Db db = getDbsByDBNeId(dbNeId, range);
        DBInfoDashboard.Summary summary = new DBInfoDashboard.Summary(db.getDbName(),
                db.isHealth(), db.getType(), String.valueOf(db.getQueries()));

        final int SUITABLE_COUNT = getSuitableRangeCountByCheckSecond(range);
        long halfInterval = (SUITABLE_COUNT == 0) ? 0 : (range.getTo() - range.getFrom()) / (2 * SUITABLE_COUNT);
        List<Range> timeRanges = range.splitRangeForTimeRanges(SUITABLE_COUNT, halfInterval);
        List<String> timePoints = convertTimestampListToTimeStringList(range, range.splitRangeForTimePoints(SUITABLE_COUNT));

        List<MonitorInfo> monitorInfoList = MonitorInfoService.getMonitorInfoList(dbNeId, range);
        DBInfoDashboard.LoadTimeSpent loadTimeSpent = getLoadTimeSpent(monitorInfoList, timeRanges, timePoints, db);
        List<DBInfoDashboard.SqlWaitStates> sqlWaitStatesList = getSqlWaitStatesList(monitorInfoList);
        DBInfoDashboard.AvgConnect avgConnect = getAvgConnect(monitorInfoList, timeRanges, timePoints);

        String osNeId = HBaseOperator.dbNeIdMapOSNeId.get(dbNeId);
        List<OSInfo> osInfoList = OSInfoService.getOSInfoList(osNeId, range);
        DBInfoDashboard.CpuInfo cpuInfo = getCpuInfo(osInfoList, timeRanges, timePoints);
        DBInfoDashboard.MemInfo memInfo = getMemInfo(osInfoList, timeRanges, timePoints);
        DBInfoDashboard.DiskInfo diskInfo = getDiskInfo(osInfoList, timeRanges, timePoints);
        DBInfoDashboard.NetInfo netInfo = getNetInfo(osInfoList, timeRanges, timePoints);

        return new DBInfoDashboard(summary, loadTimeSpent, sqlWaitStatesList, avgConnect,
                cpuInfo, memInfo, diskInfo, netInfo);
    }

    private Db getDbsByDBNeId(String dbNeId, Range range) {
        if (!NetUtils.isHBaseRunnig()) {
            return new DbBuilder().DbName("").DbHost("").OsNeId("").DbNeId("").CollectTime("")
                    .Status("").DbType("").Health(false).Queries(0L).TimeSpent("0").Cpu("0").build();
        }

        return xdBsService.createDb(HBaseOperator.dbNeIdMapRegisterInfo.get(dbNeId), range);
    }

    private DBInfoDashboard.LoadTimeSpent getLoadTimeSpent(List<MonitorInfo> monitorInfoList, List<Range> timeRanges, List<String> timePoints, Db db) {
        List<Long> loads = initListBySize(0L, timePoints.size());
        List<Long> timeSpents = initListBySize(0L, timePoints.size());
        for (MonitorInfo monitorInfo : monitorInfoList) {
            try {
                StatusBean statusBean = monitorInfo.getStatusBean();
                if (statusBean != null) {
                    if (statusBean.getCom_select() != null && !statusBean.getCom_select().isEmpty()) {
                        Long load = Long.valueOf(statusBean.getCom_select());
                        int statusIndex = getTimeIndexInTimeRanges(timeRanges, Long.valueOf(statusBean.getCollectTime()));
                        if (statusIndex != -1) {
                            loads.set(statusIndex, loads.get(statusIndex) + load);
                        }
                    }
                }

                SessionBean sessionBean = monitorInfo.getSessionBean();
                if (sessionBean != null) {
                    if (sessionBean.getSpeedTime() != null && !sessionBean.getSpeedTime().isEmpty()) {
                        long timeSpent = TimeConvert.getSecondFromTimeString(sessionBean.getSpeedTime());
                        int sessionIndex = getTimeIndexInTimeRanges(timeRanges, Long.valueOf(sessionBean.getCollectTime()));
                        if (timeSpent != -1L && sessionIndex != -1) {
                            timeSpents.set(sessionIndex, timeSpents.get(sessionIndex) + timeSpent);
                        }
                    }
                }
            } catch (Exception e){
                logger.error(e.getMessage());
            }
        }

        long totalLoad = db.getQueries();
        String totalTimeSpent = db.getTimeSpent();
        String info = "Loads: " + totalLoad + "  Time Spent in Database: " + totalTimeSpent;

        return new DBInfoDashboard.LoadTimeSpent(info, timePoints, loads, timeSpents);
    }

    private List<DBInfoDashboard.SqlWaitStates> getSqlWaitStatesList(List<MonitorInfo> monitorInfoList) {
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
        List<DBInfoDashboard.SqlWaitStates> sqlWaitStatesList = new ArrayList<DBInfoDashboard.SqlWaitStates>();

        for (Map.Entry<?, ?> entry : topNList) {
            //not like with line-chart, if totalNum is 0, then do not add to list
            if (totalNum != 0) {
                String name = (String) entry.getKey();
                Integer value;

                if (entry != topNList.get(topNList.size() - 1)) {
                    value = Math.round((Integer) entry.getValue() * 100 / totalNum);
                    otherTotalValue += value;
                } else {
                    value = 100 - otherTotalValue;
                }
                sqlWaitStatesList.add(new DBInfoDashboard.SqlWaitStates(name, value));
            }
        }

        return sqlWaitStatesList;
    }

    private DBInfoDashboard.AvgConnect getAvgConnect(List<MonitorInfo> monitorInfoList, List<Range> timeRanges, List<String> timePoints) {
        List<Integer> connectNums = initListBySize(0, timePoints.size());
        int totalNum = 0;
        for (MonitorInfo monitorInfo : monitorInfoList) {
            try {
                StatusBean statusBean = monitorInfo.getStatusBean();
                if (statusBean != null) {
                    if (statusBean.getThreads_running() != null && !statusBean.getThreads_running().isEmpty()) {
                        int connectNum = Integer.valueOf(statusBean.getThreads_running());
                        int statusIndex = getTimeIndexInTimeRanges(timeRanges, Long.valueOf(statusBean.getCollectTime()));
                        if (statusIndex != -1) {
                            connectNums.set(statusIndex, connectNums.get(statusIndex) + connectNum);
                            totalNum += connectNum;
                        }
                    }
                }
            } catch (Exception e){
                logger.error(e.getMessage());
            }
        }

        return new DBInfoDashboard.AvgConnect("Avarage Connect: " + totalNum, timePoints, connectNums);
    }

    private DBInfoDashboard.CpuInfo getCpuInfo(List<OSInfo> osInfoList, List<Range> timeRanges, List<String> timePoints) {
        List<Double> sysList = initListBySize(0.0, timePoints.size());
        List<Double> userList = initListBySize(0.0, timePoints.size());
        Map<Integer, Integer> indexMapNum = new HashMap<Integer, Integer>();

        double totalSysUsage = 0.0, totalUserUsage = 0.0;
        int totalNum = 0;

        for (OSInfo osInfo : osInfoList) {
            try {
                CpuRatio cpuRatio = osInfo.getCpuRatio();
                if (cpuRatio != null) {
                    int index = getTimeIndexInTimeRanges(timeRanges, Long.valueOf(cpuRatio.getCollectTime()));
                    if (index != -1) {
                        if (cpuRatio.getCpuSysRatio() != null && !cpuRatio.getCpuSysRatio().isEmpty()) {
                            double sysUsage = Double.valueOf(cpuRatio.getCpuSysRatio());
                            sysList.set(index, sysList.get(index) + sysUsage);
                            totalSysUsage += sysUsage;
                        }

                        if (cpuRatio.getCpuUserRatio() != null && !cpuRatio.getCpuUserRatio().isEmpty()) {
                            Double userUsage = Double.valueOf(cpuRatio.getCpuUserRatio());
                            userList.set(index, userList.get(index) + userUsage);
                            totalUserUsage += userUsage;
                        }

                        if (indexMapNum.containsKey(index)) {
                            indexMapNum.put(index, indexMapNum.get(index) + 1);
                        } else {
                            indexMapNum.put(index, 1);
                        }

                        totalNum++;
                    }
                }
            } catch (Exception e){
                logger.error(e.getMessage());
            }
        }

        for (Integer index : indexMapNum.keySet()) {
            if (indexMapNum.get(index) > 1) {
                Double sysAvgValue = sysList.get(index) / indexMapNum.get(index);
                sysList.set(index, sysAvgValue);

                Double userAvgValue = userList.get(index) / indexMapNum.get(index);
                userList.set(index, userAvgValue);
            }

            sysList.set(index, Double.valueOf(String.format("%.2f", sysList.get(index))));
            userList.set(index, Double.valueOf(String.format("%.2f", userList.get(index))));
        }

        String info = "System: " + (int) (totalNum == 0 ? 0 : Math.round(totalSysUsage / totalNum))
                + "%  User: " + (int) (totalNum == 0 ? 0 : Math.round(totalUserUsage / totalNum)) + "%";

        return new DBInfoDashboard.CpuInfo(info, timePoints, sysList, userList);
    }

    private DBInfoDashboard.MemInfo getMemInfo(List<OSInfo> osInfoList, List<Range> timeRanges, List<String> timePoints) {
        List<Double> dataList = initListBySize(0.0, timePoints.size());
        Map<Integer, Integer> indexMapNum = new HashMap<Integer, Integer>();

        double totalMemUsage = 0.0;
        int totalNum = 0;

        for (OSInfo osInfo : osInfoList) {
            try {
                MemoryRatio memoryRatio = osInfo.getMemoryRatio();
                if (memoryRatio != null) {
                    int index = getTimeIndexInTimeRanges(timeRanges, Long.valueOf(memoryRatio.getCollectTime()));
                    if (index != -1) {
                        if (memoryRatio.getMemoryRatio() != null && !memoryRatio.getMemoryRatio().isEmpty()) {
                            double memUsage = Double.valueOf(memoryRatio.getMemoryRatio());
                            dataList.set(index, dataList.get(index) + memUsage);
                            totalMemUsage += memUsage;
                        }

                        if (indexMapNum.containsKey(index)) {
                            indexMapNum.put(index, indexMapNum.get(index) + 1);
                        } else {
                            indexMapNum.put(index, 1);
                        }

                        totalNum++;
                    }
                }
            } catch (Exception e){
                logger.error(e.getMessage());
            }
        }

        for (Integer index : indexMapNum.keySet()) {
            if (indexMapNum.get(index) > 1) {
                Double avgValue = dataList.get(index) / indexMapNum.get(index);
                dataList.set(index, avgValue);
            }

            dataList.set(index, Double.valueOf(String.format("%.2f", dataList.get(index))));
        }

        String info = "Used Memory: " + (int) (totalNum == 0 ? 0 : Math.round(totalMemUsage / totalNum)) + "%";

        return new DBInfoDashboard.MemInfo(info, timePoints, dataList);
    }

    private DBInfoDashboard.DiskInfo getDiskInfo(List<OSInfo> osInfoList, List<Range> timeRanges, List<String> timePoints) {
        List<Double> incomingList = initListBySize(0.0, timePoints.size());
        List<Double> outgoingList = initListBySize(0.0, timePoints.size());
        Map<Integer, Integer> indexMapNum = new HashMap<Integer, Integer>();

        double totaliIncoming = 0.0, totalOutgoing = 0.0;
        int totalNum = 0;

        for (OSInfo osInfo : osInfoList) {
            try {
                DiskIo diskIo = osInfo.getDiskIo();
                if (diskIo != null) {
                    int index = getTimeIndexInTimeRanges(timeRanges, Long.valueOf(diskIo.getCollectTime()));
                    if (index != -1) {
                        if (diskIo.getDiskReadBytes() != null && !diskIo.getDiskReadBytes().isEmpty()) {
                            double incoming = Double.valueOf(diskIo.getDiskReadBytes());
                            incomingList.set(index, incomingList.get(index) + incoming);
                            totaliIncoming += incoming;
                        }

                        if (diskIo.getDiskWriteBytes() != null && !diskIo.getDiskWriteBytes().isEmpty()) {
                            double outgoing = Double.valueOf(diskIo.getDiskWriteBytes());
                            outgoingList.set(index, outgoingList.get(index) + outgoing);
                            totalOutgoing += outgoing;
                        }

                        if (indexMapNum.containsKey(index)) {
                            indexMapNum.put(index, indexMapNum.get(index) + 1);
                        } else {
                            indexMapNum.put(index, 1);
                        }

                        totalNum++;
                    }
                }
            } catch (Exception e){
                logger.error(e.getMessage());
            }
        }

        for (Integer index : indexMapNum.keySet()) {
            if (indexMapNum.get(index) > 1) {
                Double incomingAvgValue = incomingList.get(index) / indexMapNum.get(index);
                incomingList.set(index, incomingAvgValue);

                Double outgoingAvgValue = outgoingList.get(index) / indexMapNum.get(index);
                outgoingList.set(index, outgoingAvgValue);
            }

            incomingList.set(index, Double.valueOf(String.format("%.2f", incomingList.get(index))));
            outgoingList.set(index, Double.valueOf(String.format("%.2f", outgoingList.get(index))));
        }

        String info = "Incoming: " + String.format("%.2f", totalNum == 0 ? 0.0 : Math.round(totaliIncoming / totalNum))
                + "KB/s  Outgoing: " + String.format("%.2f", totalNum == 0 ? 0.0 : Math.round(totalOutgoing / totalNum)) + "KB/s";

        return new DBInfoDashboard.DiskInfo(info, timePoints, incomingList, outgoingList);
    }

    private DBInfoDashboard.NetInfo getNetInfo(List<OSInfo> osInfoList, List<Range> timeRanges, List<String> timePoints) {
        List<Double> incomingList = initListBySize(0.0, timePoints.size());
        List<Double> outgoingList = initListBySize(0.0, timePoints.size());
        Map<Integer, Integer> indexMapNum = new HashMap<Integer, Integer>();

        double totaliIncoming = 0.0, totalOutgoing = 0.0;
        int totalNum = 0;

        for (OSInfo osInfo : osInfoList) {
            try {
                NetworkIo networkIo = osInfo.getNetworkIo();
                if (networkIo != null) {
                    int index = getTimeIndexInTimeRanges(timeRanges, Long.valueOf(networkIo.getCollectTime()));
                    if (index != -1) {
                        if (networkIo.getMySQLInTransrate() != null && !networkIo.getMySQLInTransrate().isEmpty()) {
                            double incoming = Double.valueOf(networkIo.getMySQLInTransrate());
                            incomingList.set(index, Double.valueOf(String.format("%.2f", (incomingList.get(index) + incoming) / 2)));
                            totaliIncoming += incoming;
                        }

                        if (networkIo.getMySQLOutTransrate() != null && !networkIo.getMySQLOutTransrate().isEmpty()) {
                            double outgoing = Double.valueOf(networkIo.getMySQLOutTransrate());
                            outgoingList.set(index, Double.valueOf(String.format("%.2f", (outgoingList.get(index) + outgoing) / 2)));
                            totalOutgoing += outgoing;
                        }

                        if (indexMapNum.containsKey(index)) {
                            indexMapNum.put(index, indexMapNum.get(index) + 1);
                        } else {
                            indexMapNum.put(index, 1);
                        }

                        totalNum++;
                    }
                }
            } catch (Exception e){
                logger.error(e.getMessage());
            }
        }

        for (Integer index : indexMapNum.keySet()) {
            if (indexMapNum.get(index) > 1) {
                Double incomingAvgValue = incomingList.get(index) / indexMapNum.get(index);
                incomingList.set(index, incomingAvgValue);

                Double outgoingAvgValue = outgoingList.get(index) / indexMapNum.get(index);
                outgoingList.set(index, outgoingAvgValue);
            }

            incomingList.set(index, Double.valueOf(String.format("%.2f", incomingList.get(index))));
            outgoingList.set(index, Double.valueOf(String.format("%.2f", outgoingList.get(index))));
        }

        String info = "Incoming: " + String.format("%.2f", totalNum == 0 ? 0.0 : Math.round(totaliIncoming / totalNum))
                + "KB/s  Outgoing: " + String.format("%.2f", totalNum == 0 ? 0.0 : Math.round(totalOutgoing / totalNum)) + "KB/s";

        return new DBInfoDashboard.NetInfo(info, timePoints, incomingList, outgoingList);
    }

    private long getIntervalSecondOfRange(Range range) {
        String tmpFrom = TimeConvert.getFullTimeStringFromTimestamp(range.getFrom());
        String tmpTo = TimeConvert.getFullTimeStringFromTimestamp(range.getTo());

        return Math.abs(TimeConvert.getSecondFromTimeString(tmpTo) - TimeConvert.getSecondFromTimeString(tmpFrom));
    }

    public int getSuitableRangeCountByCheckSecond(Range range) {
        long intervalSecond = getIntervalSecondOfRange(range);
        if (intervalSecond == 0) {
            return 0;
        } else {
            return (int) (Math.min(intervalSecond, RANGE_COUNT));
        }
    }

    public List<String> convertTimestampListToTimeStringList(Range range, List<Long> timestampList) {
        final long MAX_DAY_SECOND = 23 * 3600 + 59 * 60 + 59;

        List<String> times = new ArrayList<String>();
        for (Long timestamp : timestampList) {
            long intervalSecond = getIntervalSecondOfRange(range);
            if (intervalSecond < MAX_DAY_SECOND) {
                times.add(TimeConvert.getTimeStringFromTimestamp(timestamp));
            } else {
                times.add(TimeConvert.getFullTimeStringFromTimestamp(timestamp));
            }
        }

        return times;
    }

    public int getTimeIndexInTimeRanges(List<Range> timeRanges, long time) {
        if (timeRanges.size() == 0) {
            return -1;
        }

        for (int i = 0; i < timeRanges.size(); i++) {
            long from = timeRanges.get(i).getFrom();
            long to = timeRanges.get(i).getTo();
            if (time >= from && time < to) {
                return i;
            }
        }

        return -1;
    }

    public <T> List<T> initListBySize(T t, int size) {
        List<T> list = new ArrayList<T>();
        for (int i = 0; i < size; i++) {
            list.add(t);
        }

        return list;
    }
}
