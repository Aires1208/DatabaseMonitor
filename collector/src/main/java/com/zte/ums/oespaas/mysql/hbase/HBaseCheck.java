package com.zte.ums.oespaas.mysql.hbase;

import com.zte.ums.oespaas.mysql.bean.hbase.MonitorInfo;
import com.zte.ums.oespaas.mysql.bean.hbase.OSInfo;
import com.zte.ums.oespaas.mysql.bean.hbase.RegisterInfo;
import com.zte.ums.oespaas.mysql.util.NetUtils;
import com.zte.ums.oespaas.mysql.util.TimeUtils;
import org.apache.hadoop.hbase.HColumnDescriptor;
import org.apache.hadoop.hbase.HTableDescriptor;
import org.apache.hadoop.hbase.client.*;
import org.apache.hadoop.hbase.util.Bytes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by 10183966 on 8/22/16.
 */
public class HBaseCheck {
    private static Logger logger = LoggerFactory.getLogger(HBaseCheck.class);

    public static boolean isChecked = false;
    public static boolean isTaskInit = false;

    public static void checkHTable() {
        if (!NetUtils.isHBaseRunnig()) {
            return;
        }

        try {
            HBaseOperator.hBaseAdmin = new HBaseAdmin(HBaseOperator.hbaseConf);
            HBaseOperator.hTableMonitor = new HTable(HBaseOperator.hbaseConf, HBaseConstant.MONITOR_TABLENAME);
            HBaseOperator.hTableOS = new HTable(HBaseOperator.hbaseConf, HBaseConstant.OS_TABLENAME);
            HBaseOperator.hTableRegister = new HTable(HBaseOperator.hbaseConf, HBaseConstant.REGISTER_TABLENAME);
        } catch (IOException e) {
            logger.error(e.getMessage());
        }

        createTable(HBaseConstant.REGISTER_TABLENAME, HBaseConstant.REGISTER_FAMILYNAME);
        createTable(HBaseConstant.MONITOR_TABLENAME, HBaseConstant.MONITOR_FAMILYNAME);
        createTable(HBaseConstant.OS_TABLENAME, HBaseConstant.OS_FAMILYNAME);

        //should run before checkMonitorData and checkOSData
        checkRegisterData();
        checkMonitorData();
        checkOSData();

        isChecked = true;
        isTaskInit = true;
    }

    private static boolean createTable(byte[] tableName, byte[] columnFamilyName){
        try {
            if (!HBaseOperator.hBaseAdmin.tableExists(tableName)) {
                HTableDescriptor hTableDescriptor = new HTableDescriptor(tableName);
                hTableDescriptor.addFamily(new HColumnDescriptor(columnFamilyName));
                HBaseOperator.hBaseAdmin.createTable(hTableDescriptor);

                return true;
            }
        } catch (IOException e) {
            logger.error(e.getMessage());
        }

        return false;
    }

    private static void checkRegisterData() {
        try {
            if (HBaseOperator.hTableRegister != null) {
                Set<RegisterInfo> registerInfoSet = new HashSet<RegisterInfo>();

                Scan scan = new Scan();
                ResultScanner rs = HBaseOperator.hTableRegister.getScanner(scan);
                if (rs != null) {
                    for (Result r : rs) {
                        String rowKey = Bytes.toString(r.getRow());
                        long reverseTime = Long.valueOf(rowKey.substring(rowKey.indexOf("^") + 1));
                        String collectTime = String.valueOf(TimeUtils.reverseTimeMillis(reverseTime));

                        byte[] registerBytes = r.getValue(HBaseConstant.REGISTER_FAMILYNAME, HBaseConstant.REGISTER_INPUTPARA_COLUMNNAME);
                        RegisterInfo registerInfo = new RegisterInfo();
                        registerInfo.setCollectTime(collectTime);

                        try {
                            registerInfo.readValue(registerBytes);
                        } catch (Exception e){
                            HBaseOperator.deleteRow(HBaseOperator.hTableRegister, rowKey);
                            continue;
                        }

                        if (registerInfoSet.contains(registerInfo)){
                            HBaseOperator.deleteRow(HBaseOperator.hTableRegister, rowKey);
                            continue;
                        } else {
                            registerInfoSet.add(registerInfo);
                        }

                        String osNeId = registerInfo.getOsNeId();
                        String dbNeId = registerInfo.getDbNeId();
                        HBaseOperator.osNeIdList.add(osNeId);
                        HBaseOperator.dbNeIdList.add(dbNeId);
                        HBaseOperator.dbNeIdMapOSNeId.put(dbNeId, osNeId);
                        HBaseOperator.dbNeIdMapRegisterInfo.put(dbNeId, registerInfo);

                        TaskService.createTask(registerInfo);

                        logger.info(registerInfo.toString());
                    }
                }
            }
        } catch (IOException e) {
            logger.error(e.getMessage());
        }
    }

    private static void checkMonitorData() {
        try {
            if (HBaseOperator.hTableMonitor != null) {
                Scan scan = new Scan();
                ResultScanner rs = HBaseOperator.hTableMonitor.getScanner(scan);
                if (rs != null) {
                    for (Result r : rs) {
                        String rowKey = Bytes.toString(r.getRow());
                        String dbNeId = rowKey.substring(0, rowKey.indexOf("^"));
                        long reverseTime = Long.valueOf(rowKey.substring(rowKey.indexOf("^") + 1));
                        String collectTime = String.valueOf(TimeUtils.reverseTimeMillis(reverseTime));

                        if (!HBaseOperator.dbNeIdList.contains(dbNeId)){
                            //HBaseOperator.deleteRow(HBaseOperator.hTableMonitor, rowKey);
                            continue;
                        }

                        MonitorInfo monitorInfo;
                        try {
                            monitorInfo = MonitorInfoService.resolveResult(r);
                        } catch (Exception e){
                            HBaseOperator.deleteRow(HBaseOperator.hTableMonitor, rowKey);
                            continue;
                        }

                        monitorInfo.setDbNeId(dbNeId);
                        monitorInfo.setCollectTime(collectTime);
                        HBaseOperator.rowkeyMapMonitorInfo.put(rowKey, monitorInfo);
                    }
                }
            }
        } catch (IOException e) {
            logger.error(e.getMessage());
        }
    }

    private static void checkOSData() {
        try {
            if (HBaseOperator.hTableOS != null) {
                Scan scan = new Scan();
                ResultScanner rs = HBaseOperator.hTableOS.getScanner(scan);
                if (rs != null) {
                    for (Result r : rs) {
                        String rowKey = Bytes.toString(r.getRow());
                        String osNeId = rowKey.substring(0, rowKey.indexOf("^"));
                        long reverseTime = Long.valueOf(rowKey.substring(rowKey.indexOf("^") + 1));
                        String collectTime = String.valueOf(TimeUtils.reverseTimeMillis(reverseTime));

                        if (!HBaseOperator.osNeIdList.contains(osNeId)){
                            //HBaseOperator.deleteRow(HBaseOperator.hTableOS, rowKey);
                            continue;
                        }

                        OSInfo osInfo;
                        try {
                            osInfo = OSInfoService.resolveResult(r);
                        } catch (Exception e){
                            HBaseOperator.deleteRow(HBaseOperator.hTableOS, rowKey);
                            continue;
                        }

                        osInfo.setOsNeId(osNeId);
                        osInfo.setCollectTime(collectTime);
                        HBaseOperator.rowkeyMapOSInfo.put(rowKey, osInfo);
                    }
                }
            }
        } catch (IOException e) {
            logger.error(e.getMessage());
        }
    }
}
