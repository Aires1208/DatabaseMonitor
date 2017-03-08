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
package com.zte.ums.zenap.itm.agent.datarp;

import java.util.Hashtable;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.zte.ums.zenap.itm.agent.common.util.SysConfLoad;

public class DataMsgQueue extends Thread {
    private static final Logger LOGGER = LoggerFactory.getLogger(DataMsgQueue.class);

    private int maxQueueSize = 200;
    private int maxThreadNum = 10;
    private int runCollectPeriod = 1;
	private int coreThreadNum = 10; // 处理任务队列的线程初始个数(配置文件可配)

    private ConcurrentLinkedQueue<List<Object>> queue = new ConcurrentLinkedQueue<List<Object>>();
    private ThreadPoolExecutor poolThreads = null;

    public DataMsgQueue() {
        init();
        poolThreads = new ThreadPoolExecutor(coreThreadNum, maxThreadNum, 2, TimeUnit.MINUTES,
                new ArrayBlockingQueue<Runnable>(maxQueueSize),
                new MyThreadFactory("ProxyDataUpload"), new DefaultRejectedExecutionHandler());
    }

    private void init() {
        Hashtable<String, String> htQueue =
                SysConfLoad.getInstance().getHmQueueInfo("DataMsgQueue");
        maxQueueSize = Integer.parseInt(htQueue.get("maxQueueSize"));
		int tmp_coreThreadNum = Integer.parseInt(htQueue.get("coreThreadNum"));
		coreThreadNum = tmp_coreThreadNum < 10 ? 10 : tmp_coreThreadNum;
        int tmp_maxThreadNum = Integer.parseInt(htQueue.get("maxThreadNum"));
        maxThreadNum = tmp_maxThreadNum < 10 ? 10 : tmp_maxThreadNum;
        runCollectPeriod = Integer.parseInt(htQueue.get("runCollectPeriod"));
    }

    @Override
    public void run() {
        while (true) {
            if (queue.size() == 0) {
                try {
                    Thread.sleep(runCollectPeriod * 100);
                } catch (InterruptedException e) {
                    LOGGER.warn("Thread Interrupted!", e);
                }
            } else {
                dispatchDataRptMsg();
            }
        }
    }

    private void dispatchDataRptMsg() {
        while (queue.size() > 0) {
            List<Object> vTaskPara = queue.remove();
            LOGGER.debug("DATA msg queue size:" + poolThreads.getQueue().size());
            poolThreads.execute(new DataMsgHandleThread(vTaskPara));
        }
    }

    public void put(List<Object> msg) {
        queue.add(msg);
    }

    private class DefaultRejectedExecutionHandler implements RejectedExecutionHandler {
        @Override
        public void rejectedExecution(Runnable r, ThreadPoolExecutor executor) {
            LOGGER.info("DATA msg queue full!" + executor.getQueue().size());
            if (!executor.isShutdown()) {
                executor.getQueue().poll();
                executor.execute(r);
            }
        }
    }
}
