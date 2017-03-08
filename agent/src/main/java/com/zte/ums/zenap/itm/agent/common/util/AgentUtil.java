/**
 * Copyright (C) 2015 ZTE, Inc. and others. All rights reserved. (ZTE)
 * <p/>
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 * <p/>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p/>
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */
package com.zte.ums.zenap.itm.agent.common.util;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import com.google.gson.Gson;
import com.zte.ums.zenap.itm.agent.dataaq.scheduler.MonitorTask;
import com.zte.ums.zenap.itm.agent.dataaq.scheduler.MonitorTaskQueue;
import com.zte.ums.zenap.itm.agent.datarp.DataMsgQueue;

public class AgentUtil {
    private static DataMsgQueue dataMsgQueue = null;
	private static MonitorTaskQueue monitorTaskQueue = null;

    private static SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm:ss", Locale.ENGLISH);
    private static int taskId = 1;
    
    public static void initQueue()
    {
    	// init upload data thread
        dataMsgQueue = new DataMsgQueue();
        dataMsgQueue.start();
        
        // init monitor task thread
        monitorTaskQueue = new MonitorTaskQueue();
        monitorTaskQueue.start();
    }
    public static String timeFormat(Date date) {
        return timeFormat.format(date);
    }
    
    public static String replace(String formula, String variable, String value) {
        String result = "";
        int index = formula.indexOf(variable);
        if (index != 0 && index != -1) {
            result = result + formula.substring(0, index);
        }
        result = result + value;
        result = result + formula.substring(index + variable.length(), formula.length());
        return result;
    }

    
	public static void putMonitorTaskQueue(MonitorTask task){
		monitorTaskQueue.put(task);
	}

    public static void putDataMsg(List<Object> msg) {
        dataMsgQueue.put(msg);
    }


    public static String convertBeanToJson(Object o) {
        if (o == null) return "";
        Gson gson = new Gson();
        String str = gson.toJson(o);
        return str;
    }
    
	
	public synchronized static int getTaskId()
	{
		return taskId++;
	}
	
	public static int getMaxTaskId()
	{
		return taskId;
	}
	
	public synchronized static void setMaxTaskId(int maxTaskId)
	{
		if (maxTaskId > taskId)
		{
			taskId = maxTaskId;
		}
	}
}
