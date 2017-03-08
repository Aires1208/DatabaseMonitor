package com.zte.ums.zenap.itm.agent.common.bean;

public class Queue {

	private String queueName;
	private String coreThreadNum;
	private String maxQueueSize;
	private String maxThreadNum;
	private String runCollectPeriod;
	public String getQueueName() {
		return queueName;
	}
	public void setQueueName(String queueName) {
		this.queueName = queueName;
	}
	
	public String getCoreThreadNum() {
		return coreThreadNum;
	}
	public void setCoreThreadNum(String coreThreadNum) {
		this.coreThreadNum = coreThreadNum;
	}
	public String getMaxQueueSize() {
		return maxQueueSize;
	}
	public void setMaxQueueSize(String maxQueueSize) {
		this.maxQueueSize = maxQueueSize;
	}
	public String getMaxThreadNum() {
		return maxThreadNum;
	}
	public void setMaxThreadNum(String maxThreadNum) {
		this.maxThreadNum = maxThreadNum;
	}
	public String getRunCollectPeriod() {
		return runCollectPeriod;
	}
	public void setRunCollectPeriod(String runCollectPeriod) {
		this.runCollectPeriod = runCollectPeriod;
	}

	

}
