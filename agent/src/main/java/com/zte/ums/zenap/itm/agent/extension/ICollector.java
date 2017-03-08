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
package com.zte.ums.zenap.itm.agent.extension;

import java.util.List;
import java.util.Map;
import java.util.Properties;

import com.zte.ums.zenap.itm.agent.dataaq.common.DataAcquireException;
import com.zte.ums.zenap.itm.agent.dataaq.monitor.bean.common.MonitorTaskInfo;

/**
 * Interface for performing data acquisition tasks
 */
public interface ICollector {
	
	public static final String EXTENSIONID = "com.zte.ums.zenap.itm.agent.extension.ICollector";

	/**
	 * Perform data collection and parse
	 * @param prop acquisition properties
	 * @return
	 * @throws DataAcquireException
	 */
	public Map<String, List<String>> perform(Properties prop) throws DataAcquireException;
    public void setMonitorTaskInfo(MonitorTaskInfo taskInfo);
}
