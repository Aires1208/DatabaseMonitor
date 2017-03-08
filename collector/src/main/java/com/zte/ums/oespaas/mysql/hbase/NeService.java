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
public class NeService {
    private static final Logger logger = LoggerFactory.getLogger(NeService.class);

    private static String url = "http://" + ProjectConfig.DBMONITOR_AGENT_IP + ":" + ProjectConfig.DBMONITOR_AGENT_PORT
            + "/api/itmagent/v1/ne";

    public static String createDBNe(RegisterInfo registerInfo){
        Map<String, String> databaseTagsMap = new HashMap<String, String>();
        databaseTagsMap.put("aaa", "bbb");
        String databaseTags = JsonUtils.simpleMapToJsonStr(databaseTagsMap);

        Map<String, String> databasesParams = new HashMap<String, String>();
        databasesParams.put("ip", registerInfo.getUrl());
        databasesParams.put("port", String.valueOf(registerInfo.getDbPort()));
        databasesParams.put("username", registerInfo.getDbUsername());
        databasesParams.put("password", registerInfo.getDbPassword());
        String databasesProperties = JsonUtils.simpleMapToJsonStr(databasesParams);

        String databasesType = "database.mysql";

        String dbJson = "{";
        dbJson += "\"tags\":" + databaseTags + ",";
        dbJson += "\"neTypeId\":\"" + databasesType + "\",";
        dbJson += "\"properties\":" + databasesProperties;
        dbJson += "}";

        return RestUtils.getPostResponseBody(url, dbJson, 200);
    }

    public static String createOSNe(RegisterInfo registerInfo){
        Map<String, String> osTagsMap = new HashMap<String, String>();
        osTagsMap.put("dahu", "www");
        String osTags = JsonUtils.simpleMapToJsonStr(osTagsMap);

        Map<String, String> osParams = new HashMap<String, String>();
        osParams.put("ip", registerInfo.getUrl());
        osParams.put("protocol", registerInfo.getConnectType());
        osParams.put("port", String.valueOf(registerInfo.getHostPort()));
        osParams.put("username", registerInfo.getHostUsername());
        osParams.put("password", registerInfo.getHostPassword());
        String osProperties = JsonUtils.simpleMapToJsonStr(osParams);

        String osNeType = "os.linux";

        String osJson = "{";
        osJson += "\"tags\":" + osTags + ",";
        osJson += "\"neTypeId\":\"" + osNeType + "\",";
        osJson += "\"properties\":" + osProperties;
        osJson += "}";

        return RestUtils.getPostResponseBody(url, osJson, 200);
    }
}
