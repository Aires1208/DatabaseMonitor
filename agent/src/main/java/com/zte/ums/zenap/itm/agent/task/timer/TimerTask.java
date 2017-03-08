package com.zte.ums.zenap.itm.agent.task.timer;

/**
 * <p>
 * Title: TimerTask.java
 * </p>
 * <p>
 * Description: 可以被定时器计划执行一次或循环执行的接口
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
public interface TimerTask
{
    /**
     * 定时任务要执行的业务逻辑.
     */
    void run();
}
