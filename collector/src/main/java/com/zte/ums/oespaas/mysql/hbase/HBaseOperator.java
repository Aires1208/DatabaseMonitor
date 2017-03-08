package com.zte.ums.oespaas.mysql.hbase;

import com.zte.ums.oespaas.mysql.bean.hbase.MonitorInfo;
import com.zte.ums.oespaas.mysql.bean.hbase.OSInfo;
import com.zte.ums.oespaas.mysql.bean.hbase.RegisterInfo;
import com.zte.ums.oespaas.mysql.message.ProjectConfig;
import com.zte.ums.oespaas.mysql.util.NetUtils;
import com.zte.ums.oespaas.mysql.util.TimeUtils;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.client.*;
import org.apache.hadoop.hbase.util.Bytes;
import org.eclipse.jetty.util.ConcurrentHashSet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by 10183966 on 8/22/16.
 */
public class HBaseOperator {
    private static Logger logger = LoggerFactory.getLogger(HBaseOperator.class);

    public static Configuration hbaseConf = getHbaseConfig();
    public static HBaseAdmin hBaseAdmin = null;
    public static HTable hTableMonitor = null;
    public static HTable hTableOS = null;
    public static HTable hTableRegister = null;

    public static Set<String> osNeIdList = new ConcurrentHashSet<String>();
    public static Set<String> dbNeIdList = new ConcurrentHashSet<String>();
    public static Map<String, String> dbNeIdMapOSNeId = new ConcurrentHashMap<String, String>();
    public static Map<String, RegisterInfo> dbNeIdMapRegisterInfo = new ConcurrentHashMap<String, RegisterInfo>();
    public static Map<String, MonitorInfo> rowkeyMapMonitorInfo = new ConcurrentHashMap<String, MonitorInfo>();
    public static Map<String, OSInfo> rowkeyMapOSInfo = new ConcurrentHashMap<String, OSInfo>();

    public static Configuration getHbaseConfig() {
        Configuration conf = HBaseConfiguration.create();
        conf.set("hbase.zookeeper.quorum", ProjectConfig.HBASE_IP);
        conf.set("hbase.zookeeper.property.clientPort", ProjectConfig.HBASE_PORT);

        return conf;
    }

    public static byte[] generateRowKey(String prefixName, long timestamp) {
        long reverseTimestamp = TimeUtils.reverseTimeMillis(timestamp);
        return Bytes.toBytes(prefixName + "^" + reverseTimestamp);
    }

    public static byte[] generateRowKey(String prefixName, String time) {
        long reverseTime = TimeUtils.reverseTimeMillis(Long.valueOf(time));
        return Bytes.toBytes(prefixName + "^" + reverseTime);
    }

    public static boolean deleteRow(HTable tableObj, String rowkey) {
        if (rowkey == null || rowkey.isEmpty()) {
            return false;
        }

        if (!NetUtils.isHBaseRunnig()) {
            return false;
        }

        try {
            if (tableObj != null) {
                byte[] row = Bytes.toBytes(rowkey);
                Delete delete = new Delete(row);

                tableObj.delete(delete);
                return true;
            }
        } catch (Exception e) {
            logger.error(e.getMessage());
        }

        return false;
    }

    public static boolean deleteRow(HTable tableObj, byte[] rowkey) {
        if (rowkey == null || rowkey.length == 0) {
            return false;
        }

        if (!NetUtils.isHBaseRunnig()) {
            return false;
        }

        try {
            if (tableObj != null) {
                Delete delete = new Delete(rowkey);
                tableObj.delete(delete);
                return true;
            }
        } catch (Exception e) {
            logger.error(e.getMessage());
        }

        return false;
    }

    public static boolean deleteRowByNeId(HTable tableObj, String neId) {
        if (neId == null || neId.isEmpty()) {
            return false;
        }

        if (!NetUtils.isHBaseRunnig()) {
            return false;
        }


        boolean isDel = false;
        try {
            Scan scan = new Scan();
            if (tableObj != null) {
                ResultScanner rs = tableObj.getScanner(scan);
                if (rs != null) {
                    for (Result r : rs) {
                        String rowKey = Bytes.toString(r.getRow());
                        if (neId.equals(rowKey.substring(0, rowKey.indexOf("^")))) {
                            Delete del = new Delete(Bytes.toBytes(rowKey));
                            tableObj.delete(del);

                            isDel = true;
                        }
                    }
                }
            }

            return isDel;
        } catch (IOException e) {
            logger.error(e.getMessage());
        }

        return false;
    }
}
