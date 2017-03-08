/**
 * Copyright (C) 2015 ZTE, Inc. and others. All rights reserved. (ZTE)
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */
package com.zte.ums.zenap.itm.agent.plugin.mysql.collector;

import com.zte.ums.zenap.itm.agent.plugin.mysql.utils.DBUtils;
import com.zte.ums.zenap.itm.agent.common.AgentConst;
import com.zte.ums.zenap.itm.agent.common.util.ExtensionImpl;
import com.zte.ums.zenap.itm.agent.dataaq.common.DataAcquireException;
import com.zte.ums.zenap.itm.agent.extension.Collector;
import com.zte.ums.zenap.itm.agent.extension.ICollector;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@ExtensionImpl(keys = { "database.mysql.system" }, entensionId = ICollector.EXTENSIONID)
public class MysqlCollectorStatus extends Collector {

	private static final String REQUESTSRECEIVED = "Questions";
	private static final String SELECTSQL = "Com_select";
	private static final String CURCONNECTION = "Threads_running";
	private static final String UPTIME = "Uptime";

	private Map<String, List<String>> retValueMap = new HashMap<String, List<String>>();
	private static final ConcurrentHashMap<Integer, Map<String, List<String>>> resultCache = new ConcurrentHashMap<Integer, Map<String, List<String>>>();
	private static Map<String, String> metrics = new HashMap<String, String>();
	static {
		metrics.put("Com_select", SELECTSQL);
		metrics.put("Threads_running", CURCONNECTION);
		metrics.put("Questions", REQUESTSRECEIVED); 
		metrics.put("Uptime", UPTIME); 
	}

	@Override
	public Map<String, List<String>> perform(Properties paras) throws DataAcquireException {

		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;

		try {
			conn = DBUtils.getConnection(paras);

			String sqlString = "show global variables";

			ps = conn.prepareStatement(sqlString);
			rs = ps.executeQuery();

			while (rs.next()) {
				String metricsKey = rs.getString("Variable_name");
				List<String> list = new ArrayList<String>();
				list.add(rs.getString("Value"));
				retValueMap.put(metricsKey, list);
			}
			rs.close();
			ps.close();
			
			
			String sqlData = "show /*!50002 GLOBAL */ status";
			ps = conn.prepareStatement(sqlData);
			rs = ps.executeQuery();
			while (rs.next()) {
				if (metrics.containsKey(rs.getString("Variable_name"))) {
					String metricsKey = rs.getString("Variable_name");
					List<String> list = new ArrayList<String>();
					list.add(rs.getString("Value"));
					retValueMap.put(metrics.get(metricsKey), list);
				}
			}
			computeMetrics();

		} catch (Exception e) {
			throw new DataAcquireException(e, AgentConst.ERRORCODE_COLLECTERROR);
		} finally {
			DBUtils.closeConnection(rs, ps, conn);
		}

		return retValueMap;
	}

	private void computeMetrics() {
		// Current valid connections is the same value as THREADSUSED metrics.
//		List<String> curCollectEndTime = new ArrayList<String>();
//		curCollectEndTime.add(String.valueOf(System.currentTimeMillis() / 1000));
//		retValueMap.put(AgentConst.m_COLLECTEDTIME, curCollectEndTime);

		// for first collect
		if (resultCache.get(taskInfo.getJobId()) == null) {
			List<String> firstRetValue = new ArrayList<String>();
			firstRetValue.add("0");
			retValueMap.put(REQUESTSRECEIVED, firstRetValue);
			retValueMap.put(SELECTSQL, firstRetValue);
		} else {

			List<String> curRequestsReceived = retValueMap.get(REQUESTSRECEIVED);
			List<String> curselectCount = retValueMap.get(SELECTSQL);

			Map<String, List<String>> prevResult = resultCache.get(taskInfo.getJobId());
			List<String> prevRequestsReceived = prevResult.get(REQUESTSRECEIVED);
			List<String> prevselectCount = prevResult.get(SELECTSQL);
//			List<String> prevCollectEndTime = prevResult.get(AgentConst.m_COLLECTEDTIME);

//			long computeTime = Long.parseLong(curCollectEndTime.get(0)) - Long.parseLong(prevCollectEndTime.get(0));
			List<String> requestsReceived = oprList(curRequestsReceived, prevRequestsReceived);
			List<String> selectCount = oprList(curselectCount, prevselectCount);

			retValueMap.put(REQUESTSRECEIVED, requestsReceived);
			retValueMap.put(SELECTSQL, selectCount);
		}
		resultCache.put(taskInfo.getJobId(), retValueMap);
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	protected List<String> oprList(List<String> list0, List<String> list1) {
		List list = new ArrayList();
		for (int i = 0; i < list0.size(); i++) {
			int temp = Math.abs(Integer.parseInt(list0.get(i)) - Integer.parseInt(list1.get(i)));
//			list.add(String.format("%.2f", temp));
			list.add(String.valueOf(temp));
		}
		return list;
	}
}
