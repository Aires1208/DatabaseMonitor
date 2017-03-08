package com.zte.ums.oespaas.mysql.service;

import com.zte.ums.oespaas.mysql.bean.Db;
import com.zte.ums.oespaas.mysql.bean.DbBuilder;
import com.zte.ums.oespaas.mysql.bean.Dbs;
import com.zte.ums.oespaas.mysql.bean.hbase.*;
import com.zte.ums.oespaas.mysql.hbase.*;
import com.zte.ums.oespaas.mysql.message.ProjectConfig;
import com.zte.ums.oespaas.mysql.util.NetUtils;
import com.zte.ums.oespaas.mysql.util.TimeConvert;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * Created by 10183966 on 8/17/16.
 */

@Service
public class XDBsService {
    private static final Logger logger = LoggerFactory.getLogger(XDBsService.class);

    public Map<String, Object> getDbs(Range range) {
        if (!HBaseCheck.isChecked){
            HBaseCheck.checkHTable();
        }

        Map<String, Object> result = new LinkedHashMap<String, Object>();

        if (!NetUtils.isHBaseRunnig()) {
            result.put("result", "error");
            result.put("msg", "hbase is not running on " + ProjectConfig.HBASE_IP);
            return result;
        }

        List<Db> dbList = new ArrayList<Db>();
        Set<RegisterInfo> registerInfos = RegisterInfoService.getRegisterInfoList();
        for (RegisterInfo registerInfo : registerInfos) {
            dbList.add(createDb(registerInfo, range));
        }

        result.put("result", "ok");
        result.put("msg", new Dbs(dbList));
        return result;
    }

    public List<Map<String, String>> getDBList(){
        if (!HBaseCheck.isChecked){
            HBaseCheck.checkHTable();
        }

        List<Map<String, String>> dbList = new ArrayList<Map<String, String>>();
        Set<RegisterInfo> registerInfos = RegisterInfoService.getRegisterInfoList();
        for (RegisterInfo registerInfo : registerInfos) {
            try {
                Map<String, String> db = new LinkedHashMap<String, String>();
                db.put("dbName", registerInfo.getDbName());
                db.put("dbIp", registerInfo.getUrl());
                db.put("dbPort", String.valueOf(registerInfo.getDbPort()));
                dbList.add(db);
            } catch (Exception e){
                logger.error(e.getMessage());
            }
        }

        return dbList;
    }

    public Db createDb(RegisterInfo registerInfo, Range range){
        if (registerInfo == null){
            return new DbBuilder().DbName("").DbHost("").OsNeId("").DbNeId("").CollectTime("")
                    .Status("").DbType("").Health(false).Queries(0L).TimeSpent("0").Cpu("0").build();
        }

        String dbName = registerInfo.getDbName();
        String dbHost = registerInfo.getUrl() + ":" + registerInfo.getDbPort();

        String dbType = "";
        String type = registerInfo.getDbType();
        if (type.equalsIgnoreCase("MYSQL")) {
            dbType = "MySQL";
        } else if (type.equalsIgnoreCase("ORACLE")) {
            dbType = "Oracle";
        } else if (type.equalsIgnoreCase("SQLSERVER")) {
            dbType = "SqlServer";
        }

        String status = "OFF";
        if (NetUtils.checkPortIsUsed(registerInfo.getUrl(), String.valueOf(registerInfo.getDbPort()))) {
            status = "ON";
        }

        String collectTime = TimeConvert.getFullTimeStringFromTimestamp(Long.valueOf(registerInfo.getCollectTime()));

        //Todo: health method is not defined
        boolean health = true;

        String dbNeId = registerInfo.getDbNeId();
        List<Object> result = getQueryAndTimeSpent(dbNeId, range);
        long queries = (Long)(result.get(0));
        String timeSpent = (String) (result.get(1));

        String osNeId = registerInfo.getOsNeId();
        String cpu = getCpuUsage(osNeId, range);

        return new DbBuilder().DbName(dbName).DbHost(dbHost).OsNeId(osNeId).DbNeId(dbNeId).CollectTime(collectTime)
                .Status(status).DbType(dbType).Health(health).Queries(queries).TimeSpent(timeSpent).Cpu(cpu).build();
    }

    private List<Object> getQueryAndTimeSpent(String dbNeId, Range range){
        List<Object> result = new ArrayList<Object>();

        long queries = 0L;
        String timeSpent = "0";
        long totalSpend = 0L;

        List<MonitorInfo> monitorInfoList = MonitorInfoService.getMonitorInfoList(dbNeId, range);
        for (MonitorInfo monitorInfo : monitorInfoList) {
            try {
                StatusBean statusBean = monitorInfo.getStatusBean();
                if (statusBean != null) {
                    if (statusBean.getCom_select() != null && !statusBean.getCom_select().isEmpty()) {
                        queries += Long.valueOf(statusBean.getCom_select());
                    }
                }

                SessionBean sessionBean = monitorInfo.getSessionBean();
                if (sessionBean != null) {
                    if (sessionBean.getSpeedTime() != null && !sessionBean.getSpeedTime().isEmpty()) {
                        totalSpend += TimeConvert.getSecondFromTimeString(sessionBean.getSpeedTime());
                    }
                }
            } catch (Exception e){
                logger.error(e.getMessage());
            }
        }

        if (monitorInfoList.size() != 0) {
            totalSpend /= monitorInfoList.size();
            timeSpent = TimeConvert.getTimeStringFromSecond(totalSpend);
        }

        result.add(queries);
        result.add(timeSpent);
        return result;
    }

    private String getCpuUsage(String osNeId, Range range){
        int totalNum = 0;
        double totalCpuUsage = 0.0;
        List<OSInfo> osInfoList = OSInfoService.getOSInfoList(osNeId, range);
        for (OSInfo osInfo : osInfoList) {
            try {
                CpuRatio cpuRatio = osInfo.getCpuRatio();
                if (cpuRatio != null) {
                    if (cpuRatio.getCpuUserRatio() != null && !cpuRatio.getCpuUserRatio().isEmpty()) {
                        totalCpuUsage += Double.valueOf(cpuRatio.getCpuUserRatio());
                    }

                    totalNum++;
                }
            } catch (Exception e){
                logger.error(e.getMessage());
            }
        }

        if (totalNum != 0) {
            totalCpuUsage /= totalNum;
        }

        return String.format("%d", (int) Math.round(totalCpuUsage)) + "%";
    }
}
