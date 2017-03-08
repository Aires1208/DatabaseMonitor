package com.zte.ums.oespaas.mysql.hbase;

import com.zte.ums.oespaas.mysql.bean.DBInfoBean;
import com.zte.ums.oespaas.mysql.bean.hbase.*;
import com.zte.ums.oespaas.mysql.util.NetUtils;
import org.apache.hadoop.hbase.client.Put;
import org.apache.hadoop.hbase.util.Bytes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Set;

/**
 * Created by 10183966 on 8/26/16.
 */
public class MonitorInfoHandler {
    private static final Logger logger = LoggerFactory.getLogger(MonitorInfoHandler.class);

    public static boolean insert(AbstractBean bean) {
        if (bean == null) {
            return false;
        }

        if (!NetUtils.isHBaseRunnig()) {
            return false;
        }

        if (!HBaseCheck.isChecked){
            HBaseCheck.checkHTable();
        }

        if (!HBaseOperator.dbNeIdList.contains(bean.getNeId())){
            return false;
        }

        try {
            if (HBaseOperator.hTableMonitor != null) {
                byte[] rowKey = HBaseOperator.generateRowKey(bean.getNeId(), bean.getCollectTime());
                Put put = new Put(rowKey);

                byte[] monitorValue = bean.writeValue();
                if (bean instanceof SessionBean) {
                    put.addColumn(HBaseConstant.MONITOR_FAMILYNAME, HBaseConstant.MONITOR_SESSION_COLUMNNAME, monitorValue);
                } else if (bean instanceof SlowLogBean) {
                    put.addColumn(HBaseConstant.MONITOR_FAMILYNAME, HBaseConstant.MONITOR_SLOWLOG_COLUMNNAME, monitorValue);
                } else if (bean instanceof GeneralLogBean) {
                    put.addColumn(HBaseConstant.MONITOR_FAMILYNAME, HBaseConstant.MONITOR_GENERALLOG_COLUMNNAME, monitorValue);
                } else if (bean instanceof StatusBean) {
                    put.addColumn(HBaseConstant.MONITOR_FAMILYNAME, HBaseConstant.MONITOR_STATUS_COLUMNNAME, monitorValue);
                } else if (bean instanceof UserBean) {
                    put.addColumn(HBaseConstant.MONITOR_FAMILYNAME, HBaseConstant.MONITOR_USERS_COLUMNNAME, monitorValue);
                } else if (bean instanceof DBInfoBean) {
                    put.addColumn(HBaseConstant.MONITOR_FAMILYNAME, HBaseConstant.MONITOR_DBINFO_COLUMNNAME, monitorValue);
                }

                HBaseOperator.hTableMonitor.put(put);
                addToHBaseObj(bean);
                return true;
            }
        } catch (Exception e) {
            logger.error(e.getMessage());
        }

        return false;
    }

    public static void delete(String dbNeId) {
        if (!NetUtils.isHBaseRunnig()) {
            return;
        }

        if (!HBaseCheck.isChecked){
            HBaseCheck.checkHTable();
        }

        Set<String> rowkeys = HBaseOperator.rowkeyMapMonitorInfo.keySet();
        for (String rowkey: rowkeys){
            String neId = rowkey.substring(0, rowkey.indexOf("^"));
            if (neId.equals(dbNeId)){
                HBaseOperator.deleteRow(HBaseOperator.hTableMonitor, rowkey);
                HBaseOperator.rowkeyMapMonitorInfo.remove(rowkey);
            }
        }
    }

    private static void addToHBaseObj(AbstractBean bean){
        String rowkey = Bytes.toString(HBaseOperator.generateRowKey(bean.getNeId(), bean.getCollectTime()));
        MonitorInfo monitorInfo = new MonitorInfo();
        monitorInfo.setDbNeId(bean.getNeId());
        monitorInfo.setCollectTime(bean.getCollectTime());

        if (bean instanceof SessionBean) {
            if (HBaseOperator.rowkeyMapMonitorInfo.containsKey(rowkey)){
                HBaseOperator.rowkeyMapMonitorInfo.get(rowkey).setSessionBean((SessionBean) bean);
            } else {
                monitorInfo.setSessionBean((SessionBean) bean);
                HBaseOperator.rowkeyMapMonitorInfo.put(rowkey, monitorInfo);
            }
        } else if (bean instanceof SlowLogBean) {
            if (HBaseOperator.rowkeyMapMonitorInfo.containsKey(rowkey)){
                HBaseOperator.rowkeyMapMonitorInfo.get(rowkey).setSlowLogBean((SlowLogBean) bean);
            } else {
                monitorInfo.setSlowLogBean((SlowLogBean) bean);
                HBaseOperator.rowkeyMapMonitorInfo.put(rowkey, monitorInfo);
            }
        } else if (bean instanceof GeneralLogBean) {
            if (HBaseOperator.rowkeyMapMonitorInfo.containsKey(rowkey)){
                HBaseOperator.rowkeyMapMonitorInfo.get(rowkey).setGeneralLogBean((GeneralLogBean) bean);
            } else {
                monitorInfo.setGeneralLogBean((GeneralLogBean) bean);
                HBaseOperator.rowkeyMapMonitorInfo.put(rowkey, monitorInfo);
            }
        } else if (bean instanceof StatusBean) {
            if (HBaseOperator.rowkeyMapMonitorInfo.containsKey(rowkey)){
                HBaseOperator.rowkeyMapMonitorInfo.get(rowkey).setStatusBean((StatusBean) bean);
            } else {
                monitorInfo.setStatusBean((StatusBean) bean);
                HBaseOperator.rowkeyMapMonitorInfo.put(rowkey, monitorInfo);
            }
        } else if (bean instanceof UserBean) {
            if (HBaseOperator.rowkeyMapMonitorInfo.containsKey(rowkey)){
                HBaseOperator.rowkeyMapMonitorInfo.get(rowkey).setUserBean((UserBean) bean);
            } else {
                monitorInfo.setUserBean((UserBean) bean);
                HBaseOperator.rowkeyMapMonitorInfo.put(rowkey, monitorInfo);
            }
        } else if (bean instanceof DBInfoBean) {
            if (HBaseOperator.rowkeyMapMonitorInfo.containsKey(rowkey)){
                HBaseOperator.rowkeyMapMonitorInfo.get(rowkey).setDbInfoBean((DBInfoBean) bean);
            } else {
                monitorInfo.setDbInfoBean((DBInfoBean) bean);
                HBaseOperator.rowkeyMapMonitorInfo.put(rowkey, monitorInfo);
            }
        }
    }
}
