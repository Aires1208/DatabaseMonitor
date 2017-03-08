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
package com.zte.ums.zenap.itm.agent.datarp;

import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.zte.ums.zenap.itm.agent.cometd.server.CometdService;
import com.zte.ums.zenap.itm.agent.common.bean.DataBean;
import com.zte.ums.zenap.itm.agent.common.util.ExtensionUtil;
import com.zte.ums.zenap.itm.agent.extension.IDataProcessor;

public class DataMsgHandleThread implements Runnable {
    private static final Logger LOGGER = LoggerFactory.getLogger(DataMsgHandleThread.class);
    private List<Object> taskParaList = null;

    public DataMsgHandleThread(List<Object> taskParaList) {
        this.taskParaList = taskParaList;

    }

    @Override
    public void run() {
        if (taskParaList.size() != 0) {
            int taskId = (Integer) taskParaList.get(0);
            String metricId = (String) taskParaList.get(1);
            String neId = (String) taskParaList.get(2);
            Properties tags = (Properties)taskParaList.get(3);
            Date collectTime = (Date) taskParaList.get(4);
            int granularity = (Integer) taskParaList.get(5);
            String dqResult = (String) taskParaList.get(6);
            Map<String, List<String>> result = (Map) taskParaList.get(7);
            if (dqResult.equalsIgnoreCase("success") && result.size() != 0)
            {
	            try {
	                DataBean data = new DataBean();
	                data.setTaskId(taskId);
	                data.setMetricId(metricId);
	                data.setNeId(neId);
	                data.setTags(tags);
	                data.setCollectTime(collectTime.getTime());
	                data.setGranularity(granularity);
	                data.setResult(dqResult);
	                List<Properties> values = new ArrayList<Properties>();
	                Iterator<Entry<String, List<String>>> it = result.entrySet().iterator();
	                while (it.hasNext())
	                {
	                	Entry<String, List<String>> entry = it.next();
	                	List<String> v_value = entry.getValue();
	                	adjustPropertiesList(values, v_value.size());
	                	String columnName = entry.getKey();
	                    int i = 0;
	                    for (String value : v_value) {
	                    	values.get(i++).put(columnName, value == null ? "0" : value);
	                    }
	                }
	                data.setValues(values.toArray(new Properties[0]));
	    			CometdService.getInstance().publish(CometdService.PM_UPLOAD_CHANNEL, data);
	                Object[] dataProcess = ExtensionUtil.getInstances(IDataProcessor.EXTENSIONID, IDataProcessor.KEY);
	                if (dataProcess != null)
	                {
		        		for (Object pmDataConsumer : dataProcess) {			
		        			((IDataProcessor)pmDataConsumer).process(data);
		        		}
	                }
	            } catch (Throwable e) {
	                LOGGER.warn(e.getMessage(), e);
	            }
            }
            else
            {
            	LOGGER.warn("TaskId:" + taskId + " has no data in resultMap!");
            }
            
        } else {
            LOGGER.warn("taskParaList has no data");
        }
    }
    private void adjustPropertiesList(List<Properties> values, int size)
    {
    	if (values.size() < size)
    	{
    		values.add(new Properties());
    		adjustPropertiesList(values, size);
    	}
    }
}
