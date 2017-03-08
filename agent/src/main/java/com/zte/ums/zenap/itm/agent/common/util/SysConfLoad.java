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
package com.zte.ums.zenap.itm.agent.common.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Hashtable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.yaml.snakeyaml.Yaml;

import com.zte.ums.zenap.itm.agent.common.AgentConst;
import com.zte.ums.zenap.itm.agent.common.bean.Queue;
import com.zte.ums.zenap.itm.agent.common.bean.QueueConfig;
import com.zte.ums.zenap.itm.agent.common.util.filescan.FastFileSystem;

public class SysConfLoad {
    private static final Logger LOGGER = LoggerFactory.getLogger(SysConfLoad.class);
    private static SysConfLoad instance = new SysConfLoad();
    private HashMap<String, Hashtable<String, String>> hmQueueInfo = new HashMap<String, Hashtable<String, String>>();
    public final static String MODEL_FILE_PATTERN = "queues.config";
    public static SysConfLoad getInstance() {
        return instance;
    }

    private SysConfLoad() {
        try {
            loadQueuesConfig();
        } catch (IOException e) {
            LOGGER.error(e.getMessage());
        }
    }

    private void loadQueuesConfig() throws IOException {
        Yaml yaml = new Yaml();
        File file = FastFileSystem.getFile(AgentConst.SYSTEMDIR, MODEL_FILE_PATTERN);
    	
        QueueConfig queueConfig = yaml.loadAs(new FileInputStream(file), QueueConfig.class);
        Queue[] queues = queueConfig.getQueue();
        for (Queue queue : queues)
        {
            Hashtable<String, String> htQueue = new Hashtable<String, String>();
            htQueue.put("coreThreadNum", queue.getCoreThreadNum());
            htQueue.put("maxQueueSize", queue.getMaxQueueSize());
            htQueue.put("maxThreadNum", queue.getMaxThreadNum());
            htQueue.put("runCollectPeriod", queue.getRunCollectPeriod());
            hmQueueInfo.put(queue.getQueueName(), htQueue);
        }
    }

    public Hashtable<String, String> getHmQueueInfo(String queueName) {
        return hmQueueInfo.get(queueName);
    }
	
}
