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
package com.zte.ums.zenap.itm.agent.resources;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.zte.ums.zenap.itm.agent.common.AgentConst;
import com.zte.ums.zenap.itm.agent.dataaq.common.DataAcquireException;
import com.zte.ums.zenap.itm.agent.extension.Collector;

public class MysqlCollectorSystemMetrics extends Collector {
	
	private static final Logger dMsg = LoggerFactory.getLogger(MysqlCollectorSystemMetrics.class);
	private static final String REQUESTSRECEIVED="REQUESTRATIO";
	private static final String BYTESRECEIVED="BYTESRECEIVEDRATIO";
	private static final String BYTESSENT="BYTESSENTRATIO";
	private static final String INSERTSQL="INSERTSQL";
	private static final String DELETESQL="DELETESQL";
	private static final String UPDATESQL="UPDATESQL";
	private static final String SELECTSQL="SELECTSQL";	

	private StringBuffer cacheMsg = new StringBuffer();
	private Map<String, List<String>> retValueMap = new HashMap<String, List<String>>();
	private static final ConcurrentHashMap<Integer, Map<String, List<String>>> resultCache = new ConcurrentHashMap<Integer, Map<String, List<String>>>();
	private static Map<String, String> metrics = new HashMap<String, String>();
	static
	{
		metrics.put("max_connections", "MAXCONNS"); // from global variables
		metrics.put("connect_timeout", "CONNECTTIMEOUT");
		metrics.put("thread_cache_size", "THREADCACHESIZE");

		metrics.put("Threads_connected", "CURRENTCONNS"); // from global status
		metrics.put("Aborted_connects", "ABORTEDCONNS");
		metrics.put("Aborted_clients", "ABORTEDCLIENTS");
		metrics.put("Com_insert", INSERTSQL);
		metrics.put("Com_delete", DELETESQL);
		metrics.put("Com_update", UPDATESQL);
		metrics.put("Com_select", SELECTSQL);
		metrics.put("Threads_running", "THREADSUSED");
		metrics.put("Threads_cached", "THREADSCACHED");
		metrics.put("Table_locks_immediate", "IMMEDIATELOCKS");
		metrics.put("Table_locks_waited", "WAITEDLOCKS");
		
		metrics.put("Questions", REQUESTSRECEIVED); // from global status and then compute
		metrics.put("Bytes_received", BYTESRECEIVED);
		metrics.put("Bytes_sent", BYTESSENT);
		
		metrics.put("computeConnectionTime", "CONNECTIONTIME"); // from own compute
		metrics.put("computeCurrentValidConns", "CURRENTVALIDCONNS");
	}

	@Override
	public Map<String, List<String>> perform(Properties paras)
			throws DataAcquireException {
		
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;

		try {
			conn = DBUtils.getConnection(paras);

			String[] sqlArray = { "show global variables", "show /*!50002 GLOBAL */ status" };
			for (int i = 0; i < sqlArray.length; i++) {
				ps = conn.prepareStatement(sqlArray[i]);
				rs = ps.executeQuery();

				while (rs.next()) {
					if (metrics.containsKey(rs.getString("Variable_name"))) {
						String metricsKey = rs.getString("Variable_name");

						List<String> list = new ArrayList<String>();
						list.add(rs.getString("Value"));
						retValueMap.put(metrics.get(metricsKey), list);
					}
				}
				rs.close();
				ps.close();
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
		retValueMap.put(metrics.get("computeCurrentValidConns"), retValueMap.get("THREADSUSED"));
		List<String> curCollectEndTime = new ArrayList<String>();
		curCollectEndTime.add(String.valueOf(System.currentTimeMillis() / 1000));
		retValueMap.put(AgentConst.m_COLLECTEDTIME, curCollectEndTime);
		
		//for first collect
		if(resultCache.get(taskInfo.getJobId()) == null){
			List<String> firstRetValue = new ArrayList<String>();
			firstRetValue.add("0");
			retValueMap.put(REQUESTSRECEIVED, firstRetValue);
			retValueMap.put(BYTESRECEIVED, firstRetValue);
			retValueMap.put(BYTESSENT, firstRetValue);
			retValueMap.put(INSERTSQL, firstRetValue);
			retValueMap.put(DELETESQL, firstRetValue);
			retValueMap.put(UPDATESQL, firstRetValue);
			retValueMap.put(SELECTSQL, firstRetValue);
		}
		else
		{
		
			List<String> curRequestsReceived = retValueMap.get(REQUESTSRECEIVED);
			List<String> curBytesReceived = retValueMap.get(BYTESRECEIVED);
			List<String> curBytesSent = retValueMap.get(BYTESSENT);
			List<String> curinsertCount = retValueMap.get(INSERTSQL);
			List<String> curdeleteCount = retValueMap.get(DELETESQL);
			List<String> curupdateCount = retValueMap.get(UPDATESQL);
			List<String> curselectCount = retValueMap.get(SELECTSQL);
			
			Map<String, List<String>> prevResult = resultCache.get(taskInfo.getJobId());
			List<String> prevRequestsReceived = prevResult.get(REQUESTSRECEIVED);
			List<String> prevBytesReceived = prevResult.get(BYTESRECEIVED);
			List<String> prevBytesSent = prevResult.get(BYTESSENT);
			List<String> previnsertCount = prevResult.get(INSERTSQL);
			List<String> prevdeleteCount = prevResult.get(DELETESQL);
			List<String> prevupdateCount = prevResult.get(UPDATESQL);
			List<String> prevselectCount = prevResult.get(SELECTSQL);
			List<String> prevCollectEndTime = prevResult.get(AgentConst.m_COLLECTEDTIME);
			
			long computeTime = Long.parseLong(curCollectEndTime.get(0))-  Long.parseLong(prevCollectEndTime.get(0));
			List<String> requestsReceived = oprList(curRequestsReceived, prevRequestsReceived, computeTime);
			List<String> bytesReceived = oprList(curBytesReceived, prevBytesReceived, computeTime);
			List<String> bytesSent = oprList(curBytesSent, prevBytesSent, computeTime);
			List<String> insertCount = oprList(curinsertCount, previnsertCount, computeTime);
			List<String> deleteCount = oprList(curdeleteCount, prevdeleteCount, computeTime);
			List<String> updateCount = oprList(curupdateCount, prevupdateCount, computeTime);
			List<String> selectCount = oprList(curselectCount, prevselectCount, computeTime);

			retValueMap.put(REQUESTSRECEIVED, requestsReceived);
			retValueMap.put(BYTESRECEIVED, bytesReceived);
			retValueMap.put(BYTESSENT, bytesSent);
			retValueMap.put(INSERTSQL, insertCount);
			retValueMap.put(DELETESQL, deleteCount);
			retValueMap.put(UPDATESQL, updateCount);
			retValueMap.put(SELECTSQL, selectCount);
		}
		resultCache.put(taskInfo.getJobId(), retValueMap);
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	protected List<String> oprList(List<String> list0, List<String> list1, long time) {
		List list = new ArrayList();
		for (int i = 0; i < list0.size(); i++) {
			double temp = Math.abs(Double.parseDouble(list0.get(i)) - Double.parseDouble(list1.get(i))) / time;
			list.add(String.format("%.2f", temp));
		}
		return list;
	}
}
