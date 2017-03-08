/**
 * Copyright (C) 2015 ZTE, Inc. and others. All rights reserved. (ZTE)
 * <p/>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p/>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p/>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.zte.ums.zenap.itm.agent.resources.wrapper;

import java.util.List;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.zte.ums.zenap.itm.agent.common.AgentCache;
import com.zte.ums.zenap.itm.agent.common.AgentConst;
import com.zte.ums.zenap.itm.agent.common.bean.Metric;
import com.zte.ums.zenap.itm.agent.common.bean.NeBean;
import com.zte.ums.zenap.itm.agent.common.bean.NeModel;
import com.zte.ums.zenap.itm.agent.common.bean.NeResult;
import com.zte.ums.zenap.itm.agent.common.util.AgentUtil;
import com.zte.ums.zenap.itm.agent.dataaq.common.DataAcquireException;
import com.zte.ums.zenap.itm.agent.task.TaskInfo;
import com.zte.ums.zenap.itm.agent.task.TaskService;

/**
 * Task rest interface processing class
 */
public class NeWrapper {
    private static final Logger LOGGER = LoggerFactory.getLogger(NeWrapper.class);

	public static NeResult neCreate(NeBean neInfo) {
		NeResult result = new NeResult();

		if (neInfo.getNeId() == null)
		{
			neInfo.setNeId( UUID.randomUUID().toString());
		}
		LOGGER.info("Add ne:" + AgentUtil.convertBeanToJson(neInfo));
		NeModel model = AgentCache.getNeModeByType(neInfo.getNeTypeId());
		if (model == null)
		{
			LOGGER.warn("Create ne task faile, ne model not exist:" + neInfo.getNeTypeId());
			result.setResult(AgentConst.FAIL);
			result.setInfo("Create ne task faile, ne model not exist:" + neInfo.getNeTypeId());
			return result;
		}
		Metric[] metrics = model.getMetrics();
		for (Metric metric : metrics)
		{
			TaskInfo taskInfo = new TaskInfo();
			taskInfo.setNeId(neInfo.getNeId());
			taskInfo.setGranularity(metric.getGranularity());
			taskInfo.setMetricId(metric.getMetricId());
			taskInfo.setProperties(neInfo.getProperties());
			taskInfo.setTags(neInfo.getTags());
			taskInfo.setTaskId(AgentUtil.getTaskId());

			try {
				TaskService.taskCreate(taskInfo);
			} catch (DataAcquireException e) {
				LOGGER.warn("Create ne task faile! ", e);
				result.setResult(AgentConst.FAIL);
				result.setInfo(neInfo.getNeId() + "create ne task failed.");
				return result;
			}
			AgentCache.cacheTask(AgentCache.getApiTaskFilePath(), taskInfo.getTaskId(), taskInfo);
			AgentCache.cacheNeIdTaskId(taskInfo.getNeId(), taskInfo.getTaskId());
		}
		AgentCache.updateTaskListFile(AgentCache.getApiTaskFilePath());
		result.setResult(AgentConst.SUCCESS);
		result.setNeId(neInfo.getNeId());
		result.setInfo("create ne task success.");
		return result;
	}

	public static NeResult neModify(String neId, NeBean neInfo) {

		NeResult result = new NeResult();
		List<Integer> taskIds = AgentCache.getTaskIdByNeId(neId);
		if (taskIds != null)
		{
			for (int taskId : taskIds)
			{
				try {
					TaskInfo taskInfo = AgentCache.getTaskByTaskId(taskId);
					taskInfo.setProperties(neInfo.getProperties());
					taskInfo.setTags(neInfo.getTags());
					TaskService.taskModify(taskId, taskInfo);
				} catch (DataAcquireException e) {
					LOGGER.warn("Modify ne task faile! ", e);
					result.setResult(AgentConst.FAIL);
					result.setInfo(neId + "Modify ne task failed.");
					return result;
				}
			}
			AgentCache.updateTaskListFile(AgentCache.getApiTaskFilePath());
			result.setResult(AgentConst.SUCCESS);
			result.setNeId(neId);
			result.setInfo("Modify ne task success.");
		}
		return result;
	}

	public static NeResult neDelete(String neId) {
		NeResult result = new NeResult();
		LOGGER.info("Delete ne:" + neId);
		List<Integer> taskIds = AgentCache.getTaskIdByNeId(neId);
		if (taskIds != null)
		{
			for (int taskId : taskIds)
			{
				try {
					TaskService.taskDelete(taskId);
					AgentCache.removeTask(taskId);
				} catch (DataAcquireException e) {
					LOGGER.warn("Delete ne task faile! ", e);
					result.setResult(AgentConst.FAIL);
					result.setInfo(neId + "Delete ne task failed.");
					return result;
				}
			}
			AgentCache.deleteTaskIdByNeId(neId);
			AgentCache.updateTaskListFile(AgentCache.getApiTaskFilePath());
			result.setResult(AgentConst.SUCCESS);
			result.setNeId(neId);
			result.setInfo("Delete ne task success.");
		}
		return result;
	}

}
