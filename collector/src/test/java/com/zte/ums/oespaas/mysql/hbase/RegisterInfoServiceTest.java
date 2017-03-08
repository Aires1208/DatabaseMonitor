package com.zte.ums.oespaas.mysql.hbase;

import com.zte.ums.oespaas.mysql.bean.hbase.RegisterInfo;
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
 * Created by 10183966 on 9/2/16.
 */
public class RegisterInfoServiceTest {
    @Test
    public void getRegisterInfoList() {
        Set<RegisterInfo> registerInfos = RegisterInfoService.getRegisterInfoList();
        for (RegisterInfo registerInfo : registerInfos) {
            System.out.println(registerInfo.toString());
        }

        System.out.println("end");
    }

    @Test
    public void getRegisterInfos() throws IOException {
        if (!NetUtils.isHBaseRunnig()) {
            return;
        }

        List<Long> times = new ArrayList<Long>();
        Map<String, Integer> neIdMap = new HashMap<String, Integer>();
        Map<String, String> neIdMapDbNeId = new HashMap<String, String>();

        Scan scan = new Scan();

        if (HBaseOperator.hTableRegister != null) {
            ResultScanner resultScanner = HBaseOperator.hTableRegister.getScanner(scan);
            for (Result result : resultScanner) {
                String rowKey = Bytes.toString(result.getRow());
                String dbNeId = rowKey.substring(0, rowKey.indexOf("^"));

                RegisterInfo registerInfo = new RegisterInfo();
                byte[] registerBytes = result.getValue(HBaseConstant.REGISTER_FAMILYNAME, HBaseConstant.REGISTER_INPUTPARA_COLUMNNAME);
                registerInfo.readValue(registerBytes);
                neIdMapDbNeId.put(dbNeId, registerInfo.getOsNeId());

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
            System.out.println("dbNeId: " + entry.getKey());
            System.out.println("neIdNum: " + entry.getValue());
            System.out.println("dbNeId: " + neIdMapDbNeId.get(entry.getKey()));
        }

        System.out.println("end");
    }
}
