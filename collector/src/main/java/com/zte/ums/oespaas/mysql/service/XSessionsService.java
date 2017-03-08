package com.zte.ums.oespaas.mysql.service;

import com.zte.ums.oespaas.mysql.bean.Session;
import com.zte.ums.oespaas.mysql.bean.Sessions;
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
public class XSessionsService {
    private static final Logger logger = LoggerFactory.getLogger(XSessionsService.class);

    public Sessions getTopNSessions(String dbNeId, Range range, int topN) {
        if (!HBaseCheck.isChecked){
            HBaseCheck.checkHTable();
        }

        if (! HBaseOperator.dbNeIdList.contains(dbNeId)){
            return null;
        }

        Map<String, Long> idMapTime = new HashMap<String, Long>();
        Map<String, Integer> idMapNum = new HashMap<String, Integer>();

        List<MonitorInfo> monitorInfoList = MonitorInfoService.getMonitorInfoList(dbNeId, range);
        for (MonitorInfo monitorInfo : monitorInfoList) {
            try {
                SessionBean sessionBean = monitorInfo.getSessionBean();
                if (sessionBean != null) {
                    String id = "";
                    if (sessionBean.getOid() != null) {
                        id = sessionBean.getOid();
                    }

                    long time = 0L;
                    if (sessionBean.getSpeedTime() != null && !sessionBean.getSpeedTime().isEmpty()) {
                        time = Long.valueOf(sessionBean.getSpeedTime());
                    }

                    if (!id.isEmpty()) {
                        if (idMapTime.containsKey(id)) {
                            idMapTime.put(id, idMapTime.get(id) + time);
                        } else {
                            idMapTime.put(id, time);
                        }

                        if (idMapNum.containsKey(id)) {
                            idMapNum.put(id, idMapNum.get(id) + 1);
                        } else {
                            idMapNum.put(id, 1);
                        }
                    }
                }
            } catch (Exception e) {
                logger.error(e.getMessage());
            }
        }

        for (String id : idMapNum.keySet()) {
            if (idMapNum.get(id) > 1) {
                long time = idMapTime.get(id) / idMapNum.get(id);
                idMapTime.put(id, time);
            }
        }

        List<Map.Entry<?, ?>> topNList = MapUtils.getTopNSortedByMapValueDescend(idMapTime, topN);
        Long totalTime = 0L;
        for (Map.Entry<?, ?> entry : topNList) {
            totalTime += (Long) entry.getValue();
        }

        int otherTotalWeight = 0;
        List<Session> sessionList = new ArrayList<Session>();

        for (Map.Entry<?, ?> entry : topNList) {
            String sessionId = (String) entry.getKey();
            Long timeSpent = (Long) entry.getValue();
            String spent = TimeConvert.getTimeStringFromSecond(timeSpent);
            String weight = "0";

            if (totalTime != 0L) {
                if (entry != topNList.get(topNList.size() - 1)) {
                    otherTotalWeight += Math.round(timeSpent.intValue() * 100 / totalTime.intValue());
                    weight = String.format("%d", Math.round(timeSpent.intValue() * 100 / totalTime.intValue()));
                } else {
                    weight = String.format("%d", 100 - otherTotalWeight);
                }
            }

            sessionList.add(new Session(sessionId, spent, weight));
        }

        return new Sessions(sessionList);
    }
}
