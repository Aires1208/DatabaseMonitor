package com.zte.ums.oespaas.mysql.message;

import com.zte.ums.oespaas.mysql.util.NetUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by 10183966 on 9/5/16.
 */
public class ProjectConfig {
    private static final Logger logger = LoggerFactory.getLogger(ProjectConfig.class);

    private static String localAddress = NetUtils.getLocalAddress();
    //    public static String HBASE_IP = "10.62.100.241";
    public static String HBASE_IP = System.getenv("HBase_IP") == null ? localAddress : System.getenv("HBase_IP");
    public static String HBASE_PORT = System.getenv("HBase_Port") == null ? "2181" : System.getenv("HBase_Port");

    public static String DBMONITOR_AGENT_IP = System.getenv("DBMonitor_Agent_IP") == null ? localAddress : System.getenv("DBMonitor_Agent_IP");
    public static String DBMONITOR_AGENT_PORT = System.getenv("DBMonitor_Agent_Port") == null ? "38205" : System.getenv("DBMonitor_Agent_Port");

    static {
        logger.info("HBASE_IP: " + HBASE_IP);
        logger.info("HBASE_PORT: " + HBASE_PORT);
        logger.info("DBMONITOR_AGENT_IP: " + DBMONITOR_AGENT_IP);
        logger.info("DBMONITOR_AGENT_PORT: " + DBMONITOR_AGENT_PORT);
    }
}
