package com.zte.ums.oespaas.mysql.hbase;

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
 * Created by 10183966 on 8/23/16.
 */
public class OSInfoServiceTest {
    @Test
    public void getOSInfoTest() throws IOException {
        if (!NetUtils.isHBaseRunnig()) {
            return;
        }

        List<Long> times = new ArrayList<Long>();
        Map<String, Integer> osNeIdMap = new HashMap<String, Integer>();
        Scan scan = new Scan();

        if (HBaseOperator.hTableOS != null) {
            ResultScanner resultScanner = HBaseOperator.hTableOS.getScanner(scan);
            for (Result result : resultScanner) {
                String rowKey = Bytes.toString(result.getRow());
                String osNeId = rowKey.substring(0, rowKey.indexOf("^"));
                if (osNeIdMap.containsKey(osNeId)) {
                    Integer num = osNeIdMap.get(osNeId);
                    osNeIdMap.put(osNeId, num + 1);
                } else {
                    osNeIdMap.put(osNeId, 1);
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

        List<Map.Entry<?, ?>> topNList = MapUtils.getTopNSortedByMapValueDescend(osNeIdMap, 5);
        for (Map.Entry<?, ?> entry : topNList) {
            System.out.println("osNeIdName: " + entry.getKey());
            System.out.println("osNeIdNum: " + entry.getValue());
        }

        System.out.println("end");
    }
}
