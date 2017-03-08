package com.zte.ums.zenap.itm.agent.task.timer;

import org.quartz.JobDataMap;
import org.quartz.JobDetail;
import org.quartz.JobExecutionContext;
import org.quartz.JobKey;
import org.quartz.SchedulerException;
import org.quartz.Trigger;
import org.quartz.Trigger.CompletedExecutionInstruction;
import org.quartz.listeners.TriggerListenerSupport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * <p>
 * Title: GlobalTriggerListener.java
 * </p>
 * <p>
 * Description: 实现对Trigger的监听类
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
public class GlobalTriggerListener extends TriggerListenerSupport 
{
	private static final Logger logger = LoggerFactory.getLogger(GlobalTriggerListener.class);
	
	public GlobalTriggerListener() {

	}

	@Override
	public String getName()
	{
		return "ITM_TIMER_GlobalTriggerListener";
	}

	/**
	 * Called by the Scheduler when a Trigger has fired, and it's associated JobDetail is about to be executed.
	 */
	@Override
	public void triggerFired(Trigger trigger, JobExecutionContext context)
	{
		Object taskClass = context.getJobDetail().getJobDataMap().get(TimerServiceImpl.USERTASK);
		logger.debug("Fired task:" + taskClass + " Other info:" + trigger + context);
	}

	/**
	 * Called by the Scheduler when a Trigger has misfired.
	 */
	@Override
	public void triggerMisfired(Trigger trigger)
	{
		JobKey jobKey = trigger.getJobKey();
		JobDetail jd;
		try
		{
			jd = TimerServiceImpl.getInstance().getScheduler().getJobDetail(jobKey);
			JobDataMap dataMap = null;
			if (jd != null)
			{
				dataMap = jd.getJobDataMap();
			}
			Object task = null;
			if (dataMap != null)
			{
				task = dataMap.get(TimerServiceImpl.USERTASK);
			}
			String warnStr = "Miss the execute time：Task:" + task + " Trigger:" + trigger;
			logger.info(warnStr);

		}
		catch (SchedulerException e)
		{
			logger.error(e.getMessage());
		}
	}

	/**
	 * Called by the Scheduler when a Trigger has fired, it's associated JobDetail has been executed, and it's triggered(xx) method has been called.
	 */
	@Override
	public void triggerComplete(Trigger trigger, JobExecutionContext context,
			CompletedExecutionInstruction arg2) {
		Object taskClass = context.getJobDetail().getJobDataMap().get(TimerServiceImpl.USERTASK);
		logger.debug("Complete task:" + taskClass + " Other info:" + trigger + context);
		
	}
}
