package com.zte.ums.oespaas.mysql.hbase;

import com.zte.ums.oespaas.mysql.bean.hbase.*;
import com.zte.ums.oespaas.mysql.util.NetUtils;
import org.apache.hadoop.hbase.client.Put;
import org.apache.hadoop.hbase.util.Bytes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Set;

/**
 * Created by 10183966 on 8/23/16.
 */
public class OSInfoHandler {
    private static final Logger logger = LoggerFactory.getLogger(OSInfoHandler.class);

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

        if (!HBaseOperator.osNeIdList.contains(bean.getNeId())){
            return false;
        }

        try {
            if (HBaseOperator.hTableOS != null) {
                byte[] rowKey = HBaseOperator.generateRowKey(bean.getNeId(), bean.getCollectTime());
                Put put = new Put(rowKey);

                byte[] osBeanByteValue = bean.writeValue();
                if (bean instanceof CpuRatio) {
                    put.addColumn(HBaseConstant.OS_FAMILYNAME, HBaseConstant.OS_CPURATIO_COLUMNNAME, osBeanByteValue);
                } else if (bean instanceof MemoryRatio) {
                    put.addColumn(HBaseConstant.OS_FAMILYNAME, HBaseConstant.OS_MOMERYRATIO_COLUMNNAME, osBeanByteValue);
                } else if (bean instanceof NetworkIo) {
                    put.addColumn(HBaseConstant.OS_FAMILYNAME, HBaseConstant.OS_NETWORKIO_COLUMNNAME, osBeanByteValue);
                } else if (bean instanceof DiskIo) {
                    put.addColumn(HBaseConstant.OS_FAMILYNAME, HBaseConstant.OS_DISKIO_COLUMNNAME, osBeanByteValue);
                }

                HBaseOperator.hTableOS.put(put);
                addToHBaseObj(bean);
                return true;
            }
        } catch (IOException e) {
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

        Set<String> rowkeys = HBaseOperator.rowkeyMapOSInfo.keySet();
        for (String rowkey: rowkeys){
            String neId = rowkey.substring(0, rowkey.indexOf("^"));
            String osNeId = HBaseOperator.dbNeIdMapOSNeId.get(dbNeId);
            if (neId.equals(osNeId)){
                HBaseOperator.deleteRow(HBaseOperator.hTableOS, rowkey);
                HBaseOperator.rowkeyMapOSInfo.remove(rowkey);
            }
        }
    }

    private static void addToHBaseObj(AbstractBean bean){
        String rowkey = Bytes.toString(HBaseOperator.generateRowKey(bean.getNeId(), bean.getCollectTime()));
        OSInfo osInfo = new OSInfo();
        osInfo.setOsNeId(bean.getNeId());
        osInfo.setCollectTime(bean.getCollectTime());

        if (bean instanceof CpuRatio) {
            if (HBaseOperator.rowkeyMapOSInfo.containsKey(rowkey)){
                HBaseOperator.rowkeyMapOSInfo.get(rowkey).setCpuRatio((CpuRatio)bean);
            } else {
                osInfo.setCpuRatio((CpuRatio)bean);
                HBaseOperator.rowkeyMapOSInfo.put(rowkey, osInfo);
            }
        } else if (bean instanceof MemoryRatio) {
            if (HBaseOperator.rowkeyMapOSInfo.containsKey(rowkey)){
                HBaseOperator.rowkeyMapOSInfo.get(rowkey).setMemoryRatio((MemoryRatio) bean);
            } else {
                osInfo.setMemoryRatio((MemoryRatio) bean);
                HBaseOperator.rowkeyMapOSInfo.put(rowkey, osInfo);
            }
        } else if (bean instanceof NetworkIo) {
            if (HBaseOperator.rowkeyMapOSInfo.containsKey(rowkey)){
                HBaseOperator.rowkeyMapOSInfo.get(rowkey).setNetworkIo((NetworkIo) bean);
            } else {
                osInfo.setNetworkIo((NetworkIo) bean);
                HBaseOperator.rowkeyMapOSInfo.put(rowkey, osInfo);
            }
        } else if (bean instanceof DiskIo) {
            if (HBaseOperator.rowkeyMapOSInfo.containsKey(rowkey)){
                HBaseOperator.rowkeyMapOSInfo.get(rowkey).setDiskIo((DiskIo) bean);
            } else {
                osInfo.setDiskIo((DiskIo) bean);
                HBaseOperator.rowkeyMapOSInfo.put(rowkey, osInfo);
            }
        }
    }
    
}
