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

@ExtensionImpl(keys = { "database.mysql.generallog" }, entensionId = ICollector.EXTENSIONID)
public class MysqlCollectorGeneralLog extends Collector {

	private static final Logger dMsg = LoggerFactory.getLogger(MysqlCollectorGeneralLog.class);
	private StringBuffer cacheMsg = new StringBuffer();
	private Map<String, List<String>> retValueMap = new HashMap<String, List<String>>();
	private static String COMMANDTYPE = "Query";
	private static final ConcurrentHashMap<Integer, String> resultCache = new ConcurrentHashMap<Integer, String>();

	@Override
	public Map<String, List<String>> perform(Properties props) throws DataAcquireException {
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;

		try {
			List eventTime = new ArrayList();
			List userHostList = new ArrayList();
			List commanType = new ArrayList();
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
			sqlData.append("select * from mysql.general_log where event_time >= ");
			sqlData.append("'");
			sqlData.append(beginTime);
			sqlData.append("'");
			sqlData.append(" and event_time< ");
			sqlData.append("'");
			sqlData.append(endTime);
			sqlData.append("'");
			conn = DBUtils.getConnection(props);
			ps = conn.prepareStatement(sqlData.toString());

			rs = ps.executeQuery();

			cacheMsg.append("slow_log datas are ");
			while (rs.next()) {
				if (COMMANDTYPE.equalsIgnoreCase(rs.getString("command_type"))) {
					eventTime.add(rs.getString("event_time"));
					userHostList.add(rs.getString("user_host"));
					commanType.add(rs.getString("command_type"));
					sqlInfoList.add(rs.getString("argument"));
					cacheMsg.append(rs.getString("event_time") + ",");
					cacheMsg.append(rs.getString("user_host") + ",");
					cacheMsg.append(rs.getString("command_type") + ",");
					cacheMsg.append(rs.getString("argument") + "\t");
				}

			}
			cacheMsg.append("\n");
			retValueMap.put("EVENTTIME", eventTime);
			retValueMap.put("USERHOST", userHostList);
			retValueMap.put("COMMANDTYPE", commanType);
			retValueMap.put("SQLINFO", sqlInfoList);
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
