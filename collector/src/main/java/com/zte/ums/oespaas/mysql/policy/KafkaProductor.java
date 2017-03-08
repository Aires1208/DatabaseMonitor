package com.zte.ums.oespaas.mysql.policy;

import com.zte.ums.oespaas.mysql.bean.hbase.MonitorInfo;
import com.zte.ums.oespaas.mysql.bean.hbase.Range;
import com.zte.ums.oespaas.mysql.bean.hbase.RegisterInfo;
import com.zte.ums.oespaas.mysql.hbase.MonitorInfoService;
import com.zte.ums.oespaas.mysql.hbase.RegisterInfoService;
import com.zte.ums.oespaas.mysql.util.NetUtils;
import kafka.javaapi.producer.Producer;
import kafka.producer.KeyedMessage;
import kafka.producer.ProducerConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.Set;

/**
 * Created by 10183966 on 9/6/16.
 */

@Component
public class KafkaProductor {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private Producer<String, String> producer = null;

    @Scheduled(cron = "* 0/1 * * * *")
    public void sendPolicyEventMessage() {
        if (producer == null) {
            producer = getProducer();
            if (producer == null){
                return;
            }
        }

        long timeStamp = System.currentTimeMillis() - 1000 * 30;
        List<String> policyMessages = generateDbPolicyMessages(timeStamp);
        for (String policyMessage : policyMessages) {
            logger.info("Send: " + policyMessage);

            final KeyedMessage<String, String> keyedMessage = new KeyedMessage<String, String>(KafkaProperties.topic, policyMessage);
            producer.send(keyedMessage);
        }
    }

    private Producer<String, String> getProducer() {
        if (!NetUtils.checkPortIsUsed(KafkaProperties.zk_ip, KafkaProperties.zk_port)) {
            return null;
        }

        if (!NetUtils.checkPortIsUsed(KafkaProperties.kafka_ip, KafkaProperties.kafka_port)) {
            //logger.error("kafka is not running on " + KafkaProperties.kafka_ip);
            return null;
        }

        Properties properties = new Properties();
        properties.put("zookeeper.run", KafkaProperties.zk_ip + ":" + KafkaProperties.zk_port);
        properties.put("metadata.broker.list", KafkaProperties.kafka_ip + ":" + KafkaProperties.kafka_port);
        properties.put("serializer.class", KafkaProperties.serializer_class);
        ProducerConfig producerConfig = new ProducerConfig(properties);
        return new Producer<String, String>(producerConfig);
    }

    private List<String> generateDbPolicyMessages(long timeStamp) {
        List<String> dbPolicyMessages = new ArrayList<String>();
        Range range = new Range(timeStamp - 60 * 1000, timeStamp + 30 * 1000);
        Set<RegisterInfo> registerInfos = RegisterInfoService.getRegisterInfoList();
        for (RegisterInfo registerInfo : registerInfos) {
            String dbNeid = registerInfo.getDbNeId();
            String dbName = registerInfo.getDbName();
            List<MonitorInfo> monitorInfos = MonitorInfoService.getMonitorInfoList(dbNeid, range);
            for (MonitorInfo monitorInfo : monitorInfos) {
                if (null != monitorInfo.getStatusBean()) {
                    dbPolicyMessages.add(createPolicyMessage("db", "dbname=" + dbName, "calls", Long.parseLong(monitorInfo.getStatusBean().getQuestions()), Long.parseLong(monitorInfo.getStatusBean().getCollectTime())));
                    dbPolicyMessages.add(createPolicyMessage("db", "dbname=" + dbName, "connections", Long.parseLong(monitorInfo.getStatusBean().getThreads_running()), Long.parseLong(monitorInfo.getStatusBean().getCollectTime())));
                }
                if (null != monitorInfo.getSessionBean()) {
                    dbPolicyMessages.add(createPolicyStringMessage("db", "dbname=" + dbName, "spendtime", monitorInfo.getSessionBean().getSpeedTime(), Long.parseLong(monitorInfo.getSessionBean().getCollectTime())));
                }
            }
        }

        return dbPolicyMessages;
    }

    private String createPolicyMessage(String objecttype, String objectname, String metricname, long metricvalue, long timestamp) {
        StringBuilder stringBuilder = new StringBuilder("{");
        stringBuilder.append("\"objecttype\":").append("\"").append(objecttype).append("\"").append(",");
        stringBuilder.append("\"objectname\":").append("\"").append(objectname).append("\"").append(",");
        stringBuilder.append("\"metricname\":").append("\"").append(metricname).append("\"").append(",");
        stringBuilder.append("\"metricvalue\":").append(metricvalue).append(",");
        stringBuilder.append("\"timestamp\":").append(timestamp).append("}");
        return stringBuilder.toString();
    }

    private String createPolicyStringMessage(String objecttype, String objectname, String metricname, String metricvalue, long timestamp) {
        StringBuilder stringBuilder = new StringBuilder("{");
        stringBuilder.append("\"objecttype\":").append("\"").append(objecttype).append("\"").append(",");
        stringBuilder.append("\"objectname\":").append("\"").append(objectname).append("\"").append(",");
        stringBuilder.append("\"metricname\":").append("\"").append(metricname).append("\"").append(",");
        stringBuilder.append("\"metricvalue\":").append("\"").append(metricvalue).append("\"").append(",");
        stringBuilder.append("\"timestamp\":").append(timestamp).append("}");
        return stringBuilder.toString();
    }
}
