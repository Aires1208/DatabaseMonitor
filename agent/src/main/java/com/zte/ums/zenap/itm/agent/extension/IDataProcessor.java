package com.zte.ums.zenap.itm.agent.extension;

import java.util.Properties;

import com.zte.ums.zenap.itm.agent.common.bean.DataBean;

public interface IDataProcessor {
	public static final String EXTENSIONID = "com.zte.ums.zenap.itm.agent.extension.IDataProcessor";
	public static final String KEY = "IDataProcess";
	public void process(DataBean data);
}
