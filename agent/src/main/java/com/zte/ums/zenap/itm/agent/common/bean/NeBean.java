package com.zte.ums.zenap.itm.agent.common.bean;

import java.util.Properties;

public class NeBean {
	private String neId;
	private String neTypeId;
	private Properties properties;
	private Properties tags;
	public String getNeId() {
		return neId;
	}
	public void setNeId(String neId) {
		this.neId = neId;
	}
	public String getNeTypeId() {
		return neTypeId;
	}
	public void setNeTypeId(String neTypeId) {
		this.neTypeId = neTypeId;
	}
	public Properties getProperties() {
		return properties;
	}
	public void setProperties(Properties properties) {
		this.properties = properties;
	}
	public Properties getTags() {
		return tags;
	}
	public void setTags(Properties tags) {
		this.tags = tags;
	}
	
}
