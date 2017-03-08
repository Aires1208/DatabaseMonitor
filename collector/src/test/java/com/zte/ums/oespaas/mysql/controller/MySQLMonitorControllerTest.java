package com.zte.ums.oespaas.mysql.controller;

import com.zte.ums.oespaas.mysql.Application;
import com.zte.ums.oespaas.mysql.bean.*;
import com.zte.ums.oespaas.mysql.bean.hbase.RegisterInfo;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import java.util.List;
import java.util.Map;

/**
 * Created by root on 9/8/16.
 */
//@RunWith(SpringJUnit4ClassRunner.class)
//@SpringApplicationConfiguration(classes = Application.class)
@RunWith(SpringRunner.class)
@ContextConfiguration(classes = Application.class)
@WebAppConfiguration
public class MySQLMonitorControllerTest {
    @Autowired
    private MySQLMonitorController mySQLMonitorController;

    private long from;
    private long to;
    private String dbNeId;
    private int topN;

    @Before
    public void setUp() throws Exception {
        //MonitorInfo
        //        startTime: 1473131050990  real:2016-09-05 23:04:10
        //        endTime: 1473154380594    real:2016-09-06 05:33:00
        //        neIdName: 6b8de06a-f64b-4320-9403-ffd3c8217a8a
        //        neIdNum: 528

        //OSInfo
        //        startTime: 1473062104453  real:2016-09-05 03:55:04
        //        endTime: 1473153337094    real:2016-09-06 05:15:37
        //        neIdName: bd7c2007-c5aa-4cfe-8843-6dda2883077c
        //        neIdNum: 351

        //RegisterInfo
        //        startTime: 1473392010543     real:2016-09-08 23:33:30
        //        endTime: 1473847132376    real:2016-09-14 05:58:52
        //        neId: null
        //        neIdNum: 29
        //        dbNeId: null
        //        neId: 6ba88735-4cdf-40b6-8b1b-c60c0c8344fa
        //        neIdNum: 1
        //        dbNeId: e8fa4db0-8c68-4565-a0f2-9e2c0299ebe5

//        dbNeId = "6b8de06a-f64b-4320-9403-ffd3c8217a8a";
//        from = 1473131050990L;
//        to = 1473154380594L;
        dbNeId = "5beaa136-48d6-454e-818b-6b1c1a96a2ee";
        from = 0L;
        to = System.currentTimeMillis();
        topN = 10;
    }

    @Test
    public void testAddAndDelMonitor() throws Exception {
        RegisterInfo registerInfo = new RegisterInfo();
        registerInfo.setDbName("mysql");
        registerInfo.setUrl("127.0.0.1");
        registerInfo.setDbType("MySql");
        registerInfo.setDbUsername("root");
        registerInfo.setDbPassword("root123");
        registerInfo.setDbPort(3306);
        registerInfo.setDbSID("");
        registerInfo.setConnectType("SSH");
        registerInfo.setHostPort(22);
        registerInfo.setHostUsername("root");
        registerInfo.setHostPassword("root123");

        Map<String, String> object = mySQLMonitorController.addDBMonitor(registerInfo);
        System.out.println("end");
    }

    @Test
    public void testGetDbs() throws Exception {
        Map<String, Object> result = mySQLMonitorController.getDbs(from, to);
        System.out.println("end");
    }

    @Test
    public void testGetQueries() throws Exception {
        Queries queries = mySQLMonitorController.getQueries(dbNeId, from, to, topN);
        System.out.println("end");
    }

    @Test
    public void testGetClients() throws Exception {
        Clients clients = mySQLMonitorController.getClients(dbNeId, from, to, topN);
        System.out.println("end");
    }

    @Test
    public void testGetSessions() throws Exception {
        Sessions sessions = mySQLMonitorController.getSessions(dbNeId, from, to, topN);
        System.out.println("end");
    }

    @Test
    public void testGetDBInfoDashboard() throws Exception {
        DBInfoDashboard dbInfoDashboard = mySQLMonitorController.getDBInfoDashboard(dbNeId, from, to);
        System.out.println("end");
    }

    @Test
    public void testGetDBInfoLive() throws Exception {
        DBInfoLive dbInfoLive = mySQLMonitorController.getDBInfoLive(dbNeId, from, to);
        System.out.println("end");
    }

    @Test
    public void testGetDBInfoObjects() throws Exception {
        DBInfoObjects dbInfoObjects = mySQLMonitorController.getDBInfoObjects(dbNeId, from, to);
        System.out.println("end");
    }

    @Test
    public void testGetReports() throws Exception {
        Reports reports = mySQLMonitorController.getReports(dbNeId, from, to);
        System.out.println("end");
    }

    @Test
    public void testGetDbList() throws Exception {
        List<Map<String, String>> dbList = mySQLMonitorController.getDBList();
        System.out.println("end");
    }
}