package com.zte.ums.oespaas.mysql.service;

import com.zte.ums.oespaas.mysql.bean.Reports;
import com.zte.ums.oespaas.mysql.bean.hbase.MonitorInfo;
import com.zte.ums.oespaas.mysql.bean.hbase.Range;
import com.zte.ums.oespaas.mysql.bean.hbase.SessionBean;
import com.zte.ums.oespaas.mysql.bean.hbase.SlowLogBean;
import com.zte.ums.oespaas.mysql.hbase.HBaseCheck;
import com.zte.ums.oespaas.mysql.hbase.HBaseOperator;
import com.zte.ums.oespaas.mysql.hbase.MonitorInfoService;
import com.zte.ums.oespaas.mysql.util.MapUtils;
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
 * Created by root on 9/8/16.
 */
@Service
public class XReportsService {
    private static final Logger logger = LoggerFactory.getLogger(XReportsService.class);

    @Autowired
    private XDBInfoDashboardService xdbInfoDashboardService;

    private final int TOPN = 5;

    private Reports.WaitStateChart waitStateChart;
    private List<Reports.WaitStateTable> waitStateTableList;
    private Reports.TopActivityChart topActivityChart;
    private List<Reports.TopActivityTable> topActivityTableList;

    public Reports getReports(String dbNeId, Range range) {
        if (!HBaseCheck.isChecked){
            HBaseCheck.checkHTable();
        }

        if (! HBaseOperator.dbNeIdList.contains(dbNeId)){
            return null;
        }

        final int SUITABLE_COUNT = xdbInfoDashboardService.getSuitableRangeCountByCheckSecond(range);
        long halfInterval = (SUITABLE_COUNT == 0) ? 0 : (range.getTo() - range.getFrom()) / (2 * SUITABLE_COUNT);
        List<Range> timeRanges = range.splitRangeForTimeRanges(SUITABLE_COUNT, halfInterval);
        List<String> timePoints = xdbInfoDashboardService.convertTimestampListToTimeStringList(range, range.splitRangeForTimePoints(SUITABLE_COUNT));

        List<MonitorInfo> monitorInfoList = MonitorInfoService.getMonitorInfoList(dbNeId, range);

        setWaitStateChartAndWaitStateTableList(monitorInfoList, timeRanges, timePoints);
        setTopActivityChartAndTopActivityTableList(monitorInfoList, timeRanges, timePoints);

        return new Reports(waitStateChart, waitStateTableList, topActivityChart, topActivityTableList);
    }

    private void setWaitStateChartAndWaitStateTableList(List<MonitorInfo> monitorInfoList, List<Range> timeRanges, List<String> timePoints) {
        waitStateChart = new Reports.WaitStateChart(new ArrayList<String>(),
                new ArrayList<String>(), new ArrayList<List<Long>>());
        waitStateTableList = new ArrayList<Reports.WaitStateTable>();

        Map<String, Integer> stateNum = new HashMap<String, Integer>();
        Map<String, List<Long>> stateMapTimeSpent = new HashMap<String, List<Long>>();

        for (MonitorInfo monitorInfo : monitorInfoList) {
            try {
                SessionBean sessionBean = monitorInfo.getSessionBean();
                if (sessionBean != null) {
                    String state = sessionBean.getState();
                    if (!state.isEmpty()) {
                        if (stateNum.containsKey(state)) {
                            stateNum.put(state, stateNum.get(state) + 1);
                        } else {
                            stateNum.put(state, 1);

                            stateMapTimeSpent.put(state, xdbInfoDashboardService.initListBySize(0L, timePoints.size()));
                        }

                        List<Long> timeSpentList = stateMapTimeSpent.get(state);
                        int index = xdbInfoDashboardService.getTimeIndexInTimeRanges(timeRanges, Long.valueOf(sessionBean.getCollectTime()));
                        if (index != -1) {
                            if (sessionBean.getSpeedTime() != null && !sessionBean.getSpeedTime().isEmpty()) {
                                long timeSpent = TimeConvert.getSecondFromTimeString(sessionBean.getSpeedTime());
                                if (timeSpent != -1L) {
                                    timeSpentList.set(index, timeSpentList.get(index) + timeSpent);
                                }
                            }
                        }
                        stateMapTimeSpent.put(state, timeSpentList);
                    }
                }
            } catch (Exception e) {
                logger.error(e.getMessage());
            }
        }

        if (stateNum.size() != 0) {
            List<String> legends = new ArrayList<String>();
            List<Map.Entry<?, ?>> topNList = MapUtils.getTopNSortedByMapValueDescend(stateNum, TOPN);
            for (Map.Entry<?, ?> entry : topNList) {
                legends.add((String) entry.getKey());
            }

            List<List<Long>> allDatas = new ArrayList<List<Long>>();
            for (String state : legends) {
                List<Long> data = stateMapTimeSpent.get(state);
                allDatas.add(data);

                long totalTime = 0L;
                for (Long time : data) {
                    totalTime += time;
                }

                waitStateTableList.add(new Reports.WaitStateTable(state, "description defined later",
                        TimeConvert.getTimeStringFromSecond(totalTime)));
            }

            waitStateChart = new Reports.WaitStateChart(legends, timePoints, allDatas);
        }
    }

    private void setTopActivityChartAndTopActivityTableList(List<MonitorInfo> monitorInfoList, List<Range> timeRanges, List<String> timePoints) {
        topActivityChart = new Reports.TopActivityChart(new ArrayList<String>(),
                new ArrayList<String>(), new ArrayList<List<Long>>());
        topActivityTableList = new ArrayList<Reports.TopActivityTable>();

        //queryId is not defined, so the key now is set to sqlText
        Map<String, Integer> sqlNum = new HashMap<String, Integer>();
        Map<String, List<Long>> sqlMapTimeSpent = new HashMap<String, List<Long>>();

        for (MonitorInfo monitorInfo : monitorInfoList) {
            try {
                SlowLogBean slowLogBean = monitorInfo.getSlowLogBean();
                if (slowLogBean != null) {
                    String sql = slowLogBean.getSqlText();
                    if (!sql.isEmpty()) {
                        if (sqlNum.containsKey(sql)) {
                            sqlNum.put(sql, sqlNum.get(sql) + 1);
                        } else {
                            sqlNum.put(sql, 1);

                            sqlMapTimeSpent.put(sql, xdbInfoDashboardService.initListBySize(0L, timePoints.size()));
                        }

                        List<Long> timeSpentList = sqlMapTimeSpent.get(sql);
                        int index = xdbInfoDashboardService.getTimeIndexInTimeRanges(timeRanges, Long.valueOf(slowLogBean.getCollectTime()));
                        if (index != -1) {
                            if (slowLogBean.getQueryTime() != null && !slowLogBean.getQueryTime().isEmpty()) {
                                long timeSpent = TimeConvert.getSecondFromTimeString(slowLogBean.getQueryTime());
                                if (timeSpent != -1L) {
                                    timeSpentList.set(index, timeSpentList.get(index) + timeSpent);
                                }
                            }
                        }
                        sqlMapTimeSpent.put(sql, timeSpentList);
                    }
                }
            } catch (Exception e){
                logger.error(e.getMessage());
            }
        }

        if (sqlNum.size() != 0) {
            List<String> legends = new ArrayList<String>();
            List<String> topNSqls = new ArrayList<String>();
            List<Map.Entry<?, ?>> topNList = MapUtils.getTopNSortedByMapValueDescend(sqlNum, TOPN);
            for (Map.Entry<?, ?> entry : topNList) {
                legends.add("queryId defined later");
                topNSqls.add((String) entry.getKey());
            }

            List<List<Long>> allDatas = new ArrayList<List<Long>>();
            for (String sql : topNSqls) {
                List<Long> data = sqlMapTimeSpent.get(sql);
                allDatas.add(data);

                long totalTime = 0L;
                for (Long time : data) {
                    totalTime += time;
                }

                topActivityTableList.add(new Reports.TopActivityTable("queryId defined later", sql,
                        TimeConvert.getTimeStringFromSecond(totalTime)));
            }

            topActivityChart = new Reports.TopActivityChart(legends, timePoints, allDatas);
        }
    }
}
