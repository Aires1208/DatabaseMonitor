package com.zte.ums.zenap.itm.agent.task;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.yaml.snakeyaml.Yaml;

import com.zte.ums.zenap.itm.agent.common.AgentCache;
import com.zte.ums.zenap.itm.agent.common.AgentConst;
import com.zte.ums.zenap.itm.agent.common.bean.Measurement;
import com.zte.ums.zenap.itm.agent.common.util.AgentUtil;
import com.zte.ums.zenap.itm.agent.common.util.GeneralFileLocaterImpl;
import com.zte.ums.zenap.itm.agent.common.util.filescan.FastFileSystem;
import com.zte.ums.zenap.itm.agent.dataaq.common.DataAcquireException;

public class TaskInit {
    private static final Logger LOGGER = LoggerFactory.getLogger(TaskInit.class);
    public final static String API_TASK_FILENAME = "api.task";
    public final static String TASK_FILE_PATTERN = "*.task";
    private static String neId;
	public static void init()
	{
		neId = UUID.randomUUID().toString();
        StringBuilder apiFilePath = new StringBuilder(GeneralFileLocaterImpl.getGeneralFileLocater().getConfigPath());
        apiFilePath.append(File.separator);
        apiFilePath.append(AgentConst.TASKDIR);
        apiFilePath.append(File.separator);
        apiFilePath.append(API_TASK_FILENAME);
        AgentCache.setApiTaskFilePath(new File(apiFilePath.toString()).getAbsolutePath());
        File[] taskFiles = FastFileSystem.getFiles(TASK_FILE_PATTERN);
        
        // load task which init by taskFile
        if (taskFiles != null)
        {
        	for (File taskFile : taskFiles)
        	{
        		loadTaskFile(taskFile);
        	}
        }
	}
	
	private static void loadTaskFile(File taskFile)
	{
    	Yaml yaml = new Yaml();
    	LOGGER.info("load task file:" + taskFile.getName());
    	try {
			Measurement measurement = yaml.loadAs(new FileInputStream(taskFile), Measurement.class);
			if (measurement != null)
			{
				TaskInfo[] cTask = measurement.getCollectTask();
		        LOGGER.info("Init task size:" + cTask.length);
		        for (TaskInfo task : cTask)
		        {
		        	if (task.getNeId() == null)
		        	{
		        		task.setNeId(neId);
		        	}
		        	if (task.getTaskId() == -1)
		        	{
		        		task.setTaskId(AgentUtil.getTaskId());
		        	}
		        	AgentCache.cacheTask(taskFile.getAbsolutePath(), task.getTaskId(), task);
		            try {
		                TaskService.taskCreate(task);
		            } catch (DataAcquireException e) {
		                LOGGER.error("create task fail.", e);
		            }
		            AgentCache.cacheNeIdTaskId(task.getNeId(), task.getTaskId());
		            AgentUtil.setMaxTaskId( task.getTaskId());
		        }
		        AgentCache.updateTaskListFile(taskFile.getAbsolutePath());
			}
		} catch (IOException e) {
			LOGGER.warn("load task file failed!", e);
		}
	}
}
