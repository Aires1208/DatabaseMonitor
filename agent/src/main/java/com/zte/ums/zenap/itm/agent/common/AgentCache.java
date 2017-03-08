package com.zte.ums.zenap.itm.agent.common;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.yaml.snakeyaml.Yaml;

import com.zte.ums.zenap.itm.agent.common.bean.Measurement;
import com.zte.ums.zenap.itm.agent.common.bean.NeModel;
import com.zte.ums.zenap.itm.agent.task.TaskInfo;

public class AgentCache {
	private static final Logger LOGGER = LoggerFactory.getLogger(AgentCache.class);
	private static ConcurrentHashMap<Integer, String> taskIdToFileMap = new ConcurrentHashMap<Integer, String>();
	private static ConcurrentHashMap<String, ConcurrentHashMap<Integer, TaskInfo>> fileTaskIdMap = new ConcurrentHashMap<String, ConcurrentHashMap<Integer, TaskInfo>>();
	private static ConcurrentHashMap<String, NeModel> neTypeModelMap = new ConcurrentHashMap<String, NeModel>();
	private static ConcurrentHashMap<String, List<Integer>> neIdToTaskIdMap = new ConcurrentHashMap<String, List<Integer>>();
	private static String apiTaskFilePath;
	private static Yaml yaml = new Yaml();
	
	public static String getFilePathByTaskId(int taskId)
	{
		return taskIdToFileMap.get(taskId);
	}
	
	public static void cacheTask(String filePath, int taskId, TaskInfo task)
	{
		if (!fileTaskIdMap.containsKey(filePath))
		{
			ConcurrentHashMap<Integer, TaskInfo> taskIdMap = new ConcurrentHashMap<Integer, TaskInfo>();
			fileTaskIdMap.put(filePath, taskIdMap);
		}
		fileTaskIdMap.get(filePath).put(taskId, task);
		taskIdToFileMap.put(taskId, filePath);
	}
	
	public static TaskInfo getTaskByTaskId(int taskId)
	{
		String filePath = taskIdToFileMap.get(taskId);
		return fileTaskIdMap.get(filePath).get(taskId);
	}
	
	public static TaskInfo[] getAllTasksByFilePath(String filePath)
	{
		return fileTaskIdMap.get(filePath).values().toArray(new TaskInfo[0]);
	}

	public static void removeTask(int taskId)
	{
		String filePath = taskIdToFileMap.get(taskId);
		fileTaskIdMap.get(filePath).remove(taskId);
		taskIdToFileMap.remove(taskId);
	}
	
	public static void cacheNeModel(String neTypeId, NeModel model)
	{
		neTypeModelMap.put(neTypeId, model);
	}
	
	public static NeModel getNeModeByType(String neTypeId)
	{
		return neTypeModelMap.get(neTypeId);
	}
	

	public static synchronized void updateTaskListFile(String filePath)
	{
		try {
			Measurement measurement = new Measurement();
			measurement.setCollectTask(getAllTasksByFilePath(filePath));
			yaml.dump(measurement, new FileWriter(new File(filePath)));
		} catch (IOException e) {
			LOGGER.warn("Update TaskList file failed!", e);
		}
	}
	
	public static void cacheNeIdTaskId(String neId, int taskId)
	{
		if (!neIdToTaskIdMap.containsKey(neId))
		{
			List<Integer> taskIds = new ArrayList<Integer>();
			neIdToTaskIdMap.put(neId, taskIds);
		}
		neIdToTaskIdMap.get(neId).add(taskId);
	}
	
	public static List<Integer> getTaskIdByNeId(String neId)
	{
		return neIdToTaskIdMap.get(neId);
	}
	
	public static void deleteTaskIdByNeId(String neId)
	{
		neIdToTaskIdMap.remove(neId);
	}

	public static String getApiTaskFilePath() {
		return apiTaskFilePath;
	}

	public static void setApiTaskFilePath(String apiTaskFilePath) {
		AgentCache.apiTaskFilePath = apiTaskFilePath;
	}

	
}
