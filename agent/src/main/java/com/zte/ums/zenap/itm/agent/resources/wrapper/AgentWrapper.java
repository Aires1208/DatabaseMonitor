/**
 * Copyright (C) 2015 ZTE, Inc. and others. All rights reserved. (ZTE)
 * <p/>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p/>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p/>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.zte.ums.zenap.itm.agent.resources.wrapper;

import javax.ws.rs.core.Response;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.zte.ums.zenap.itm.agent.task.TaskService;

/**
 * DAC rest interface processing class
 */
public class AgentWrapper {
    private static final Logger LOGGER = LoggerFactory.getLogger(AgentWrapper.class);

    /**
     * Register DAC
     * @return status code 201
     */
    public static Response registerAgent() {
        LOGGER.info("register Agent success.");
        return Response.status(Response.Status.CREATED).build();
    }

    /**
     * unregister DAC
     * @return status code 204
     */
    public static Response unregisterAgent() {
        TaskService.deleteAllMonitorTask();
        LOGGER.info("unregister Agent success.");
        return Response.status(Response.Status.NO_CONTENT).build();
    }
}
