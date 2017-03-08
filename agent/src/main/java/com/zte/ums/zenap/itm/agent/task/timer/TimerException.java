package com.zte.ums.zenap.itm.agent.task.timer;

/**
 * <p>
 * Title: TimerException.java
 * </p>
 * <p>
 * Description: Timer异常类
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
public class TimerException extends Exception
{
    private static final long serialVersionUID = -988427439604881645L;

    /**
     * 构造函数
     * 
     * @param message
     *            异常消息
     */
    public TimerException(String message)
    {
        super(message);
    }

    /**
     * 构造函数
     * 
     * @param cause
     *            原始异常
     */
    public TimerException(Throwable cause)
    {
        super(cause);
    }
}
