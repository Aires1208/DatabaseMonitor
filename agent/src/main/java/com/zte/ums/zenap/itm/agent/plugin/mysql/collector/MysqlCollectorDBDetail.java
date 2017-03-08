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

@ExtensionImpl(keys = { "database.mysql.db" }, entensionId = ICollector.EXTENSIONID)
public class MysqlCollectorDBDetail extends Collector {
	
	private static final Logger dMsg = LoggerFactory.getLogger(MysqlCollectorDBDetail.class);
	private StringBuffer cacheMsg = new StringBuffer();
	private Map<String, List<String>> retValueMap = new HashMap<String, List<String>>();

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public Map<String, List<String>> perform(Properties paras) throws DataAcquireException {

		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;

		try {
			List dbNames = new ArrayList();
			List dbSizes = new ArrayList();
			List indexSizes = new ArrayList();

			conn = DBUtils.getConnection(paras);
			ps = conn.prepareStatement("select schema_name as name from information_schema.schemata");
			rs = ps.executeQuery();

			cacheMsg.append("database names are ");
			while (rs.next()) {
				dbNames.add(rs.getString("name"));
				cacheMsg.append(rs.getString("name") + "\t");
			}

			if (dbNames.size() <= 0) {
				throw new DataAcquireException(11,
						"select schema_name as name from information_schema.schemata return null!");
			}

			cacheMsg.append("\n");
			retValueMap.put("DBNAME", dbNames);

			rs.close();
			ps.close();

			for (int i = 0; i < dbNames.size(); i++) {
				String dbName = (String) dbNames.get(i);
				long size = 0;
				long indexSIze =0;
				ps = conn.prepareStatement("show table status from " + dbName);
				rs = ps.executeQuery();
				while (rs.next()) {
					size += rs.getLong("Data_length") / 1024;
					indexSIze += rs.getLong("Index_length") / 1024;
				}
				dbSizes.add(String.valueOf(size));
				indexSizes.add(String.valueOf(indexSIze));
				cacheMsg.append(dbName).append(" data size is ").append(size);
				cacheMsg.append(dbName).append(" index size is ").append(indexSIze).append("\n");

			}
			retValueMap.put("DATALENGTH", dbSizes);
			retValueMap.put("INDEXLENGTH", indexSizes);
		} catch (Exception e) {
			dMsg.error("select schema_name as name from information_schema.schemata error!");
			throw new DataAcquireException(e, AgentConst.ERRORCODE_COLLECTERROR);
		} finally {
			DBUtils.closeConnection(rs, ps, conn);
		}

		return retValueMap;
	}

}
