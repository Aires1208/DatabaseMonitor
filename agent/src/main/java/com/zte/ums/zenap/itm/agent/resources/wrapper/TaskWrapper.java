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

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ws.rs.core.Response;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.zte.ums.zenap.itm.agent.common.AgentCache;
import com.zte.ums.zenap.itm.agent.common.util.AgentUtil;
import com.zte.ums.zenap.itm.agent.dataaq.common.DataAcquireException;
import com.zte.ums.zenap.itm.agent.task.TaskInfo;
import com.zte.ums.zenap.itm.agent.task.TaskService;

/**
 * Task rest interface processing class
 */
public class TaskWrapper {
    private static final Logger LOGGER = LoggerFactory.getLogger(TaskWrapper.class);

    /**
     * Create data acquisition task
     * @param taskBean task detail
     * @return status code 201(success) or 500(fail)
     */
    public static Response taskCreate(TaskInfo taskInfo) {
//        LOGGER.info("Receive create task request.taskBean:" + AgentUtil.convertBeanToJson(taskBean));
    	if (taskInfo.getTaskId() == -1)
    	{
    		taskInfo.setTaskId(AgentUtil.getTaskId());
    	}
        if (!TaskService.isTaskExist(taskInfo.getTaskId())) {
	    	try {
	            TaskService.taskCreate(taskInfo);
				AgentCache.cacheTask(AgentCache.getApiTaskFilePath(), taskInfo.getTaskId(), taskInfo);
				AgentCache.cacheNeIdTaskId(taskInfo.getNeId(), taskInfo.getTaskId());
				AgentCache.updateTaskListFile(AgentCache.getApiTaskFilePath());
	        } catch (DataAcquireException e) {
	            LOGGER.error("create task fail.", e);
	            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(e.getMessage()).build();
	        }  
    	}
        return Response.status(Response.Status.CREATED).build();
    }

    /**
     * Modify data acquisition task
     * @param taskId the task id
     * @param taskBean task detail
     * @return status code 201(success) or 500(fail)
     */
    public static Response taskGranularityModify(int taskId, int granularity){
//        LOGGER.info("Receive modify task request.taskId:" + taskId + " taskBean:" + AgentUtil.convertBeanToJson(taskBean));
        try {
        	TaskInfo oTaskInfo = AgentCache.getTaskByTaskId(taskId);
        	oTaskInfo.setGranularity(granularity);
            TaskService.taskModify(taskId, oTaskInfo);
            AgentCache.updateTaskListFile(AgentCache.getFilePathByTaskId(taskId));
        } catch (DataAcquireException e) {
            LOGGER.error("modify task fail.", e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(e.getMessage()).build();
        }
        return Response.status(Response.Status.CREATED).build();
    }

    /**
     * Delete data acquisition task
     * @param taskId the task id
     * @return status code 204(success) or 500(fail)
     */
    public static Response taskDelete(int taskId) {
        LOGGER.info("Receive delete task request.taskId:" + taskId);
        try {
            TaskService.taskDelete(taskId);
            String filePath = AgentCache.getFilePathByTaskId(taskId);
            AgentCache.removeTask(taskId);
            AgentCache.updateTaskListFile(filePath);
        } catch (DataAcquireException e) {
            LOGGER.error("delete task fail.", e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(e.getMessage()).build();
        }
        return Response.status(Response.Status.NO_CONTENT).build();
    }
    
    /**
     * Create data acquisition task
     * @param taskBean task detail
     * @return status code 201(success) or 500(fail)
     */
    public static Map<String, List<String>> taskExecute(TaskInfo taskBean) {
//        LOGGER.info("Receive task execute request.taskBean:" + AgentUtil.convertBeanToJson(taskBean));
        try {
            return TaskService.taskExecute(taskBean);
        } catch (DataAcquireException e) {
            LOGGER.error("execute task fail.", e);
            return new HashMap<String, List<String>>();
        }
    }

}
