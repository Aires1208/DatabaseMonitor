package com.zte.ums.oespaas.mysql.hbase;

import com.zte.ums.oespaas.mysql.bean.hbase.CpuRatio;
import com.zte.ums.oespaas.mysql.bean.hbase.DiskIo;
import com.zte.ums.oespaas.mysql.bean.hbase.MemoryRatio;
import com.zte.ums.oespaas.mysql.bean.hbase.NetworkIo;
import org.junit.Test;

/**
 * Created by 10183966 on 8/23/16.
 */
public class OSInfoHandlerTest {
    @Test
    public void insertTest() throws Exception {
        int taskId = 1;
        String osNeId = "test";
        String collectTime = "111111";
        int granulartiy = 60;

        CpuRatio cpuRatio = new CpuRatio();
        cpuRatio.setTaskId(taskId);
        cpuRatio.setNeId(osNeId);
        cpuRatio.setCollectTime(collectTime);
        cpuRatio.setGranularity(granulartiy);
        cpuRatio.setCpuIOWaitRatio("80%");
        cpuRatio.setCpuSysRatio("30%");
        cpuRatio.setCpuUserRatio("50%");
        OSInfoHandler.insert(cpuRatio);

        MemoryRatio memoryRatio = new MemoryRatio();
        memoryRatio.setTaskId(taskId);
        memoryRatio.setNeId(osNeId);
        memoryRatio.setCollectTime(collectTime);
        memoryRatio.setGranularity(granulartiy);
        memoryRatio.setMemoryRatio("87%");
        OSInfoHandler.insert(memoryRatio);

        NetworkIo networkIo = new NetworkIo();
        networkIo.setTaskId(taskId);
        networkIo.setNeId(osNeId);
        networkIo.setCollectTime(collectTime);
        networkIo.setGranularity(granulartiy);
        networkIo.setMySQLInTransrate("54%");
        networkIo.setMySQLOutTransrate("98");
        OSInfoHandler.insert(networkIo);

        DiskIo diskIo = new DiskIo();
        diskIo.setTaskId(taskId);
        diskIo.setNeId(osNeId);
        diskIo.setCollectTime(collectTime);
        diskIo.setGranularity(granulartiy);
        diskIo.setDiskReadBytes("89");
        diskIo.setDiskWriteBytes("1024");
        OSInfoHandler.insert(diskIo);
    }

    @Test
    public void delRows() {

    }
}
