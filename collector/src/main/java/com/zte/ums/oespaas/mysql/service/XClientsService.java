package com.zte.ums.oespaas.mysql.service;

import com.zte.ums.oespaas.mysql.bean.Client;
import com.zte.ums.oespaas.mysql.bean.Clients;
import com.zte.ums.oespaas.mysql.bean.hbase.MonitorInfo;
import com.zte.ums.oespaas.mysql.bean.hbase.Range;
import com.zte.ums.oespaas.mysql.bean.hbase.UserBean;
import com.zte.ums.oespaas.mysql.hbase.HBaseCheck;
import com.zte.ums.oespaas.mysql.hbase.HBaseOperator;
import com.zte.ums.oespaas.mysql.hbase.MonitorInfoService;
import com.zte.ums.oespaas.mysql.util.MapUtils;
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
public class XClientsService {
    private static final Logger logger = LoggerFactory.getLogger(XClientsService.class);

    public Clients getTopNClients(String dbNeId, Range range, int topN) {
        if (!HBaseCheck.isChecked){
            HBaseCheck.checkHTable();
        }

        if (! HBaseOperator.dbNeIdList.contains(dbNeId)){
            return null;
        }

        Map<String, Integer> hostNameWithNum = new HashMap<String, Integer>();
        List<MonitorInfo> monitorInfoList = MonitorInfoService.getMonitorInfoList(dbNeId, range);
        for (MonitorInfo monitorInfo : monitorInfoList) {
            try {
                UserBean userBean = monitorInfo.getUserBean();
                if (userBean != null) {
                    String hostName = userBean.getHostName();
                    if (hostName != null && !hostName.isEmpty() && !isExcludedName(hostName)) {
                        if (hostNameWithNum.containsKey(hostName)) {
                            hostNameWithNum.put(hostName, hostNameWithNum.get(hostName) + 1);
                        } else {
                            hostNameWithNum.put(hostName, 1);
                        }
                    }
                }
            } catch (Exception e){
                logger.error(e.getMessage());
            }
        }

        List<Map.Entry<?, ?>> topNList = MapUtils.getTopNSortedByMapValueDescend(hostNameWithNum, topN);
        int totalNum = 0;
        for (Map.Entry<?, ?> entry : topNList) {
            totalNum += (Integer) entry.getValue();
        }

        int otherTotalWeight = 0;
        List<Client> clientList = new ArrayList<Client>();
        for (Map.Entry<?, ?> entry : topNList) {
            String hostName = (String) entry.getKey();
            Integer count = (Integer) entry.getValue();
            String weight = "0";

            if (totalNum != 0) {
                if (entry != topNList.get(topNList.size() - 1)) {
                    otherTotalWeight += Math.round(count * 100 / totalNum);
                    weight = String.format("%d", Math.round(count * 100 / totalNum));
                } else {
                    weight = String.format("%d", 100 - otherTotalWeight);
                }
            }

            clientList.add(new Client(hostName, count.toString(), weight));
        }

        return new Clients(clientList);
    }

    private boolean isExcludedName(String name) {
        return name.equals("::1") || name.equals("%");
    }
}
