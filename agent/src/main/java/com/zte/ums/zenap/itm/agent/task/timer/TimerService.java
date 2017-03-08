package com.zte.ums.zenap.itm.agent.task.timer;

import java.util.Date;

import org.quartz.JobKey;

/**
 * <p>
 * Title: TimerService.java
 * </p>
 * <p>
 * Description:
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
public class TimerService
{
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
    public static JobKey addTask(TimerTask task, Date startTime, Date endTime, long repeatInterval)
            throws TimerException
    {
        return TimerServiceImpl.getInstance().addTask(task, startTime, endTime, repeatInterval);
    }

    /**
     * 删除定时任务
     * 
     * @param taskID
     *            任务名称。若传入一个不存在的taskID，此操作无效，不会抛异常。
     * @exception TimerException
     *                线程安全
     */
    public static void removeTask(JobKey jobKey) throws TimerException
    {
       TimerServiceImpl.getInstance().removeTask(jobKey);
    }
}
