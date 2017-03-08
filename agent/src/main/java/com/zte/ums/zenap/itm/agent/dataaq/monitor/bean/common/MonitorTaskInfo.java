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
package com.zte.ums.zenap.itm.agent.dataaq.monitor.bean.common;

import java.util.Properties;

/**
 * Monitor task details
 */

public class MonitorTaskInfo{

    private int taskId = 0;
    private String neId;
    private int granularity;
    private String metricId;
    private Properties monitorProperty;
    private Properties tags;
	public String cachedMessage;
	public int getJobId() {
		return taskId;
	}
	public void setJobId(int taskId) {
		this.taskId = taskId;
	}
	public int getGranularity() {
		return granularity;
	}
	public void setGranularity(int granularity) {
		this.granularity = granularity;
	}
	public String getMetricId() {
		return metricId;
	}
	public void setMetricId(String metricId) {
		this.metricId = metricId;
	}
	public Properties getMonitorProperty() {
		return monitorProperty;
	}
	public void setMonitorProperty(Properties monitorProperty) {
		this.monitorProperty = monitorProperty;
	}
	public Properties getTags() {
		return tags;
	}
	public void setTags(Properties tags) {
		this.tags = tags;
	}
	public String getNeId() {
		return neId;
	}
	public void setNeId(String neId) {
		this.neId = neId;
	}


        
}

	
