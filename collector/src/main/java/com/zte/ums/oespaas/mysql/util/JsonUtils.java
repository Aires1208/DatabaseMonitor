package com.zte.ums.oespaas.mysql.util;

import java.util.Map;
import java.util.Set;

/**
 * Created by 10203846 on 11/11/16.
 */
public class JsonUtils {
    public static String simpleMapToJsonStr(Map<String, String> map) {
        if (map == null || map.isEmpty()) {
            return "{}";
        }

        String jsonStr = "{";
        for (String key : map.keySet()) {
            jsonStr += "\"" + key + "\":\"" + map.get(key) + "\",";
        }

        jsonStr = jsonStr.substring(0, jsonStr.length() - 1);
        jsonStr += "}";

        return jsonStr;
    }
}
