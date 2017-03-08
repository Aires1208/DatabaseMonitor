package com.zte.ums.zenap.itm.agent.task.timer;

import static org.quartz.JobBuilder.newJob;
import static org.quartz.SimpleScheduleBuilder.simpleSchedule;
import static org.quartz.TriggerBuilder.newTrigger;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Date;
import java.util.Properties;

import org.quartz.JobDetail;
import org.quartz.JobKey;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.SchedulerFactory;
import org.quartz.SimpleTrigger;
import org.quartz.impl.StdSchedulerFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.zte.ums.zenap.itm.agent.common.AgentConst;
import com.zte.ums.zenap.itm.agent.common.util.filescan.FastFileSystem;

/**
 * <p>
 * Title: TimerServiceImpl.java
 * </p>
 * <p>
 * Description: Timer操作实现类
 * </p>
 * <p>
 * Copyright: Copyright (c) 2010
 * </p>
 * <p>
 * Company: ZTE
 * </p>
 * 
 * @author HuangJian
 * @version 1.0
 */
final public class TimerServiceImpl
{
	static final int MILLISECONDS_OF_SECOND = 1000;
	static final int SECONDS_OF_MINUTE = 60;
	static final int MINUTES_OF_HOUR = 60;
	static final int HOURS_OF_DAY = 24;
	static final int DAYS_OF_WEEK = 7;
	static final int DAYS_OF_YEAR = 365;
	static final int MINDAYS_OF_MONTH = 1;
	static final int MAXDAYS_OF_MONTH = 31;
	static final long OFFSET = 3000;
	// internal key.
	// USERTASK名字不能随便修改，因为在quartz的JobRunShell里面有用到
	public static final String USERTASK = "UserTask";
	// internal key.
	public static final String ISRUNNING = "isRunning";
	// internal key.
	public static final String BEMULTI = "beMulti";

	private static Scheduler scheduler = null;
	static long id = 0;
	static long triggerID = 0;

	/** 如果小于这个重复间隔，则抛出异常，单位：毫秒 */
	private final static long ERROR_REPEAT_INTERVAL = 1000;
	private static final int DEFAULT_THREAD_COUNT = 50;
	private static final long DEFAULT_REPEAT_INTERVAL = 10000;
	public static int WARNING_THREAD_COUNT = DEFAULT_THREAD_COUNT;
	public static long WARNING_REPEAT_INTERVAL = DEFAULT_REPEAT_INTERVAL;
	public static String QUARTZ_PROPERTIES = "timer-quartz.properties";
//	private static long misfireThreshold = 60000;
	private static final Logger logger = LoggerFactory.getLogger(TimerServiceImpl.class);

	private static TimerServiceImpl instance = new TimerServiceImpl();

	/**
	 * 获得TimerServiceImpl实例
	 * 
	 * @return
	 */
	public static TimerServiceImpl getInstance()
	{
		return instance;
	}

	/**
	 * <p>
	 * 构造函数
	 * </p>
	 * 
	 * @concurrency
	 */
	private TimerServiceImpl()
	{
		initScheduler();
	}

	/**
	 * 初始化调度器参数
	 */
	private void initScheduler()
	{
		SchedulerFactory fac = null;
		File propFile = FastFileSystem.getFile(AgentConst.SYSTEMDIR,
				QUARTZ_PROPERTIES);
		logger.info("timer config property file is:" + propFile);
		BufferedInputStream is = null;
		try
		{
			Properties props = new Properties();
			is = new BufferedInputStream(new FileInputStream(propFile));
			props.load(is);

			fac = new StdSchedulerFactory(props);
			scheduler = fac.getScheduler();
			scheduler.start();
		}
		catch (Exception e)
		{
			logger.error("Initial timer failed", e);
			if (scheduler != null)
			{
				try
				{
					if (!scheduler.isShutdown())
					{
						scheduler.shutdown();
					}
				}
				catch (SchedulerException e1)
				{
					logger.error(e1.getMessage());
				}
				scheduler = null;
			}
		}
		finally
		{
			if (is != null)
			{
				try
				{
					is.close();
				}
				catch (IOException e)
				{
					logger.error(e.getMessage());
				}
				is = null;
			}
		}
	}

	/**
	 * 设置定时任务
	 * 
	 * @param task
	 *            任务对象实例
	 * @param startTime
	 *            任务开始执行的时间,null表示当前时间
	 * @param endTime
	 *            任务结束执行时间，null表示不停止
	 * @param repeatInterval
	 *            任务重复执行的间隔时间，单位是毫秒
	 * @return 任务的标示ID
	 * @exception TimerException
	 *                线程安全
	 */
	protected JobKey addTask(TimerTask task, Date startTime, Date endTime, long repeatInterval) throws TimerException
	{
		// 周期小于10秒则警告
		if ((repeatInterval > 0) && (repeatInterval < WARNING_REPEAT_INTERVAL))
		{
			String warnStr = "The repeat interval of new task:" + task + " is too short:" + repeatInterval
					+ "ms. It's fewer than warning interval:" + WARNING_REPEAT_INTERVAL + "ms";
			logger.warn(warnStr);
			if (repeatInterval < ERROR_REPEAT_INTERVAL)// 周期小于1秒则返回异常，不支持
			{
				throw new TimerException("The task repeat interval is short than:" + ERROR_REPEAT_INTERVAL + "ms");
			}
		}
		if (startTime == null)
		{
			startTime = new Date();// 开始时间为null则取当前时间为开始时间
		}
		if (endTime != null)
		{
			if (startTime.after(endTime) || endTime.before(new Date()))// 检查结束时间是否早于开始时间或结束时间早于当前时间
			{
				throw new TimerException("end time before now!");
			}
		}
		String taskID = genTaskID();
		try
		{
			// 构造JobDetail
			JobDetail detail = newJob(TimerJob.class)
			.withIdentity(taskID, "Scheduler.DEFAULT_GROUP")
			.build();
			detail.getJobDataMap().put(USERTASK, task);// 设置用户要执行的任务
			detail.getJobDataMap().put(ISRUNNING, false);// 设置任务的运行状态
			detail.getJobDataMap().put(BEMULTI, false);// 设置是否支持堆叠任务
			// 构造SimpleTrigger
			SimpleTrigger trigger = newTrigger()
				    .withIdentity(genTriggerID(taskID), Scheduler.DEFAULT_GROUP)
				    .startAt(startTime)  // if a start time is not given (if this line were omitted), "now" is implied
				    .endAt(endTime)
				    .withSchedule(simpleSchedule()
				    .withIntervalInMilliseconds(repeatInterval) 
				    .withRepeatCount(SimpleTrigger.REPEAT_INDEFINITELY)) // note that 10 repeats will give a total of 11 firings
				    .build();
			
			
			// 判断是否有有效的执行时间点
			Date firstFireTime;
			if (startTime.compareTo(new Date()) >= 0)
			{
				firstFireTime = trigger.getFireTimeAfter(new Date(startTime.getTime() - OFFSET));
			} else
			{
				firstFireTime = trigger.getFireTimeAfter(new Date(System.currentTimeMillis() - OFFSET));
			}
			if (firstFireTime == null)
			{
				throw new TimerException("firstFireTime == null,Task has no chance to be excuted");
			}

			// 设置定时任务
			scheduler.scheduleJob(detail, trigger);
			logger.info("Add task successful:" + detail.getJobDataMap().get(USERTASK));

			return detail.getKey();
		}
		catch (SchedulerException e)
		{
			throw new TimerException(e);
		}
	}



	/**
	 * 删除定时任务
	 * 
	 * @param taskID
	 *            任务名称。若传入一个不存在的taskID，此操作无效，不会抛异常。
	 * @exception TimerException
	 *                线程安全
	 */
	protected void removeTask(JobKey jobKey) throws TimerException
	{
		try
		{
			boolean result = scheduler.deleteJob(jobKey);
			if (result == false)
			{
				logger.info("Time job:" + jobKey + " doesn't exist, may be it has been removed automatically.");
			} else
			{
				logger.info("Time job:" + jobKey + " was removed successfully.");
			}

		}
		catch (SchedulerException e)
		{
			throw new TimerException(e);
		}
	}

	/**
	 * 生成任务id
	 * 
	 * @return
	 */
	static private synchronized String genTaskID()
	{
		return String.valueOf(id++);
	}

	/**
	 * 生成触发器id
	 * 
	 * @param taskID
	 * @return 格式：triggerID:taskID
	 */
	static private synchronized String genTriggerID(String taskID)
	{
		StringBuffer sb = new StringBuffer();
		sb.append(triggerID++).append(':').append(taskID);
		return sb.toString();
	}

	protected Scheduler getScheduler()
	{
		return scheduler;
	}
}
