package com.zte.ums.oespaas.mysql.hbase;

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

/**
 * Created by 10183966 on 8/23/16.
 */
public class OSInfoService {
    private static final Logger logger = LoggerFactory.getLogger(OSInfoService.class);

    public static List<OSInfo> getOSInfoList(String osNeId, Range range) {
        List<OSInfo> osInfos = new ArrayList<OSInfo>();
        if (!NetUtils.isHBaseRunnig()) {
            return osInfos;
        }

        if (!HBaseCheck.isChecked){
            HBaseCheck.checkHTable();
        }

        long start = TimeUtils.reverseTimeMillis(range.getTo());
        long end = TimeUtils.reverseTimeMillis(range.getFrom());
        for (String rowkey : HBaseOperator.rowkeyMapOSInfo.keySet()){
            String neId = rowkey.substring(0, rowkey.indexOf("^"));
            long reverseTime = Long.valueOf(rowkey.substring(rowkey.indexOf("^") + 1));

            if (neId.equals(osNeId) && reverseTime >= start && reverseTime < end) {
                osInfos.add(HBaseOperator.rowkeyMapOSInfo.get(rowkey));
            }
        }

        return osInfos;
    }

    public static List<OSInfo> getOSInfoListFromHBase(String osNeId, Range range) {
        if (!HBaseCheck.isChecked){
            HBaseCheck.checkHTable();
        }

        List<OSInfo> osInfoList = new ArrayList<OSInfo>();
        if (osNeId == null || osNeId.equals("-1") || osNeId.isEmpty()
                || range == null) {
            return osInfoList;
        }

        if (!NetUtils.isHBaseRunnig()) {
            return osInfoList;
        }

        Scan scan = createScan(osNeId, range);
        ResultScanner rs;
        try {
            if (HBaseOperator.hTableOS != null) {
                rs = HBaseOperator.hTableOS.getScanner(scan);
                for (Result result : rs) {
                    OSInfo osInfo;
                    try {
                        osInfo = resolveResult(result);
                    } catch (Exception e){
                        continue;
                    }

                    osInfoList.add(osInfo);
                }
            }
        } catch (IOException e) {
            logger.error(e.getMessage());
        }

        return osInfoList;
    }

    private static Scan createScan(String osNeId, Range range) {
        Scan scan = new Scan();
        byte[] startRowKey = HBaseOperator.generateRowKey(osNeId, range.getTo());
        byte[] stopRowKey = HBaseOperator.generateRowKey(osNeId, range.getFrom());
        scan.setStartRow(startRowKey);
        scan.setStopRow(stopRowKey);
        return scan;
    }

    public static OSInfo resolveResult(Result result){
        String rowKey = Bytes.toString(result.getRow());
        String osNeId = rowKey.substring(0, rowKey.indexOf("^"));
        long reverseTime = Long.valueOf(rowKey.substring(rowKey.indexOf("^") + 1));
        String collectTime = String.valueOf(TimeUtils.reverseTimeMillis(reverseTime));

        OSInfo osInfo = new OSInfo();
        osInfo.setOsNeId(osNeId);
        osInfo.setCollectTime(collectTime);

        byte[] cpuRatioBytes = result.getValue(HBaseConstant.OS_FAMILYNAME, HBaseConstant.OS_CPURATIO_COLUMNNAME);
        byte[] diskIoBytes = result.getValue(HBaseConstant.OS_FAMILYNAME, HBaseConstant.OS_DISKIO_COLUMNNAME);
        byte[] networkIoBytes = result.getValue(HBaseConstant.OS_FAMILYNAME, HBaseConstant.OS_NETWORKIO_COLUMNNAME);
        byte[] memoryRatioBytes = result.getValue(HBaseConstant.OS_FAMILYNAME, HBaseConstant.OS_MOMERYRATIO_COLUMNNAME);

        if (null != cpuRatioBytes) {
            CpuRatio cpuRatio = new CpuRatio();
            cpuRatio.readValue(cpuRatioBytes);
            osInfo.setCpuRatio(cpuRatio);
        }
        if (null != memoryRatioBytes) {
            MemoryRatio memoryRatio = new MemoryRatio();
            memoryRatio.readValue(memoryRatioBytes);
            osInfo.setMemoryRatio(memoryRatio);
        }
        if (null != diskIoBytes) {
            DiskIo diskIo = new DiskIo();
            diskIo.readValue(diskIoBytes);
            osInfo.setDiskIo(diskIo);
        }
        if (null != networkIoBytes) {
            NetworkIo networkIo = new NetworkIo();
            networkIo.readValue(networkIoBytes);
            osInfo.setNetworkIo(networkIo);
        }

        return osInfo;
    }
}
