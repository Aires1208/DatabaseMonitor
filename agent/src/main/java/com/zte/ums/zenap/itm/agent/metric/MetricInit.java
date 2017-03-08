package com.zte.ums.zenap.itm.agent.metric;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.yaml.snakeyaml.Yaml;

import com.zte.ums.zenap.itm.agent.common.AgentCache;
import com.zte.ums.zenap.itm.agent.common.bean.NeModel;
import com.zte.ums.zenap.itm.agent.common.util.filescan.FastFileSystem;

public class MetricInit {
    private static final Logger LOGGER = LoggerFactory.getLogger(MetricInit.class);
    public final static String METRIC_FILE_PATTERN = "*.metric";
    
	public static void init()
	{
        Yaml yaml = new Yaml();
        File[] files = FastFileSystem.getFiles(METRIC_FILE_PATTERN);
        LOGGER.info("Metric file size:" + files.length);
        for (File file : files)
        {
	    	LOGGER.info("load metric file:" + file.getName());
	    	try {
	    		NeModel model = yaml.loadAs(new FileInputStream(file), NeModel.class);
	    		AgentCache.cacheNeModel(model.getNeTypeId(), model);
			} catch (IOException e) {
				LOGGER.warn("load metric file failed!", e);
			}
        }
	}
}
