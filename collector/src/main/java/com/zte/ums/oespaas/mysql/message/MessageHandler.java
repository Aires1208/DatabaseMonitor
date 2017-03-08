package com.zte.ums.oespaas.mysql.message;

import com.zte.ums.oespaas.mysql.bean.DBInfoBean;
import com.zte.ums.oespaas.mysql.bean.hbase.*;
import com.zte.ums.oespaas.mysql.hbase.*;
import com.zte.ums.oespaas.mysql.util.NetUtils;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

/**
 * Created by 10183966 on 8/31/16.
 */
public class MessageHandler {
    private static final Logger logger = LoggerFactory.getLogger(MessageHandler.class);

    public static void messageProess(String message) {
        logger.info("message: " + message);

        if (!NetUtils.isHBaseRunnig()) {
            return;
        }

        if (!HBaseCheck.isChecked){
            HBaseCheck.checkHTable();
        }

        try {
            JSONObject jsonObject = JSONObject.fromObject(message);
            String metricId = jsonObject.getString("metricId");
            int taskId = jsonObject.getInt("taskId");
            String neId = jsonObject.getString("neId");
            String collectTime = jsonObject.getString("collectTime");
            int granularity = jsonObject.getInt("granularity");
            String result = jsonObject.getString("result");

            //checkTask(metricId, neId, taskId);

            if (result.equals("success")) {
                JSONArray jsonArray = jsonObject.getJSONArray("values");
                if (jsonArray.size() == 0){
                    return;
                }

                int totalFail = 0;
                for (int i = 0; i < jsonArray.size(); i++) {
                    String json = jsonArray.get(i).toString();
                    Map<String, String> valueMap = resovleValue(json);
                    boolean isWrite = writeMessageToHBase(metricId, taskId, neId, collectTime, granularity, valueMap);
                    if (!isWrite) {
                        totalFail++;
                        logger.error(metricId + ": taskId=" + taskId + ", valueIndex=" + i + ", fail to insert to hbase");
                    }
                }

                if (totalFail > 0) {
                    logger.info(metricId + ": taskId=" + taskId + ", " + (jsonArray.size() - totalFail)
                            + " success and " + totalFail + " fail to insert to hbase");
                } else {
                    logger.info(metricId + ": taskId=" + taskId + ", " + "all success to insert to hbase");
                }
            }
        } catch (Exception e) {
            logger.error(e.getMessage());
        }
    }

    private static boolean writeMessageToHBase(String metricId, int taskId, String neId, String collectTime, int granularity, Map<String, String> valueMap) {
        if ("os.linux.cpu".equals(metricId)) {
            CpuRatio cpuRatio = new CpuRatio();
            cpuRatio.setTaskId(taskId);
            cpuRatio.setNeId(neId);
            cpuRatio.setCollectTime(collectTime);
            cpuRatio.setGranularity(granularity);
            cpuRatio.setCpuIOWaitRatio(valueMap.get("CPUWAITRATIO"));
            cpuRatio.setCpuUserRatio(valueMap.get("CPUUSERRATIO"));
            cpuRatio.setCpuSysRatio(valueMap.get("CPUSYSRATIO"));
            return OSInfoHandler.insert(cpuRatio);
        } else if ("os.linux.ram".equals(metricId)) {
            MemoryRatio memoryRatio = new MemoryRatio();
            memoryRatio.setTaskId(taskId);
            memoryRatio.setNeId(neId);
            memoryRatio.setCollectTime(collectTime);
            memoryRatio.setGranularity(granularity);
            memoryRatio.setMemoryRatio(valueMap.get("USEDMEMRATIO"));
            return OSInfoHandler.insert(memoryRatio);
        } else if ("os.linux.mysqlnetwork".equals(metricId)) {
            NetworkIo networkIo = new NetworkIo();
            networkIo.setTaskId(taskId);
            networkIo.setNeId(neId);
            networkIo.setCollectTime(collectTime);
            networkIo.setGranularity(granularity);
            networkIo.setMySQLInTransrate(valueMap.get("MYSQLINTRANSRATE"));
            networkIo.setMySQLOutTransrate(valueMap.get("MYSQLOUTTRANSRATE"));
            return OSInfoHandler.insert(networkIo);
        } else if ("os.linux.mysqldisk".equals(metricId)) {
            DiskIo diskIo = new DiskIo();
            diskIo.setTaskId(taskId);
            diskIo.setNeId(neId);
            diskIo.setCollectTime(collectTime);
            diskIo.setGranularity(granularity);
            diskIo.setDiskReadBytes(valueMap.get("DISKREADBYTES"));
            diskIo.setDiskWriteBytes(valueMap.get("DISKWRITEBYTES"));
            return OSInfoHandler.insert(diskIo);
        } else if ("database.mysql.session".equals(metricId)) {
            SessionBean sessionBean = new SessionBean();
            sessionBean.setTaskId(taskId);
            sessionBean.setNeId(neId);
            sessionBean.setCollectTime(collectTime);
            sessionBean.setGranularity(granularity);
            sessionBean.setOid(valueMap.get("OID"));
            sessionBean.setUser(valueMap.get("USER"));
            sessionBean.setHostName(valueMap.get("HOSTNAME"));
            sessionBean.setDbname(valueMap.get("DBNAME"));
            sessionBean.setCommand(valueMap.get("COMMAND"));
            sessionBean.setSpeedTime(valueMap.get("SPENDTIME"));
            sessionBean.setState(valueMap.get("STATE"));
            sessionBean.setSqlInfo(valueMap.get("SQLINFO"));
            return MonitorInfoHandler.insert(sessionBean);
        } else if ("database.mysql.slowlog".equals(metricId)) {
            SlowLogBean slowLogBean = new SlowLogBean();
            slowLogBean.setTaskId(taskId);
            slowLogBean.setNeId(neId);
            slowLogBean.setCollectTime(collectTime);
            slowLogBean.setGranularity(granularity);
            slowLogBean.setBeginTime(valueMap.get("BEGINTIME"));
            slowLogBean.setUserHost(valueMap.get("USERHOST"));
            slowLogBean.setQueryTime(valueMap.get("QUERYTIME"));
            slowLogBean.setLockTime(valueMap.get("LOCKTIME"));
            slowLogBean.setDbName(valueMap.get("DBNAME"));
            slowLogBean.setSqlText(valueMap.get("SQLTEXT"));
            return MonitorInfoHandler.insert(slowLogBean);
        } else if ("database.mysql.generallog".equals(metricId)) {
            GeneralLogBean generalLogBean = new GeneralLogBean();
            generalLogBean.setTaskId(taskId);
            generalLogBean.setNeId(neId);
            generalLogBean.setCollectTime(collectTime);
            generalLogBean.setGranularity(granularity);
            generalLogBean.setEventTime(valueMap.get("EVENTTIME"));
            generalLogBean.setUserHost(valueMap.get("USERHOST"));
            generalLogBean.setCommandType(valueMap.get("COMMANDTYPE"));
            generalLogBean.setSqlInfo(valueMap.get("SQLINFO"));
            return MonitorInfoHandler.insert(generalLogBean);
        } else if ("database.mysql.system".equals(metricId)) {
            StatusBean statusBean = new StatusBean();
            statusBean.setTaskId(taskId);
            statusBean.setNeId(neId);
            statusBean.setCollectTime(collectTime);
            statusBean.setGranularity(granularity);
            statusBean.setQuestions(valueMap.get("Questions"));
            statusBean.setThreads_running(valueMap.get("Threads_running"));
            statusBean.setCom_select(valueMap.get("Com_select"));
            statusBean.setUp_time(valueMap.get("Uptime"));
            statusBean.setVars(valueMap.toString());
            return MonitorInfoHandler.insert(statusBean);
        } else if ("database.mysql.users".equals(metricId)) {
            UserBean userBean = new UserBean();
            userBean.setTaskId(taskId);
            userBean.setNeId(neId);
            userBean.setCollectTime(collectTime);
            userBean.setGranularity(granularity);
            userBean.setUserName(valueMap.get("USERNAME"));
            userBean.setHostName(valueMap.get("HOSTNAME"));
            return MonitorInfoHandler.insert(userBean);
        } else if ("database.mysql.db".equals(metricId)) {
            DBInfoBean dbInfoBean = new DBInfoBean();
            dbInfoBean.setTaskId(taskId);
            dbInfoBean.setNeId(neId);
            dbInfoBean.setCollectTime(collectTime);
            dbInfoBean.setGranularity(granularity);
            dbInfoBean.setDbName(valueMap.get("DBNAME"));
            dbInfoBean.setDataLength(valueMap.get("DATALENGTH"));
            dbInfoBean.setIndexLength(valueMap.get("INDEXLENGTH"));
            return MonitorInfoHandler.insert(dbInfoBean);
        }

        return false;
    }

    private static boolean checkTask(String metricId, String neId, int taskId){
        if (metricId.equals("os.linux.cpu") || metricId.equals("os.linux.ram")
                || metricId.equals("os.linux.mysqlnetwork")
                || metricId.equals("os.linux.mysqldisk")) {
            if (!HBaseOperator.osNeIdList.contains(neId)){
                return TaskService.deleteTask(taskId);
            }
        } else if (metricId.equals("database.mysql.session") || metricId.equals("database.mysql.slowlog")
                || metricId.equals("database.mysql.generallog")
                || metricId.equals("database.mysql.system")
                || metricId.equals("database.mysql.users")
                || metricId.equals("database.mysql.db")) {
            if (!HBaseOperator.dbNeIdList.contains(neId)){
                return TaskService.deleteTask(taskId);
            }
        }

        return false;
    }

    private static Map<String, String> resovleValue(String json){
        Map<String, String> valueMap = new HashMap<String, String>();

        try {
            JSONObject jsonObject = JSONObject.fromObject(json);
            Iterator iterator = jsonObject.keys();
            while (iterator.hasNext()) {
                String key = (String) iterator.next();
                String value = (String) jsonObject.get(key);
                valueMap.put(key, value);
            }
        } catch (Exception e){
            logger.error(e.getMessage());
        }

        return valueMap;
    }
}
