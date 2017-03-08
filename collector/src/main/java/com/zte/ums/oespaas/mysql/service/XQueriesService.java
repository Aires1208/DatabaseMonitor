package com.zte.ums.oespaas.mysql.service;

import com.zte.ums.oespaas.mysql.bean.Queries;
import com.zte.ums.oespaas.mysql.bean.Query;
import com.zte.ums.oespaas.mysql.bean.hbase.MonitorInfo;
import com.zte.ums.oespaas.mysql.bean.hbase.Range;
import com.zte.ums.oespaas.mysql.bean.hbase.SessionBean;
import com.zte.ums.oespaas.mysql.hbase.HBaseCheck;
import com.zte.ums.oespaas.mysql.hbase.HBaseOperator;
import com.zte.ums.oespaas.mysql.hbase.MonitorInfoService;
import com.zte.ums.oespaas.mysql.util.MapUtils;
import com.zte.ums.oespaas.mysql.util.TimeConvert;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by 10183966 on 8/18/16.
 */

@Service
public class XQueriesService {
    private static final Logger logger = LoggerFactory.getLogger(XQueriesService.class);

    public Queries getTopNQueries(String dbNeId, Range range, int topN) {
        if (!HBaseCheck.isChecked){
            HBaseCheck.checkHTable();
        }

        if (! HBaseOperator.dbNeIdList.contains(dbNeId)){
            return null;
        }

        Map<String, Long> sqlMapWithTime = new HashMap<String, Long>();

        List<MonitorInfo> monitorInfoList = MonitorInfoService.getMonitorInfoList(dbNeId, range);
        for (MonitorInfo monitorInfo : monitorInfoList) {
            try {
                SessionBean sessionBean = monitorInfo.getSessionBean();
                if (sessionBean != null) {
                    if (sessionBean.getSqlInfo() != null && !sessionBean.getSqlInfo().isEmpty()) {
                        String sqlInfo = sessionBean.getSqlInfo();

                        if (sessionBean.getSpeedTime() != null && !sessionBean.getSpeedTime().isEmpty()) {
                            long timeSpent = Long.valueOf(sessionBean.getSpeedTime());

                            if (sqlMapWithTime.containsKey(sqlInfo)) {
                                sqlMapWithTime.put(sqlInfo, sqlMapWithTime.get(sqlInfo) + timeSpent);
                            } else {
                                sqlMapWithTime.put(sqlInfo, timeSpent);
                            }
                        }
                    }
                }
            } catch (Exception e){
                logger.error(e.getMessage());
            }
        }

        List<Map.Entry<?, ?>> topNList = MapUtils.getTopNSortedByMapValueDescend(sqlMapWithTime, topN);
        long totalTimeSpent = 0L;
        for (Map.Entry<?, ?> entry : topNList) {
            totalTimeSpent += (Long) entry.getValue();
        }

        int otherTotalWeight = 0;
        List<Query> queryList = new ArrayList<Query>();

        for (Map.Entry<?, ?> entry : topNList) {
            String query = (String) entry.getKey();
            String timeSpent = TimeConvert.getTimeStringFromSecond((Long) entry.getValue());
            String weight = "0";

            if (totalTimeSpent != 0) {
                if (entry != topNList.get(topNList.size() - 1)) {
                    otherTotalWeight += Math.round(((Long) entry.getValue() * 100 / totalTimeSpent));
                    weight = String.format("%d", Math.round((Long) entry.getValue() * 100 / totalTimeSpent));
                } else {
                    weight = String.format("%d", 100 - otherTotalWeight);
                }
            }

            queryList.add(new Query(query, timeSpent, weight));
        }

        return new Queries(queryList);
    }
}
