package com.zte.ums.zenap.itm.agent.dataaq.scheduler;

import java.util.Date;
import java.util.Hashtable;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.zte.ums.zenap.itm.agent.common.util.SysConfLoad;

public class MonitorTaskQueue extends Thread
{
    private static final Logger dMsg = LoggerFactory.getLogger(MonitorTaskQueue.class);
	private int maxQueueSize = 200; // 按时间、粒度归类后一个任务队列中任务个数(2000个节点预估)
	private int coreThreadNum = 10; // 处理任务队列的线程初始个数(配置文件可配)
	private static int maxThreadNum = 10; // 处理任务队列的线程最大个数(配置文件可配)
	private int runCollectPeriod = 1; // 正常处理任务的时间间隔(配置文件可配)

	private transient boolean isWorking = true;
	private ConcurrentLinkedQueue<MonitorTask> queue = new ConcurrentLinkedQueue<MonitorTask>();
	private ThreadPoolExecutor poolThreads = null;

	/**
	 * 构建器
	 */
	public MonitorTaskQueue()
	{
		init();
		poolThreads = new ThreadPoolExecutor(coreThreadNum, maxThreadNum, 2, TimeUnit.MINUTES, new ArrayBlockingQueue<Runnable>(
				maxQueueSize), new DefaultRejectedExecutionHandler());
	}

	/**
	 * 从配置文件获取参数
	 */
	private void init()
	{
		Hashtable<String, String> htQueue = SysConfLoad.getInstance().getHmQueueInfo("MonitorTaskQueue");
		maxQueueSize = Integer.parseInt(htQueue.get("maxQueueSize"));
		int tmp_coreThreadNum = Integer.parseInt(htQueue.get("coreThreadNum"));
		coreThreadNum = tmp_coreThreadNum < 10 ? 10 : tmp_coreThreadNum;
		int tmp_maxThreadNum = Integer.parseInt((String) htQueue.get("maxThreadNum"));
		maxThreadNum = tmp_maxThreadNum < 10 ? 10 : tmp_maxThreadNum;
		runCollectPeriod = Integer.parseInt((String) htQueue.get("runCollectPeriod"));
	}

	/**
	 * 执行
	 */
	public void run()
	{
		while (isWorking)
		{
			if (queue.size() == 0)
			{
				try
				{
					Thread.sleep(runCollectPeriod * 100);
				}
				catch (InterruptedException e)
				{
					dMsg.warn("Thread Interrupted!", e);
				}
			} else
			{
				dispatchMonitorTask();
			}
		}
	}

	/**
	 * 处理消息
	 */
	private void dispatchMonitorTask()
	{
		while (queue.size() > 0)
		{
			MonitorTask monitorTask = (MonitorTask) queue.remove();
			// 处理对象
			poolThreads.execute(monitorTask);
		}
	}

	/**
	 * 向collectMsgMap添加消息对象，供内部工作线程处理
	 */
	public void put(MonitorTask task)
	{
		task.initReportTime(new Date());
		// 向队列添加消息
		queue.add(task);
	}

	/**
	 * 关闭消息队列，主要是停止各内部工作者线程
	 */
	public void close()
	{
		isWorking = false;
		poolThreads.shutdownNow();
	}
	
	private class DefaultRejectedExecutionHandler implements
	RejectedExecutionHandler {

		@Override
		public void rejectedExecution(Runnable r, ThreadPoolExecutor executor) {
			dMsg.info("DATAAQ task queu full!" + executor.getQueue().size());
            if (!executor.isShutdown()) {
            	executor.getQueue().poll();
            	executor.execute(r);
            }
		}
	}
}
