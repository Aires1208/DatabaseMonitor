package com.zte.ums.zenap.itm.agent.plugin.mysql.collector;

import com.zte.ums.zenap.itm.agent.plugin.mysql.utils.DBUtils;
import com.zte.ums.zenap.itm.agent.plugin.mysql.utils.TimesUtils;
import com.zte.ums.zenap.itm.agent.common.AgentConst;
import com.zte.ums.zenap.itm.agent.common.util.ExtensionImpl;
import com.zte.ums.zenap.itm.agent.dataaq.common.DataAcquireException;
import com.zte.ums.zenap.itm.agent.extension.Collector;
import com.zte.ums.zenap.itm.agent.extension.ICollector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@ExtensionImpl(keys = { "database.mysql.slowlog" }, entensionId = ICollector.EXTENSIONID)
public class MysqlCollectorSlowLog extends Collector{

	private static final Logger dMsg = LoggerFactory.getLogger(MysqlCollectorSlowLog.class);
	private StringBuffer cacheMsg = new StringBuffer();
	private Map<String, List<String>> retValueMap = new HashMap<String, List<String>>();
	private static final ConcurrentHashMap<Integer, String> resultCache = new ConcurrentHashMap<Integer, String>();
	@Override
	public Map<String, List<String>> perform(Properties props) throws DataAcquireException {
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;

		try {
			List starTime = new ArrayList();
			List userHostList = new ArrayList();
			List queryTime = new ArrayList();
			List lockTime = new ArrayList();
			List dbName = new ArrayList();
			List sqlInfoList = new ArrayList();

			String beginTime = null;
			long curTime = System.currentTimeMillis();
			if(null == resultCache.get(taskInfo.getJobId())){
				long preMunite = TimesUtils.getCurrentTime(curTime, 1);
				beginTime = TimesUtils.convertTime(preMunite);
			}else{
				beginTime = resultCache.get(taskInfo.getJobId());
			}
			long CurrentMunite = TimesUtils.getCurrentTime(curTime, 0);
			String endTime = TimesUtils.convertTime(CurrentMunite);
				
			StringBuilder sqlData = new StringBuilder();
			sqlData.append("select * from mysql.slow_log where start_time >= ");
			sqlData.append("'");
			sqlData.append(beginTime);
			sqlData.append("'");
			sqlData.append(" and start_time< ");
			sqlData.append("'");
			sqlData.append(endTime);
			sqlData.append("'");
			conn = DBUtils.getConnection(props);
			ps = conn.prepareStatement(sqlData.toString());
			
			rs = ps.executeQuery();

			cacheMsg.append("slow_log datas are ");
			while (rs.next()) {
				starTime.add(rs.getString("start_time"));
				userHostList.add(rs.getString("user_host"));
				queryTime.add(rs.getString("query_time"));
				lockTime.add(rs.getString("lock_time"));
				dbName.add(rs.getString("db"));
				sqlInfoList.add(rs.getString("sql_text"));
				cacheMsg.append(rs.getString("start_time") + ",");
				cacheMsg.append(rs.getString("user_host") + ",");
				cacheMsg.append(rs.getString("query_time") + ",");
				cacheMsg.append(rs.getString("lock_time") + ",");
				cacheMsg.append(rs.getString("db") + ",");
				cacheMsg.append(rs.getString("sql_text") + "\t");
			}
			cacheMsg.append("\n");
			retValueMap.put("BEGINTIME", starTime);
			retValueMap.put("USERHOST", userHostList);
			retValueMap.put("QUERYTIME", queryTime);
			retValueMap.put("LOCKTIME", lockTime);
			retValueMap.put("DBNAME", dbName);
			retValueMap.put("SQLTEXT", sqlInfoList);
			resultCache.put(taskInfo.getJobId(), endTime);
			
		} catch (Exception e) {
			dMsg.error("select slow_log error!");
			throw new DataAcquireException(e, AgentConst.ERRORCODE_COLLECTERROR);
		} finally {
			DBUtils.closeConnection(rs, ps, conn);
		}

		return retValueMap;
	}

}
