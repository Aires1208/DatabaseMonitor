package com.zte.ums.oespaas.mysql.policy;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by 10183966 on 9/6/16.
 */
public class KafkaProperties {
    private static final Logger logger = LoggerFactory.getLogger(KafkaProperties.class);

    //String metadata_broker = "10.62.100.76:9092";
//    public static String kafka_ip = "10.62.100.76";
    public static String kafka_ip = System.getenv("Kafka_IP") == null ? "127.0.0.1" : System.getenv("Kafka_IP");
    public static String kafka_port = System.getenv("Kafka_Port") == null ? "9092" : System.getenv("Kafka_Port");

    //String zkConnect = "10.62.100.76:2181";
//    public static String zk_ip = "10.62.100.76";
    public static String zk_ip = System.getenv("HBase_IP") == null ? "127.0.0.1" : System.getenv("HBase_IP");
    public static String zk_port = System.getenv("HBase_Port") == null ? "2181" : System.getenv("HBase_Port");
    //String zookeeper_connectiontimeout_ms = 1000000;

    public static String groupid = "group";
    public static String topic = "policy_event";
    public static String serializer_class = "kafka.serializer.StringEncoder";

    static {
        logger.info("kafka_ip: " + kafka_ip);
        logger.info("kafka_port: " + kafka_port);
        logger.info("zk_ip: " + zk_ip);
        logger.info("zk_port: " + zk_port);
        logger.info("groupid: " + groupid);
        logger.info("topic: " + topic);
        logger.info("serializer_class: " + serializer_class);
    }
}