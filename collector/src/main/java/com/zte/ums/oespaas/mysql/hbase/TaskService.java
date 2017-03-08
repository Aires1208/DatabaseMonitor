package com.zte.ums.oespaas.mysql.hbase;

import com.zte.ums.oespaas.mysql.bean.hbase.RegisterInfo;
import com.zte.ums.oespaas.mysql.message.ProjectConfig;
import com.zte.ums.oespaas.mysql.util.JsonUtils;
import com.zte.ums.oespaas.mysql.util.RestUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by 10183966 on 9/1/16.
 */
public class TaskService {
    private static final Logger logger = LoggerFactory.getLogger(TaskService.class);

    private static String url = "http://" + ProjectConfig.DBMONITOR_AGENT_IP + ":" + ProjectConfig.DBMONITOR_AGENT_PORT
            + "/api/itmagent/v1/tasks";

    public static boolean deleteTask(int taskId){
        if (taskId >= 0){
            String delUrl = url + "/" + taskId;
            return RestUtils.checkDeleteResponseCode(delUrl, 204);
        }

        return false;
    }

    public static void createTask(RegisterInfo registerInfo){
        createOSTask("os.linux.cpu", registerInfo);
        createOSTask("os.linux.ram", registerInfo);
        createOSTask("os.linux.mysqlnetwork", registerInfo);
        createOSTask("os.linux.mysqldisk", registerInfo);

        createDBTask("database.mysql.session", registerInfo);
        createDBTask("database.mysql.slowlog", registerInfo);
        createDBTask("database.mysql.generallog", registerInfo);
        createDBTask("database.mysql.system", registerInfo);
        createDBTask("database.mysql.users", registerInfo);
        createDBTask("database.mysql.db", registerInfo);
    }

    public static boolean createDBTask(String metricId, RegisterInfo registerInfo){
        Map<String, String> tagMap = new HashMap<String, String>();
        tagMap.put("aaa", "bbb");
        String tag = JsonUtils.simpleMapToJsonStr(tagMap);

        Map<String, String> paramMap = new HashMap<String, String>();
        paramMap.put("ip", registerInfo.getUrl());
        paramMap.put("port", String.valueOf(registerInfo.getDbPort()));
        paramMap.put("username", registerInfo.getDbUsername());
        paramMap.put("password", registerInfo.getDbPassword());
        String params = JsonUtils.simpleMapToJsonStr(paramMap);

        String neId = registerInfo.getDbNeId();

        String json = "{";
        json += "\"neId\":\"" + neId + "\",";
        json += "\"metricId\":\"" + metricId + "\",";
        json += "\"granularity\": 60,";
        json += "\"properties\":" + params + "\",";
        json += "\"tags\":" + tag;
        json += "}";

        return RestUtils.checkPostResponseCode(url, json, 200);
    }

    public static boolean createOSTask(String metricId, RegisterInfo registerInfo){
        Map<String, String> tagMap = new HashMap<String, String>();
        tagMap.put("dahu", "www");
        String tag = JsonUtils.simpleMapToJsonStr(tagMap);

        Map<String, String> paramMap = new HashMap<String, String>();
        paramMap.put("ip", registerInfo.getUrl());
        paramMap.put("protocol", registerInfo.getConnectType());
        paramMap.put("port", String.valueOf(registerInfo.getHostPort()));
        paramMap.put("username", registerInfo.getHostUsername());
        paramMap.put("password", registerInfo.getHostPassword());
        String params = JsonUtils.simpleMapToJsonStr(paramMap);

        String neId = registerInfo.getOsNeId();

        String json = "{";
        json += "\"neId\":\"" + neId + "\",";
        json += "\"metricId\":\"" + metricId + "\",";
        json += "\"granularity\": 60,";
        json += "\"properties\":" + params + "\",";
        json += "\"tags\":" + tag;
        json += "}";

        return RestUtils.checkPostResponseCode(url, json, 200);
    }
}
