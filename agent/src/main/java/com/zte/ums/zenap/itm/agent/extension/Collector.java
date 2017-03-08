package com.zte.ums.zenap.itm.agent.extension;

import com.zte.ums.zenap.itm.agent.dataaq.monitor.bean.common.MonitorTaskInfo;


public abstract class Collector implements ICollector {

	protected MonitorTaskInfo taskInfo = null;
	
	@Override
	public void setMonitorTaskInfo(MonitorTaskInfo taskInfo)
	{
		this.taskInfo = taskInfo;
	}
}
