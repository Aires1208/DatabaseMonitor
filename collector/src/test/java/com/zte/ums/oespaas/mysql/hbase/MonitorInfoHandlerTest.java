package com.zte.ums.oespaas.mysql.hbase;

import com.zte.ums.oespaas.mysql.bean.DBInfoBean;
import com.zte.ums.oespaas.mysql.bean.hbase.*;
import org.junit.Test;

/**
 * Created by 10203846 on 11/9/16.
 */
public class MonitorInfoHandlerTest {

    @Test
    public void testInsert() throws Exception {
        int taskId = 1;
        String dbNeId = "test";
        String collectTime = "1111111";
        int granulartiy = 60;

        DBInfoBean dbInfoBean = new DBInfoBean();
        dbInfoBean.setTaskId(taskId);
        dbInfoBean.setNeId(dbNeId);
        dbInfoBean.setCollectTime(collectTime);
        dbInfoBean.setGranularity(granulartiy);
        dbInfoBean.setDbName("db");
        dbInfoBean.setDataLength("12");
        dbInfoBean.setIndexLength("10");
        MonitorInfoHandler.insert(dbInfoBean);

        GeneralLogBean generalLogBean = new GeneralLogBean();
        generalLogBean.setTaskId(taskId);
        generalLogBean.setNeId(dbNeId);
        generalLogBean.setCollectTime(collectTime);
        generalLogBean.setGranularity(granulartiy);
        generalLogBean.setEventTime("11111");
        generalLogBean.setUserHost("root");
        generalLogBean.setCommandType("Query");
        generalLogBean.setSqlInfo("select * from user");
        MonitorInfoHandler.insert(generalLogBean);

        SessionBean sessionBean = new SessionBean();
        sessionBean.setTaskId(taskId);
        sessionBean.setNeId(dbNeId);
        sessionBean.setCollectTime(collectTime);
        sessionBean.setGranularity(granulartiy);
        sessionBean.setOid("123");
        sessionBean.setUser("root");
        sessionBean.setHostName("localhost");
        sessionBean.setDbname("db");
        sessionBean.setCommand("Query");
        sessionBean.setSpeedTime("10");
        sessionBean.setState("Sleep");
        sessionBean.setSqlInfo("select");
        MonitorInfoHandler.insert(sessionBean);

        SlowLogBean slowLogBean = new SlowLogBean();
        slowLogBean.setTaskId(taskId);
        slowLogBean.setNeId(dbNeId);
        slowLogBean.setCollectTime(collectTime);
        slowLogBean.setGranularity(granulartiy);
        slowLogBean.setBeginTime("1111");
        slowLogBean.setUserHost("localhost");
        slowLogBean.setQueryTime("18");
        slowLogBean.setLockTime("2");
        slowLogBean.setDbName("db");
        slowLogBean.setSqlText("select");
        MonitorInfoHandler.insert(slowLogBean);

        StatusBean statusBean = new StatusBean();
        statusBean.setTaskId(taskId);
        statusBean.setNeId(dbNeId);
        statusBean.setCollectTime(collectTime);
        statusBean.setGranularity(granulartiy);
        statusBean.setQuestions("152");
        statusBean.setThreads_running("1");
        statusBean.setCom_select("17");
        statusBean.setUp_time("188");
        statusBean.setVars("");
        MonitorInfoHandler.insert(statusBean);

        UserBean userBean = new UserBean();
        userBean.setTaskId(taskId);
        userBean.setNeId(dbNeId);
        userBean.setCollectTime(collectTime);
        userBean.setGranularity(granulartiy);
        userBean.setUserName("root");
        userBean.setHostName("localhost");
        MonitorInfoHandler.insert(userBean);
    }
}