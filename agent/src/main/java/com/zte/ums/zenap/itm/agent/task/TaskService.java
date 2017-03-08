/**
 * Copyright (C) 2015 ZTE, Inc. and others. All rights reserved. (ZTE)
 * <p/>
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 * <p/>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p/>
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */
package com.zte.ums.zenap.itm.agent.task;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;

import org.quartz.JobKey;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.zte.ums.zenap.itm.agent.dataaq.common.DataAcquireException;
import com.zte.ums.zenap.itm.agent.dataaq.monitor.bean.common.MonitorTaskInfo;
import com.zte.ums.zenap.itm.agent.dataaq.scheduler.MonitorTask;
import com.zte.ums.zenap.itm.agent.dataaq.scheduler.MonitorTaskRunnerThread;
import com.zte.ums.zenap.itm.agent.task.timer.TimerException;
import com.zte.ums.zenap.itm.agent.task.timer.TimerService;
import com.zte.ums.zenap.itm.agent.task.timer.TimerTask;

public class TaskService {
    private static final Logger LOGGER = LoggerFactory.getLogger(TaskService.class);
    /**
     * cache all task timer thread with task id. key->task id, value->the timer thread
     */
    private static ConcurrentHashMap<Integer, String> taskIdToKeyMap = new ConcurrentHashMap<Integer, String>();
    
    private static ConcurrentHashMap<Integer, MonitorTask> taskIdToTaskInfo = new ConcurrentHashMap<Integer, MonitorTask>();

    public static boolean isTaskExist(int taskId)
    {
    	return (taskIdToTaskInfo.get(taskId) != null);
    }
    
    /**
     * Create monitor task
     *
     * @param taskInfo task info
     * @throws DataAcquireException
     */
    public static void taskCreate(TaskInfo taskInfo) throws DataAcquireException {
        MonitorTaskInfo monitorTaskInfo = convertTaskInfo(taskInfo);
        createMonitorTask(monitorTaskInfo);
    }

    /**
     * Modify monitor task
     * @param taskId task id
     * @param taskInfo task info
     * @throws DataAcquireException
     */
    public static void taskModify(int taskId, TaskInfo taskInfo) throws DataAcquireException {
        MonitorTaskInfo monitorTaskInfo = convertTaskInfo(taskInfo);
        modifyMonitorTask(taskId, monitorTaskInfo);
    }

    /**
     * Delete monitor task
     * @param taskId task id
     * @throws DataAcquireException
     */
    public static void taskDelete(int taskId) throws DataAcquireException {
        deleteMonitorTask(taskId);
    }

    
    /**
     * execute monitor task at once
     *
     * @param taskInfo task info
     * @throws DataAcquireException
     */
    public static Map<String, List<String>> taskExecute(TaskInfo taskInfo) throws DataAcquireException {
        MonitorTaskInfo monitorTaskInfo = convertTaskInfo(taskInfo);
        MonitorTask monitorTask = new MonitorTask(monitorTaskInfo);
        return monitorTask.perform();
    }
    
    /**
     * Create monitor task for acquire data
     *
     * @param taskInfo task info
     * @throws DataAcquireException
     */
    private static void createMonitorTask(MonitorTaskInfo taskInfo) throws DataAcquireException {
        if (taskIdToTaskInfo.get(taskInfo.getJobId()) != null) {
            return;// ignore.the task is running
        }
        activateMonitorTask(taskInfo);
    }

    /**
     * modify monitor task
     *
     * @param taskId   the task id
     * @param taskInfo the task info
     * @throws DataAcquireException
     */
    private static void modifyMonitorTask(int taskId, MonitorTaskInfo taskInfo)
            throws DataAcquireException {
        deleteMonitorTask(taskId);// delete the old task
        activateMonitorTask(taskInfo);// active the new task
    }

    /**
     * delete monitor task by task id
     *
     * @param taskId the task id
     */
    private static void deleteMonitorTask(int taskId) {
    	String key = taskIdToKeyMap.get(taskId);
    	MonitorTask monitorTask = taskIdToTaskInfo.get(taskId);
    	MonitorTaskRunnerThread runner = MonitorTaskRunnerThread.getRunner(key);
		if (runner == null)
		{
			LOGGER.info("deleteMonitorTask: monitorTaskList is not exist");
			return;
		}
		if (runner.containsMonitorTask(monitorTask))
		{
			runner.removeMonitorTask(monitorTask); // 删除任务列表中的任务
            taskIdToKeyMap.remove(taskId);
            taskIdToTaskInfo.remove(taskId);
            LOGGER.info("taskId=" + taskId + " has deleted.");
			// 当任务列表被清空了, 删除定时器对应的定时任务
			if (runner.isMonitorTaskEmpty())
			{
				JobKey timerJobKey = runner.getTimeJobKey();
				try
				{
					TimerService.removeTask(timerJobKey);
					MonitorTaskRunnerThread.removeRunner(key);

					LOGGER.info("Delete monitor timerTask:" + timerJobKey);
				}
				catch (TimerException e)
				{
					LOGGER.warn("Delete Task TimerException: operate timer task exception");
				}
			}
		}
    }

    /**
     * Delete all running monitor tasks
     */
    public static void deleteAllMonitorTask() {
        for (Integer jobId : taskIdToTaskInfo.keySet()) {
            deleteMonitorTask(jobId);
        }
    }

    /**
     * Convert TaskBean object to MonitorTaskInfo object
     *
     * @param taskBean TaskBean object
     * @return MonitorTaskInfo object
     */
    private static MonitorTaskInfo convertTaskInfo(TaskInfo taskBean) {

        
        MonitorTaskInfo monitorTaskInfo = new MonitorTaskInfo();
        monitorTaskInfo.setJobId(taskBean.getTaskId());
        monitorTaskInfo.setNeId(taskBean.getNeId());
        monitorTaskInfo.setGranularity(taskBean.getGranularity());
        monitorTaskInfo.setMetricId(taskBean.getMetricId());
        monitorTaskInfo.setMonitorProperty(taskBean.getProperties());
        monitorTaskInfo.setTags(taskBean.getTags());
        return monitorTaskInfo;
    }
    
    private static void activateMonitorTask(MonitorTaskInfo taskInfo) throws DataAcquireException {
        MonitorTask monitorTask = new MonitorTask(taskInfo);
        long offset = DateOffset.nextOffSet();
        Date beginTime = getNextGranularityTimeFromBeginTime(taskInfo.getGranularity());
        Date nextLatestExeTime = offsetDate(beginTime, offset);
        nextLatestExeTime = adjustBeginTime(nextLatestExeTime);
		String key = getKey(taskInfo.getGranularity());
		MonitorTaskRunnerThread runner = MonitorTaskRunnerThread.getRunner(key);
		if (runner == null)
		{
			runner = new MonitorTaskRunnerThread();
			runner.addMonitorTask(monitorTask);
			JobKey timeJobKey = null;
			try
			{
				timeJobKey = TimerService.addTask((TimerTask) runner, nextLatestExeTime, null,
						taskInfo.getGranularity() * 1000);
//		        LOGGER.info("new time job:" + jobKey + " for monitorTask: " + taskInfo.getTaskId());
			}
			catch (TimerException e)
			{
				throw new DataAcquireException(
						"TimerException: operate timer task exception. " + e.getMessage());
			}
			LOGGER.info("New monitorTaskRunnerThread, key:" + key + " timeJobKey:" + timeJobKey
					+ " beginTime:" + beginTime);
			runner.setTimeJobKey(timeJobKey);
			MonitorTaskRunnerThread.putRunner(key, runner);
		}
		 else
		{
			LOGGER.info("Put monitorTask in old monitorTaskRunnerThread, key:" + key);
			if (!runner.containsMonitorTask(monitorTask))
			{
				runner.addMonitorTask(monitorTask);
			} else
			{
				LOGGER.warn("monitorTaskList has already contain monitorTask:" + monitorTask);
			}
		}
        taskIdToKeyMap.put(taskInfo.getJobId(), key);
        taskIdToTaskInfo.put(taskInfo.getJobId(), monitorTask);
    }

    /**
     * Calculate the execution time of the next granularity according to the begin time.<br>
     * <br>
     * current time is 9:22 and granularity is 5 minutes, the next execute time is 9:25<br>
     * current time is 9:22 and granularity is 15 minutes, the next execute time is 9:30<br>
     * current time is 9:22 and granularity is 30 minutes, the next execute time is 9:30<br>
     * current time is 9:22 and granularity is 60 minutes, the next execute time is 10:00<br>
     */
    private static Date getNextGranularityTimeFromBeginTime(long granularity) {
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(new Date().getTime());
        if (granularity <= 3600) {
            long retLong = cal.get(Calendar.HOUR_OF_DAY) * 60 * 60 + cal.get(Calendar.MINUTE) * 60
                    + cal.get(Calendar.SECOND);
            retLong = retLong / granularity * granularity;
            cal.set(Calendar.HOUR_OF_DAY, (int) retLong / 60 / 60);
            cal.set(Calendar.MINUTE, (int) retLong / 60 % 60);
            cal.set(Calendar.SECOND, 0);
            cal.set(Calendar.MILLISECOND, 0);
            cal.add(Calendar.SECOND, (int) granularity);
        } else {
            int hour = (int) granularity / 3600;
            cal.set(Calendar.HOUR_OF_DAY, cal.get(Calendar.HOUR_OF_DAY) + hour);
        }

        return cal.getTime();
    }

    /**
     * Calculate the new time according to the offset
     *
     * @param date   initial time
     * @param offset offset
     * @return the new time
     */
    private static Date offsetDate(Date date, long offset) {
        long time = date.getTime();
        return new Date(time + offset);
    }

    private static Date adjustBeginTime(Date beginTime) {
        Calendar ca = Calendar.getInstance();
        ca.setTime(beginTime);
        int second = ca.get(Calendar.SECOND) / 10;
        ca.set(Calendar.SECOND, second * 10);
        ca.set(Calendar.MILLISECOND, 0);
        beginTime = ca.getTime();
        return beginTime;
    }

    /**
     * Calculating a random offset
     */
    private static class DateOffset {
        private static int delta = 15000;

        private static synchronized long nextOffSet() {
            delta = (delta + 4370) % 230000; // (20s~230s)
            return delta;
        }
    }
    
	/**
	 * 每个粒度有10个key值比如  0:60 1:60 ... 9:60
	 * 根据粒度和0-9的随机数生成key，例如：1:60  4:300
	 */
	private static String getKey(int granularity)
	{
		int keyExp = new Random().nextInt(10);
		String key = keyExp + ":" + granularity;
		return key;
	}
}
