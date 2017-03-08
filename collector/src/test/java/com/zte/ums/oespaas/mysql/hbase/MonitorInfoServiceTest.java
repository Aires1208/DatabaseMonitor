package com.zte.ums.oespaas.mysql.hbase;

import com.zte.ums.oespaas.mysql.bean.hbase.MonitorInfo;
import com.zte.ums.oespaas.mysql.bean.hbase.Range;
import com.zte.ums.oespaas.mysql.util.MapUtils;
import com.zte.ums.oespaas.mysql.util.NetUtils;
import com.zte.ums.oespaas.mysql.util.TimeUtils;
import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.client.ResultScanner;
import org.apache.hadoop.hbase.client.Scan;
import org.apache.hadoop.hbase.util.Bytes;
import org.junit.Test;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created by 10183966 on 9/1/16.
 */
public class MonitorInfoServiceTest {
    @Test
    public void getMonitorInfoListTest() throws IOException {
        if (!NetUtils.isHBaseRunnig()) {
            return;
        }

        List<Long> times = new ArrayList<Long>();
        Map<String, Integer> neIdMap = new HashMap<String, Integer>();
        Scan scan = new Scan();

        if (HBaseOperator.hTableMonitor != null) {
            ResultScanner resultScanner = HBaseOperator.hTableMonitor.getScanner(scan);
            for (Result result : resultScanner) {
                String rowKey = Bytes.toString(result.getRow());
                String dbNeId = rowKey.substring(0, rowKey.indexOf("^"));
                if (neIdMap.containsKey(dbNeId)) {
                    Integer num = neIdMap.get(dbNeId);
                    neIdMap.put(dbNeId, num + 1);
                } else {
                    neIdMap.put(dbNeId, 1);
                }

                String reverseTime = rowKey.substring(rowKey.indexOf("^") + 1, rowKey.length());
                long startTime = TimeUtils.reverseTimeMillis(reverseTime);
                times.add(startTime);
            }
        }

        if (times.size() >= 2) {
            Collections.sort(times, new Comparator<Long>() {
                @Override
                public int compare(Long o1, Long o2) {
                    return (int) (o1 - o2);
                }
            });

            System.out.println("startTime: " + times.get(0) + "     real:" + new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date(times.get(0))));
            System.out.println("endTime: " + times.get(times.size() - 1) + "    real:" + new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date(times.get(times.size() - 1))));
        }

        List<Map.Entry<?, ?>> topNList = MapUtils.getTopNSortedByMapValueDescend(neIdMap, 5);
        for (Map.Entry<?, ?> entry : topNList) {
            System.out.println("neIdName: " + entry.getKey());
            System.out.println("neIdNum: " + entry.getValue());
        }

        System.out.println("end");

    }

    @Test
    public void testMonitorInfoService() {
        List<MonitorInfo> monitorInfoList = MonitorInfoService.getMonitorInfoList(
                "6b8de06a-f64b-4320-9403-ffd3c8217a8a", new Range(1473131050990L, 1473154380594L));

        System.out.println("end");
    }
}
