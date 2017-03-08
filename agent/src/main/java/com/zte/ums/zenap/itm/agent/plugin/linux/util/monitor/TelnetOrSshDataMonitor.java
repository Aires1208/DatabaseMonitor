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
package com.zte.ums.zenap.itm.agent.plugin.linux.util.monitor;

import com.zte.ums.zenap.itm.agent.plugin.linux.util.Const;
import com.zte.ums.zenap.itm.agent.plugin.linux.util.MonitorException;
import com.zte.ums.zenap.itm.agent.plugin.linux.util.cli.datacollector.TelnetOrSshDataCollector;
import com.zte.ums.zenap.itm.agent.plugin.linux.util.conf.DaConfReader;
import com.zte.ums.zenap.itm.agent.plugin.linux.util.conf.DaMonitorPerfInfo;
import com.zte.ums.zenap.itm.agent.plugin.linux.util.conf.DaPerfCounterInfo;
import com.zte.ums.zenap.itm.agent.plugin.linux.util.dataparser.TelnetOrSshDataParser;
import com.zte.ums.zenap.itm.agent.plugin.linux.util.para.ICollectorPara;
import com.zte.ums.zenap.itm.agent.plugin.linux.util.para.SshCollectorPara;
import com.zte.ums.zenap.itm.agent.plugin.linux.util.para.TelnetCollectorPara;
import com.zte.ums.zenap.itm.agent.dataaq.common.DataAcquireException;
import com.zte.ums.zenap.itm.agent.dataaq.monitor.bean.common.MonitorTaskInfo;

import java.util.*;

public class TelnetOrSshDataMonitor{

    private static TelnetOrSshDataMonitor dataMonitor;
    
    public static synchronized TelnetOrSshDataMonitor getIntance()
    {
    	if (dataMonitor == null)
    	{
    		dataMonitor = new TelnetOrSshDataMonitor();
    	}
    	return dataMonitor;
    }
    
    @SuppressWarnings({ "rawtypes", "unchecked", "unused" })
	public Map perform(MonitorTaskInfo taskInfo, TelnetOrSshDataCollector dataCollector, TelnetOrSshDataParser dataParser)
            throws DataAcquireException {
    	Properties prop = taskInfo.getMonitorProperty();
        String metricId = taskInfo.getMetricId();
        DaMonitorPerfInfo monitorParserMapInfo = getMonitorParserMapInfo(metricId);
        if (monitorParserMapInfo == null) {
            return new HashMap<String, List<String>>();
        }
        String commandStr = monitorParserMapInfo.getCommand();

        String ip = prop.getProperty(Const.IPADDRESS);
        String portString = prop.getProperty(Const.PORT);
        String userName = prop.getProperty(Const.USERNAME);
        String passWord = prop.getProperty(Const.PASSWORD);

        ICollectorPara para;
        String protocol = prop.getProperty(Const.PROTOCOL);
        if (protocol == null || protocol.equalsIgnoreCase(Const.TELNET)) {
            int port = 23;
            if (portString != null) {
                port = Integer.parseInt(portString);
            }
            para = new TelnetCollectorPara(ip, port, userName, passWord);
        } else {
            int port = 22;
            if (portString != null) {
                port = Integer.parseInt(portString);
            }
            para = new SshCollectorPara(ip, port, userName, passWord);
        }

        Map commandsMap = new HashMap();
        commandsMap.put(commandStr, monitorParserMapInfo.acceptTokens);

        Map retDataCollected;
        try {
            retDataCollected = dataCollector.collectData(taskInfo, para, commandsMap);
        } catch (MonitorException e) {
        	throw new DataAcquireException(e.getMessage());

        }

        Map resultMap = new HashMap();

        try {
            Set retDataEntySet = retDataCollected.entrySet();
            for (Iterator iterator = retDataEntySet.iterator(); iterator.hasNext();) {
                Map.Entry valueForEveryCmdEntry = (Map.Entry) iterator.next();
                String retCommandName = (String) valueForEveryCmdEntry.getKey();
                ArrayList valueCollected = (ArrayList) valueForEveryCmdEntry.getValue();

                List perfCounters = monitorParserMapInfo.getPerfCounters();
                int size = perfCounters.size();
                for (int i = 0; i < size; i++) {
                    DaPerfCounterInfo perfCounterInfo = (DaPerfCounterInfo) perfCounters.get(i);
                    Object value = dataParser.parse(valueCollected, perfCounterInfo);
                    resultMap.put(perfCounterInfo.getName(), value);
                }
            }
        } catch (Exception e) {
            throw new DataAcquireException(e.getMessage());
        }

//        if (resultMap.get("TELNETINPACKET") != null) {
//            if (resultMap.get("TELNETSPEED") != null) {
//            	TelnetOrSshDataParser telnetDataParser = (TelnetOrSshDataParser) dataParser;
//                resultMap = telnetDataParser
//                        .cacheAndCalculateLinuxTelnetNetworkInterface(taskInfo, resultMap);
//            }
//        }
//		if (resultMap.get("DISKREADBYTES") != null)
//		{
//			TelnetOrSshDataParser telnetDataParser = (TelnetOrSshDataParser) dataParser;
//			resultMap = telnetDataParser.addDiskBusyRatio(resultMap);
//		}
//
//		if (resultMap.get("MYSQLINTRANSRATE") != null)
//		{
//			TelnetOrSshDataParser telnetDataParser = (TelnetOrSshDataParser) dataParser;
//			resultMap = telnetDataParser.calculateMysqlNetworkIO(taskInfo,resultMap);
//		}

        return resultMap;
    }

    protected DaMonitorPerfInfo getMonitorParserMapInfo(String monitorName) {
        return DaConfReader.getInstance().getMonitorParserMapInfo(monitorName);
    }
}
