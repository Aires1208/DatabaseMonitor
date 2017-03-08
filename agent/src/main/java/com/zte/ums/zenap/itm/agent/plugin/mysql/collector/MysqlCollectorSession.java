package com.zte.ums.zenap.itm.agent.plugin.mysql.collector;

import com.zte.ums.zenap.itm.agent.plugin.mysql.utils.DBUtils;
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

@ExtensionImpl(keys = { "database.mysql.session" }, entensionId = ICollector.EXTENSIONID)
public class MysqlCollectorSession extends Collector{
	
	private static final Logger dMsg = LoggerFactory.getLogger(MysqlCollectorSession.class);
	private StringBuffer cacheMsg = new StringBuffer();
	private Map<String, List<String>> retValueMap = new HashMap<String, List<String>>();
	@Override
	public Map<String, List<String>> perform(Properties prop) throws DataAcquireException {

		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;

		try {
			List sessionIDs = new ArrayList();
			List userList = new ArrayList();
			List hostList = new ArrayList();
			List dbName = new ArrayList();
			List commanList = new ArrayList();
			List spendTimeList = new ArrayList();
			List stateList = new ArrayList();
			List sqlInfoList = new ArrayList();

			conn = DBUtils.getConnection(prop);
			ps = conn.prepareStatement("show processlist");
			rs = ps.executeQuery();

			cacheMsg.append("database sessions are ");
			while (rs.next()) {
				sessionIDs.add(rs.getString("Id"));
				userList.add(rs.getString("User"));
				hostList.add(rs.getString("Host"));
				commanList.add(rs.getString("Command"));
				spendTimeList.add(rs.getString("Time"));
				cacheMsg.append(rs.getString("Id") + ",");
				cacheMsg.append(rs.getString("User") + ",");
				cacheMsg.append(rs.getString("Host") + ",");
				cacheMsg.append(rs.getString("Command") + ",");			
				if(null == rs.getString("db") || "".equals(rs.getString("db"))){
					dbName.add("");
				}else{
					dbName.add(rs.getString("db"));
					cacheMsg.append(rs.getString("db") + ",");
				}
				if(null == rs.getString("State") || "".equals(rs.getString("State"))){
					stateList.add("");
				}else{
					stateList.add(rs.getString("State"));
					cacheMsg.append(rs.getString("State") + ",");
				}
				if(null == rs.getString("Info") || "".equals(rs.getString("Info"))){
					sqlInfoList.add("");
				}else{
					sqlInfoList.add(rs.getString("Info"));
					cacheMsg.append(rs.getString("Info") + ",");
					
				}
				cacheMsg.append(rs.getString("Time") + "\t");
			}

			if (sessionIDs.size() <= 0) {
				throw new DataAcquireException(11,
						"show processlist return null!");
			}
			cacheMsg.append("\n");
			retValueMap.put("OID", sessionIDs);
			retValueMap.put("USER", userList);
			retValueMap.put("HOSTNAME", hostList);
			retValueMap.put("DBNAME", dbName);
			retValueMap.put("COMMAND", commanList);
			retValueMap.put("SPENDTIME", spendTimeList);
			retValueMap.put("STATE", stateList);
			retValueMap.put("SQLINFO", sqlInfoList);
			
		} catch (Exception e) {
			dMsg.error("show processlist error!");
			throw new DataAcquireException(e, AgentConst.ERRORCODE_COLLECTERROR);
		} finally {
			DBUtils.closeConnection(rs, ps, conn);
		}

		return retValueMap;
	}

}
