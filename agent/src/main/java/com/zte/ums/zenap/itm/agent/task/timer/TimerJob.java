package com.zte.ums.zenap.itm.agent.task.timer;

import org.quartz.Job;
import org.quartz.JobDataMap;
import org.quartz.JobDetail;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * <p>
 * Title: TimerJob.java
 * </p>
 * <p>
 * Description: Job接口的一个实现
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
public class TimerJob implements Job
{
	private static final Logger dMsg = LoggerFactory.getLogger(TimerJob.class);

	public TimerJob()
	{
	}

	/**
	 * 任务类的任务执行函数。Called by the Scheduler when a Trigger fires that is associated with the Job.
	 * 
	 * @param context
	 *            任务执行上下文
	 * @concurrency
	 * @throws JobExecutionException
	 */
	public void execute(JobExecutionContext context) throws JobExecutionException
	{
		if (context == null)
		{
			dMsg.error("context is null");
			return;
		}
		JobDetail jobDetail = context.getJobDetail();
		if (jobDetail != null)
		{
			JobDataMap jobDataMap = jobDetail.getJobDataMap();
			if (jobDataMap != null)
			{
				/** 当前将要执行的任务 */
				TimerTask task = (TimerTask) jobDataMap.get(TimerServiceImpl.USERTASK);
				/** 任务是否正在执行 */
				boolean isRunning;
				synchronized (jobDataMap)
				{
					isRunning = jobDataMap.getBoolean(TimerServiceImpl.ISRUNNING);
				}
				if (isRunning)
				{
					dMsg.info("The pre-execution is running, this execution cancled：" + task);
					return;
				} else
				{
					synchronized (jobDataMap)
					{
						jobDataMap.put(TimerServiceImpl.ISRUNNING, true);
					}
				}

				if (task != null)
				{
					try
					{
						task.run();
					}
					finally
					{
						synchronized (jobDataMap)
						{
							jobDataMap.put(TimerServiceImpl.ISRUNNING, false);
						}
					}
				} else
				{
					dMsg.error("task is null");
				}
			} else
			{
				dMsg.error("jobDataMap is null");
			}
		} else
		{
			dMsg.error("jobDetail is null");
		}
	}
}