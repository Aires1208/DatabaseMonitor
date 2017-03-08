package com.zte.ums.zenap.itm.agent.common.bean;

public class NeModel {
	private String neTypeId;
	private Metric[] metrics;
	public String getNeTypeId() {
		return neTypeId;
	}
	public void setNeTypeId(String neTypeId) {
		this.neTypeId = neTypeId;
	}
	public Metric[] getMetrics() {
		return metrics;
	}
	public void setMetrics(Metric[] metrics) {
		this.metrics = metrics;
	}
	
}
