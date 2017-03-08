package com.zte.ums.oespaas.mysql.hbase;

import com.zte.ums.oespaas.mysql.bean.DBInfoBean;
import com.zte.ums.oespaas.mysql.bean.hbase.*;
import com.zte.ums.oespaas.mysql.util.NetUtils;
import com.zte.ums.oespaas.mysql.util.TimeUtils;
import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.client.ResultScanner;
import org.apache.hadoop.hbase.client.Scan;
import org.apache.hadoop.hbase.util.Bytes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MonitorInfoService {
    private static final Logger logger = LoggerFactory.getLogger(MonitorInfoService.class);

    public static List<MonitorInfo> getMonitorInfoList(String dbNeId, Range range) {
        List<MonitorInfo> monitorInfos = new ArrayList<MonitorInfo>();
        if (!NetUtils.isHBaseRunnig()) {
            return monitorInfos;
        }

        if (!HBaseCheck.isChecked){
            HBaseCheck.checkHTable();
        }

        long start = TimeUtils.reverseTimeMillis(range.getTo());
        long end = TimeUtils.reverseTimeMillis(range.getFrom());
        for (String rowkey : HBaseOperator.rowkeyMapMonitorInfo.keySet()){
            String neId = rowkey.substring(0, rowkey.indexOf("^"));
            long reverseTime = Long.valueOf(rowkey.substring(rowkey.indexOf("^") + 1));

            if (neId.equals(dbNeId) && reverseTime >= start && reverseTime < end) {
                monitorInfos.add(HBaseOperator.rowkeyMapMonitorInfo.get(rowkey));
            }
        }

        return monitorInfos;
    }

    public static List<MonitorInfo> getMonitorInfoListFromHBase(String dbNeId, Range range) {
        List<MonitorInfo> monitorInfos = new ArrayList<MonitorInfo>();
        if (!NetUtils.isHBaseRunnig()) {
            return monitorInfos;
        }

        if (!HBaseCheck.isChecked){
            HBaseCheck.checkHTable();
        }

        try {
            if (HBaseOperator.hTableMonitor != null) {
                Scan scan = createScan(dbNeId, range);
                ResultScanner rs = HBaseOperator.hTableMonitor.getScanner(scan);
                for (Result result : rs) {
                    MonitorInfo monitorInfo;
                    try {
                        monitorInfo = resolveResult(result);
                    } catch (Exception e){
                        continue;
                    }

                    monitorInfos.add(monitorInfo);
                }
            }
        } catch (IOException e) {
            logger.error(e.getMessage());
        }

        return monitorInfos;
    }

    private static Scan createScan(String dbNeId, Range range) {
        Scan scan = new Scan();
        byte[] startRowKey = HBaseOperator.generateRowKey(dbNeId, range.getTo());
        byte[] stopRowKey = HBaseOperator.generateRowKey(dbNeId, range.getFrom());
        scan.setStartRow(startRowKey);
        scan.setStopRow(stopRowKey);
        return scan;
    }

    public static MonitorInfo resolveResult(Result result){
        String rowKey = Bytes.toString(result.getRow());
        String dbNeId = rowKey.substring(0, rowKey.indexOf("^"));
        long reverseTime = Long.valueOf(rowKey.substring(rowKey.indexOf("^") + 1));
        String collectTime = String.valueOf(TimeUtils.reverseTimeMillis(reverseTime));

        MonitorInfo monitorInfo = new MonitorInfo();
        monitorInfo.setDbNeId(dbNeId);
        monitorInfo.setCollectTime(collectTime);

        byte[] sessionBytes = result.getValue(HBaseConstant.MONITOR_FAMILYNAME, HBaseConstant.MONITOR_SESSION_COLUMNNAME);
        byte[] slowLogBytes = result.getValue(HBaseConstant.MONITOR_FAMILYNAME, HBaseConstant.MONITOR_SLOWLOG_COLUMNNAME);
        byte[] generalBytes = result.getValue(HBaseConstant.MONITOR_FAMILYNAME, HBaseConstant.MONITOR_GENERALLOG_COLUMNNAME);
        byte[] statusBytes = result.getValue(HBaseConstant.MONITOR_FAMILYNAME, HBaseConstant.MONITOR_STATUS_COLUMNNAME);
        byte[] userBytes = result.getValue(HBaseConstant.MONITOR_FAMILYNAME, HBaseConstant.MONITOR_USERS_COLUMNNAME);
        byte[] dbinfoBytes = result.getValue(HBaseConstant.MONITOR_FAMILYNAME, HBaseConstant.MONITOR_DBINFO_COLUMNNAME);

        if (null != sessionBytes) {
            SessionBean sessionBean = new SessionBean();
            sessionBean.readValue(sessionBytes);
            monitorInfo.setSessionBean(sessionBean);
        }
        if (null != slowLogBytes) {
            SlowLogBean slowLogBean = new SlowLogBean();
            slowLogBean.readValue(slowLogBytes);
            monitorInfo.setSlowLogBean(slowLogBean);
        }
        if (null != generalBytes) {
            GeneralLogBean generalLogBean = new GeneralLogBean();
            generalLogBean.readValue(generalBytes);
            monitorInfo.setGeneralLogBean(generalLogBean);
        }
        if (null != statusBytes) {
            StatusBean statusBean = new StatusBean();
            statusBean.readValue(statusBytes);
            monitorInfo.setStatusBean(statusBean);
        }
        if (null != userBytes) {
            UserBean userBean = new UserBean();
            userBean.readValue(userBytes);
            monitorInfo.setUserBean(userBean);
        }
        if (null != dbinfoBytes) {
            DBInfoBean dbInfoBean = new DBInfoBean();
            dbInfoBean.readValue(dbinfoBytes);
            monitorInfo.setDbInfoBean(dbInfoBean);
        }

        return monitorInfo;
    }
}
