package com.zte.ums.zenap.itm.agent.dataaq.scheduler;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import org.quartz.JobKey;

import com.zte.ums.zenap.itm.agent.common.util.AgentUtil;
import com.zte.ums.zenap.itm.agent.task.timer.TimerTask;

public class MonitorTaskRunnerThread implements TimerTask
{
//	private static DebugPrn dMsg = new DebugPrn(MonitorTaskRunnerThread.class.getName());
	/**
	 * 缓存 key 和 MonitorTaskRunnerThread 以供删除时使用
	 */
	private static ConcurrentHashMap<String, MonitorTaskRunnerThread> keyRunnerMap = new ConcurrentHashMap<String, MonitorTaskRunnerThread>();

	/**
	 * 由时间调度服务TimerService产生的TimerTask的ID
	 */
	private JobKey timeJobKey;

	/**
	 * MonitorTask任务列表
	 */
	private List<MonitorTask> monitorTaskList = null;

	/**
	 * 构造方法
	 * 
	 * @param key
	 */
	public MonitorTaskRunnerThread()
	{
		monitorTaskList = new ArrayList<MonitorTask>();
	}

	/**
	 * 根据指定的时间归一化key从缓存中查找一个MonitorTaskRunnerThread。没有则返回null
	 * 
	 * @param key
	 * @return
	 */
	public static MonitorTaskRunnerThread getRunner(String key)
	{
		return keyRunnerMap.get(key);
	}

	/**
	 * 将一个时间归一化key和其对应的一个MonitorTaskRunnerThread放入缓存中
	 * 
	 * @param key
	 * @param runner
	 */
	public static void putRunner(String key, MonitorTaskRunnerThread runner)
	{
		keyRunnerMap.put(key, runner);
	}

	/**
	 * 从缓存中移除指定的时间归一化key对应的MonitorTaskRunnerThread
	 * 
	 * @param key
	 * @return
	 */
	public static MonitorTaskRunnerThread removeRunner(String key)
	{
		return keyRunnerMap.remove(key);
	}

	public void run()
	{
		for (int i = 0; i < monitorTaskList.size(); i++)
		{
			MonitorTask monitorTask = monitorTaskList.get(i);
			AgentUtil.putMonitorTaskQueue(monitorTask);
		}
	}

	/**
	 * 向MonitorTask列表中添加一个任务
	 * 
	 * @param monitorTask
	 */
	public void addMonitorTask(MonitorTask monitorTask)
	{
		monitorTaskList.add(monitorTask);
	}

	/**
	 * 判断MonitorTask列表中是否已包含一个指定的任务
	 * 
	 * @param monitorTask
	 * @return
	 */
	public boolean containsMonitorTask(MonitorTask monitorTask)
	{
		return monitorTaskList.contains(monitorTask);
	}

	/**
	 * 检查MonitorTask列表时候为空
	 * 
	 * @return
	 */
	public boolean isMonitorTaskEmpty()
	{
		return monitorTaskList.size() == 0;
	}

	/**
	 * 从MonitorTask列表中移除一个指定的任务
	 * 
	 * @param monitorTask
	 */
	public void removeMonitorTask(MonitorTask monitorTask)
	{
		monitorTaskList.remove(monitorTask);
	}

	public JobKey getTimeJobKey() {
		return timeJobKey;
	}

	public void setTimeJobKey(JobKey timeJobKey) {
		this.timeJobKey = timeJobKey;
	}
	
}
