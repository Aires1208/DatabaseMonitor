package com.zte.ums.zenap.itm.agent.extension;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.zte.ums.zenap.itm.agent.common.AgentConst;
import com.zte.ums.zenap.itm.agent.common.bean.DataBean;
import com.zte.ums.zenap.itm.agent.common.util.filescan.FastFileSystem;

public abstract class DataProcessor implements IDataProcessor {
	private static final Logger LOGGER = LoggerFactory.getLogger(DataProcessor.class);

	private Properties properties;
	
	@Override
	public void process(DataBean data) {
		loadProperties();
		String enable = properties.getProperty(AgentConst.ENABLE);
		if (enable != null && !AgentConst.TRUE.equals(enable))
		{
			return;
		}
		process(data, properties);
	}
	
	private synchronized void loadProperties() {
		
		if (properties == null) {
			File file = FastFileSystem.getFile(AgentConst.PROCESSORDIR,
					propertiesFileName());
			Properties prop = new Properties();
			try {
				FileInputStream is = new FileInputStream(file.getAbsolutePath());
				prop.load(is);
				is.close();
				properties = prop;
			} catch (IOException e) {
				LOGGER.warn("Can't find properties file:" + file.getName());
				properties = new Properties();
				properties.put(AgentConst.ENABLE, AgentConst.TRUE);
			}
		}
	}

	abstract public void process(DataBean data, Properties properties);
	abstract public String propertiesFileName();
}
