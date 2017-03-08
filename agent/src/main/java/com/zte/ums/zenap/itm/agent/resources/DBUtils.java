package com.zte.ums.zenap.itm.agent.resources;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.zte.ums.zenap.itm.agent.dataaq.common.DataAcquireException;

public class DBUtils {
	
	private static final Logger dMsg = LoggerFactory.getLogger(DBUtils.class);
	@SuppressWarnings("rawtypes")
	public static Connection getConnection(Map paras) throws DataAcquireException, ClassNotFoundException, SQLException {
		String dbUser = (String) paras.get("username");
		String dbPassword = (String) paras.get("password");
		String dbIP = (String) paras.get("ip");
		String mysqlPort = (String) paras.get("port");

		StringBuffer sConnStr = new StringBuffer("jdbc:mysql://");
		sConnStr.append(dbIP).append(":").append(mysqlPort);

		DbDriver.register("MySQL");
		Connection conn = DriverManager.getConnection(sConnStr.toString(), dbUser, dbPassword);
		return conn;
	}

	public static void closeConnection(ResultSet rs, PreparedStatement ps, Connection conn) {
		if (rs != null) {
			try {
				rs.close();
			} catch (SQLException e) {
				dMsg.warn(e.getMessage());
			}
		}
		if (ps != null) {
			try {
				ps.close();
			} catch (SQLException e) {
				dMsg.warn(e.getMessage());
			}
			ps = null;
		}

		if (conn != null) {
			try {
				conn.close();
			} catch (SQLException e) {
				dMsg.warn(e.getMessage());
			}

		}
	}

}
