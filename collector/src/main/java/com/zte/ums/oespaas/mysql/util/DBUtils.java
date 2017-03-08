package com.zte.ums.oespaas.mysql.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.util.Map;

/**
 * Created by 10203846 on 9/23/16.
 */
public class DBUtils {
    private static final Logger LOGGER = LoggerFactory.getLogger(DBUtils.class);

    public static synchronized void loadDBDriver(String dbType) {
        try {
            if (dbType.toUpperCase().equals("MYSQL")) {
                Class.forName("com.mysql.jdbc.Driver");
            } else if (dbType.toUpperCase().equals("ORACLE")) {
                Class.forName("oracle.jdbc.driver.OracleDriver");
            } else if (dbType.toUpperCase().equals("SQLSERVER")) {
                Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
            }
        } catch (ClassNotFoundException e) {
            LOGGER.error(e.getMessage());
        }
    }

    public static Connection getConnection(String dbType, Map<String, String> paras) {
        String user = paras.get("username");
        String password = paras.get("password");
        String ip = paras.get("ip");
        String port = paras.get("port");

        StringBuilder url = new StringBuilder();
        if (dbType.toUpperCase().equals("MYSQL")) {
            //port default is 3306
            url.append("jdbc:mysql://").append(ip).append(":").append(port);
        } else if (dbType.toUpperCase().equals("ORACLE")) {
            //port default is 1521
            url.append("jdbc:oracle:thin:@").append(ip).append(":").append(port);
        } else if (dbType.toUpperCase().equals("SQLSERVER")) {
            //port default is 1433
            url.append("jdbc:sqlserver://").append(ip).append(":").append(port);
        }

        Connection connection = null;
        try {
            connection = DriverManager.getConnection(url.toString(), user, password);
        } catch (SQLException e) {
            LOGGER.error(e.getMessage());
        }

        return connection;
    }

    public static void closeConnection(ResultSet rs, PreparedStatement ps, Connection conn) {
        if (rs != null) {
            try {
                rs.close();
            } catch (SQLException e) {
                LOGGER.error(e.getMessage());
            } finally {
                rs = null;
            }
        }

        if (ps != null) {
            try {
                ps.close();
            } catch (SQLException e) {
                LOGGER.error(e.getMessage());
            } finally {
                ps = null;
            }
        }

        if (conn != null) {
            try {
                conn.close();
            } catch (SQLException e) {
                LOGGER.error(e.getMessage());
            } finally {
                conn = null;
            }
        }
    }
}
