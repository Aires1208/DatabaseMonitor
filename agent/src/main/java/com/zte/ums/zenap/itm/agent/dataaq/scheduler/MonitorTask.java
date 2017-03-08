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
package com.zte.ums.zenap.itm.agent.dataaq.scheduler;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import com.zte.ums.zenap.itm.agent.plugin.linux.OsLinuxCollector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.zte.ums.zenap.itm.agent.common.AgentConst;
import com.zte.ums.zenap.itm.agent.common.util.AgentUtil;
import com.zte.ums.zenap.itm.agent.common.util.ExtensionUtil;
import com.zte.ums.zenap.itm.agent.dataaq.common.DataAcquireException;
import com.zte.ums.zenap.itm.agent.dataaq.monitor.bean.common.MonitorTaskInfo;
import com.zte.ums.zenap.itm.agent.extension.ICollector;

/**
 * This class inherits the TimerTask used to perform specific monitoring tasks.
 */
public class MonitorTask implements Runnable {
	private static final Logger LOGGER = LoggerFactory.getLogger(MonitorTask.class);

	private MonitorTaskInfo monitorTaskInfo = null;
	private String metricId = null;
	private Date reportTime;

	public MonitorTask(MonitorTaskInfo monitorTaskInfo) {
		this.monitorTaskInfo = monitorTaskInfo;
		metricId = (String) monitorTaskInfo.getMetricId();
	}

	@Override
	public void run() {
		long timerRunStart = System.currentTimeMillis();
		Map<String, List<String>> result;
		List<Object> vPara = new ArrayList<Object>();
		vPara.add(this.monitorTaskInfo.getJobId());
		vPara.add(this.monitorTaskInfo.getMetricId());
		vPara.add(this.monitorTaskInfo.getNeId());
		vPara.add(this.monitorTaskInfo.getTags());
		vPara.add(getReportTime());
		vPara.add(this.monitorTaskInfo.getGranularity());
		try {
			result = perform();		
			vPara.add(AgentConst.SUCCESS);
		} catch (DataAcquireException e) {
			if (e.getErrorCode() == AgentConst.ERRORCODE_NOTUPONDATA)
			{
				LOGGER.info("TaskId: " + monitorTaskInfo.getJobId() + " Gathering Data not report. " + e.getMessage());
				return;
			}
			LOGGER.warn("TaskId: " + monitorTaskInfo.getJobId() + " Gathering Data Failed. " + e.getMessage());
			result = new HashMap<String, List<String>>();
			vPara.add(AgentConst.FAIL);
		}
		vPara.add(result);
		AgentUtil.putDataMsg(vPara);
		long timerRunEnd = System.currentTimeMillis();
		LOGGER.debug("TaskId: " + monitorTaskInfo.getJobId() + "; MonitorTask---timerRunPeriod = "
				+ (timerRunEnd - timerRunStart));
	}

	/**
	 * Perform data collection
	 * 
	 * @return Data acquisition result
	 * @throws DataAcquireException
	 */
	
	public Map<String, List<String>> perform() throws DataAcquireException {

		String startTime = AgentUtil.timeFormat(new Date());
		Map<String, List<String>> result = new HashMap<String, List<String>>();

		ICollector monitor = createMonitor();
		Properties paras = monitorTaskInfo.getMonitorProperty();
        StringBuffer sb = new StringBuffer();
        sb.append("Taskid: ").append(monitorTaskInfo.getJobId()).append(" reportTime ").append(reportTime).append(" execution ").append(startTime).append(
            " New monitor perform: metricId: ").append(metricId);
        LOGGER.info(sb.toString());

		result = monitor.perform(paras);
		if (result == null) {
			LOGGER.info("TaskId: " + monitorTaskInfo.getJobId() + " execution " + startTime + " aborted.");
			throw new DataAcquireException(AgentConst.ERRORCODE_PROVIDERS,
					"CollectTask  [" + monitorTaskInfo.getJobId() + "] fail. monitorId=" + metricId);
		}

		LOGGER.info("TaskId: " + monitorTaskInfo.getJobId() + " execution " + startTime + " completed.");
		return result;
	}
    
    private ICollector createMonitor() {
        ICollector monitor;
        monitor = (ICollector)ExtensionUtil.getInstance(ICollector.EXTENSIONID, metricId);
        if (monitor != null)
        {
        	monitor.setMonitorTaskInfo(monitorTaskInfo);
        }
        return monitor;
    }


	/**
	 * get the data report time
	 * 
	 * @return data report time
	 */
	private synchronized Date getReportTime() {
		long times = reportTime.getTime();
		Date time = new Date(times);
		reportTime = new Date(times + monitorTaskInfo.getGranularity() * 1000);
		return time;
	}

	/**
	 * init data report time
	 * 
	 * @param initTime
	 */
	public void initReportTime(Date initTime) {
		if (reportTime == null) {
			reportTime = initTime;
		}
	}
}
