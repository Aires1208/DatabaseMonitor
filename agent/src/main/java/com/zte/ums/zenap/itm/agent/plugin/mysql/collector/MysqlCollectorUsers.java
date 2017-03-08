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

@ExtensionImpl(keys = { "database.mysql.users" }, entensionId = ICollector.EXTENSIONID)
public class MysqlCollectorUsers extends Collector{
	
	private static final Logger dMsg = LoggerFactory.getLogger(MysqlCollectorUsers.class);
	private StringBuffer cacheMsg = new StringBuffer();
	private Map<String, List<String>> retValueMap = new HashMap<String, List<String>>();

	@Override
	public Map<String, List<String>> perform(Properties props) throws DataAcquireException {
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;

		try {
			List hostName = new ArrayList();
			List userName = new ArrayList();
		
			StringBuilder sqlData = new StringBuilder();
			sqlData.append("select Host,User from mysql.user");
			conn = DBUtils.getConnection(props);
			ps = conn.prepareStatement(sqlData.toString());
			
			rs = ps.executeQuery();

			cacheMsg.append("mysql.user datas are ");
			while (rs.next()) {
				hostName.add(rs.getString("Host"));
				userName.add(rs.getString("User"));
				cacheMsg.append(rs.getString("Host") + ",");
				cacheMsg.append(rs.getString("User") + "\t");
			}
			cacheMsg.append("\n");
			retValueMap.put("HOSTNAME", hostName);
			retValueMap.put("USERNAME", userName);
			
		} catch (Exception e) {
			dMsg.error("select Host,User from mysql.user error!");
			throw new DataAcquireException(e, AgentConst.ERRORCODE_COLLECTERROR);
		} finally {
			DBUtils.closeConnection(rs, ps, conn);
		}

		return retValueMap;
	}

}
